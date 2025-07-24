package com.supermarket.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.dto.HeldTransactionDTO;
import com.supermarket.dto.PaymentDTO;
import com.supermarket.entity.*;
import com.supermarket.mapper.*;
import com.supermarket.service.CashierService;
import com.supermarket.service.FinanceRecordService;
import com.supermarket.utils.UserContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 收银台服务实现类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CashierServiceImpl implements CashierService {

    private final SaleOrderMapper saleOrderMapper;
    private final SaleOrderItemMapper saleOrderItemMapper;
    private final HeldTransactionMapper heldTransactionMapper;
    private final ProductMapper productMapper;
    private final StockRecordMapper stockRecordMapper;
    private final SysUserMapper sysUserMapper;
    private final ObjectMapper objectMapper;
    private final UserContextUtils userContextUtils;
    private final FinanceRecordService financeRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String processPayment(PaymentDTO paymentDTO, Long operatorId) {
        try {
            // 1. 智能验证并更新商品信息
            ValidationResult validationResult = validateAndUpdateItems(paymentDTO.getItems());
            List<PaymentDTO.CartItemDTO> validatedItems = validationResult.getValidatedItems();

            // 检查是否还有有效商品
            if (validatedItems.isEmpty()) {
                throw new RuntimeException("购物车中没有有效的商品可以支付");
            }

            // 如果有变化，记录日志但继续处理
            if (validationResult.hasChanges()) {
                log.warn("支付时发现商品变化：{}", String.join("; ", validationResult.getChangeMessages()));
            }

            // 更新支付DTO中的商品列表为验证后的商品
            paymentDTO.setItems(validatedItems);

            // 重新计算总金额
            BigDecimal newTotalAmount = calculateTotalAmount(validatedItems);
            paymentDTO.setTotalAmount(newTotalAmount);

            // 2. 获取操作员信息
            SysUser operator = sysUserMapper.selectById(operatorId);
            if (operator == null) {
                throw new RuntimeException("操作员不存在");
            }

            // 3. 生成订单号
            String orderNo = generateOrderNo();

            // 4. 创建销售订单
            SaleOrder saleOrder = createSaleOrder(paymentDTO, operatorId, operator.getRealName(), orderNo);
            saleOrderMapper.insert(saleOrder);

            // 5. 处理库存出库并创建订单明细
            List<SaleOrderItem> orderItems = processStockOutAndCreateItems(saleOrder.getId(), validatedItems, operatorId, orderNo);
            saleOrderItemMapper.insertBatch(orderItems);

            // 6. 记录财务收入
            recordSalesIncome(saleOrder, operator);

            log.info("支付处理成功，订单号：{}，原始金额：{}，实际金额：{}",
                orderNo, paymentDTO.getTotalAmount(), newTotalAmount);
            return orderNo;

        } catch (Exception e) {
            log.error("支付处理失败：{}", e.getMessage(), e);
            throw new RuntimeException("支付处理失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveHeldTransaction(HeldTransactionDTO heldTransactionDTO, Long operatorId) {
        try {
            // 1. 获取操作员信息
            SysUser operator = sysUserMapper.selectById(operatorId);
            if (operator == null) {
                throw new RuntimeException("操作员不存在");
            }

            // 2. 生成挂单号
            String transactionNo = generateTransactionNo();

            // 3. 将商品数据序列化为JSON
            String itemsJson = objectMapper.writeValueAsString(heldTransactionDTO.getItems());

            // 4. 创建挂单记录
            HeldTransaction heldTransaction = new HeldTransaction();
            heldTransaction.setTransactionNo(transactionNo);
            heldTransaction.setItemsJson(itemsJson);
            heldTransaction.setItemCount(heldTransactionDTO.getItemCount());
            heldTransaction.setTotalAmount(heldTransactionDTO.getTotalAmount());
            heldTransaction.setCashierId(operatorId);
            heldTransaction.setCashierName(operator.getRealName());
            heldTransaction.setTerminalId(heldTransactionDTO.getTerminalId());
            heldTransaction.setStatus(1); // 1-挂起状态

            // 5. 保存到数据库
            heldTransactionMapper.insert(heldTransaction);

            log.info("挂单保存成功，挂单号：{}", transactionNo);
            return transactionNo;

        } catch (Exception e) {
            log.error("挂单保存失败：{}", e.getMessage(), e);
            throw new RuntimeException("挂单保存失败：" + e.getMessage());
        }
    }

    @Override
    public List<Object> getHeldTransactions(Long cashierId, String terminalId) {
        try {
            List<HeldTransaction> heldTransactions = heldTransactionMapper.selectByCashierAndTerminal(cashierId, terminalId);
            List<Object> result = new ArrayList<>();

            for (HeldTransaction transaction : heldTransactions) {
                // 构建返回对象
                java.util.Map<String, Object> transactionData = new java.util.HashMap<>();
                transactionData.put("id", transaction.getTransactionNo());
                transactionData.put("itemCount", transaction.getItemCount());
                transactionData.put("totalAmount", transaction.getTotalAmount());
                transactionData.put("cashierName", transaction.getCashierName());
                transactionData.put("createTime", transaction.getCreateTime());
                result.add(transactionData);
            }

            return result;
        } catch (Exception e) {
            log.error("获取挂单列表失败：{}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Object resumeHeldTransaction(String transactionId) {
        try {
            // 1. 根据挂单号查询挂单数据
            HeldTransaction heldTransaction = heldTransactionMapper.selectByTransactionNo(transactionId);
            if (heldTransaction == null) {
                throw new RuntimeException("挂单不存在");
            }

            // 2. 反序列化商品数据
            String itemsJson = heldTransaction.getItemsJson();
            List<PaymentDTO.CartItemDTO> items = objectMapper.readValue(itemsJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, PaymentDTO.CartItemDTO.class));

            // 3. 验证并更新商品信息（新增）
            ValidationResult validationResult = validateAndUpdateItems(items);
            List<PaymentDTO.CartItemDTO> validatedItems = validationResult.getValidatedItems();

            // 4. 更新挂单状态为已取单
            heldTransaction.setStatus(2); // 2-已取单
            heldTransactionMapper.updateById(heldTransaction);

            // 5. 重新计算总金额（因为价格可能变化）
            BigDecimal newTotalAmount = calculateTotalAmount(validatedItems);
            int newItemCount = validatedItems.stream().mapToInt(PaymentDTO.CartItemDTO::getQuantity).sum();

            // 6. 构建返回数据
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("items", validatedItems);
            result.put("itemCount", newItemCount);
            result.put("totalAmount", newTotalAmount);
            result.put("originalAmount", heldTransaction.getTotalAmount()); // 原始金额，用于前端提示
            result.put("hasChanges", validationResult.hasChanges()); // 是否有变化
            result.put("changeMessages", validationResult.getChangeMessages()); // 变化信息

            log.info("恢复挂单成功，挂单号：{}，原始金额：{}，当前金额：{}",
                transactionId, heldTransaction.getTotalAmount(), newTotalAmount);

            // 记录变化信息
            if (validationResult.hasChanges()) {
                log.warn("挂单恢复时发现变化：{}", String.join("; ", validationResult.getChangeMessages()));
            }

            return result;

        } catch (Exception e) {
            log.error("恢复挂单失败：{}", e.getMessage(), e);
            throw new RuntimeException("恢复挂单失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHeldTransaction(String transactionId) {
        try {
            HeldTransaction heldTransaction = heldTransactionMapper.selectByTransactionNo(transactionId);
            if (heldTransaction != null) {
                heldTransactionMapper.deleteById(heldTransaction.getId());
                log.info("删除挂单成功，挂单号：{}", transactionId);
            }
        } catch (Exception e) {
            log.error("删除挂单失败：{}", e.getMessage(), e);
            throw new RuntimeException("删除挂单失败：" + e.getMessage());
        }
    }

    /**
     * 严格验证商品库存（已废弃，使用validateAndUpdateItems替代）
     * 保留此方法用于特殊场景的严格验证
     */
    @Deprecated
    private void validateStockStrict(List<PaymentDTO.CartItemDTO> items) {
        for (PaymentDTO.CartItemDTO item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在：" + item.getProductName());
            }

            // 检查商品库存
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("商品库存不足：" + item.getProductName() +
                    "，当前库存：" + product.getStockQuantity() + "，需要：" + item.getQuantity());
            }
        }
    }

    /**
     * 验证并更新商品信息
     */
    private ValidationResult validateAndUpdateItems(List<PaymentDTO.CartItemDTO> items) {
        List<PaymentDTO.CartItemDTO> validatedItems = new ArrayList<>();
        List<String> changeMessages = new ArrayList<>();
        boolean hasChanges = false;

        for (PaymentDTO.CartItemDTO item : items) {
            // 重新查询商品信息
            Product product = productMapper.selectById(item.getProductId());

            if (product == null || product.getStatus() != 1) {
                // 商品不存在或已停用，记录并跳过
                changeMessages.add("移除商品：" + item.getProductName() + "（商品不存在或已停用）");
                hasChanges = true;
                continue;
            }

            // 检查库存是否充足
            if (product.getStockQuantity() < item.getQuantity()) {
                // 库存不足，调整数量为可用库存
                if (product.getStockQuantity() > 0) {
                    changeMessages.add(String.format("调整商品：%s，数量从%d调整为%d（库存不足）",
                        item.getProductName(), item.getQuantity(), product.getStockQuantity()));
                    item.setQuantity(product.getStockQuantity());
                    hasChanges = true;
                } else {
                    // 无库存，移除商品
                    changeMessages.add("移除商品：" + item.getProductName() + "（无库存）");
                    hasChanges = true;
                    continue;
                }
            }

            // 更新商品信息（价格可能变化）
            if (!item.getSellingPrice().equals(product.getSellingPrice())) {
                changeMessages.add(String.format("价格调整：%s，从%.2f调整为%.2f",
                    item.getProductName(), item.getSellingPrice(), product.getSellingPrice()));
                item.setSellingPrice(product.getSellingPrice());
                hasChanges = true;
            }

            // 更新其他可能变化的信息
            if (!item.getProductName().equals(product.getProductName())) {
                changeMessages.add(String.format("商品名称更新：%s → %s",
                    item.getProductName(), product.getProductName()));
                item.setProductName(product.getProductName());
                hasChanges = true;
            }

            if (!item.getBarcode().equals(product.getBarcode())) {
                item.setBarcode(product.getBarcode());
            }

            validatedItems.add(item);
        }

        return new ValidationResult(validatedItems, hasChanges, changeMessages);
    }

    /**
     * 计算总金额
     */
    private BigDecimal calculateTotalAmount(List<PaymentDTO.CartItemDTO> items) {
        return items.stream()
            .map(item -> item.getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 验证结果内部类
     */
    private static class ValidationResult {
        private final List<PaymentDTO.CartItemDTO> validatedItems;
        private final boolean hasChanges;
        private final List<String> changeMessages;

        public ValidationResult(List<PaymentDTO.CartItemDTO> validatedItems, boolean hasChanges, List<String> changeMessages) {
            this.validatedItems = validatedItems;
            this.hasChanges = hasChanges;
            this.changeMessages = changeMessages;
        }

        public List<PaymentDTO.CartItemDTO> getValidatedItems() {
            return validatedItems;
        }

        public boolean hasChanges() {
            return hasChanges;
        }

        public List<String> getChangeMessages() {
            return changeMessages;
        }
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "SO" + timestamp + String.format("%04d", (int)(Math.random() * 10000));
    }

    /**
     * 生成挂单号
     */
    private String generateTransactionNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "HT" + timestamp + String.format("%04d", (int)(Math.random() * 10000));
    }

    /**
     * 创建销售订单
     */
    private SaleOrder createSaleOrder(PaymentDTO paymentDTO, Long operatorId, String operatorName, String orderNo) {
        LocalDateTime now = LocalDateTime.now();

        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setOrderNo(orderNo);
        saleOrder.setTotalAmount(paymentDTO.getTotalAmount());
        saleOrder.setReceivedAmount(paymentDTO.getReceivedAmount());
        saleOrder.setChangeAmount(paymentDTO.getChangeAmount());
        saleOrder.setPaymentMethod(paymentDTO.getPaymentMethod());
        saleOrder.setTotalQuantity(paymentDTO.getItems().stream().mapToInt(PaymentDTO.CartItemDTO::getQuantity).sum());
        saleOrder.setCashierId(operatorId);
        saleOrder.setCashierName(operatorName);
        saleOrder.setTerminalId(paymentDTO.getTerminalId());
        saleOrder.setStatus(1); // 1-已完成

        // 显式设置时间字段（虽然MyBatis Plus会自动填充，但为了确保数据正确性）
        saleOrder.setCreateTime(now);
        saleOrder.setUpdateTime(now);

        return saleOrder;
    }

    /**
     * 记录销售收入到财务记录
     */
    private void recordSalesIncome(SaleOrder saleOrder, SysUser operator) {
        try {
            String description = String.format("销售收入 - 订单号：%s，支付方式：%s",
                saleOrder.getOrderNo(), getPaymentMethodText(saleOrder.getPaymentMethod()));

            financeRecordService.recordSalesIncome(
                saleOrder.getTotalAmount(),
                saleOrder.getOrderNo(),
                saleOrder.getId(),
                description,
                operator.getId(),
                operator.getRealName()
            );

            log.info("记录销售收入成功，订单号：{}，金额：{}", saleOrder.getOrderNo(), saleOrder.getTotalAmount());
        } catch (Exception e) {
            log.error("记录销售收入失败，订单号：{}，金额：{}", saleOrder.getOrderNo(), saleOrder.getTotalAmount(), e);
            // 不抛出异常，避免影响主要的支付流程
        }
    }

    /**
     * 获取支付方式文本
     */
    private String getPaymentMethodText(String paymentMethod) {
        switch (paymentMethod) {
            case "cash": return "现金";
            case "alipay": return "支付宝";
            case "wechat": return "微信";
            case "card": return "银行卡";
            default: return "其他";
        }
    }

    /**
     * 处理库存出库并创建订单明细
     */
    private List<SaleOrderItem> processStockOutAndCreateItems(Long orderId, List<PaymentDTO.CartItemDTO> items, Long operatorId, String orderNo) {
        List<SaleOrderItem> orderItems = new ArrayList<>();

        for (PaymentDTO.CartItemDTO item : items) {
            Product product = productMapper.selectById(item.getProductId());

            // 创建订单明细
            SaleOrderItem orderItem = new SaleOrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(item.getProductId());
            orderItem.setProductName(item.getProductName());
            orderItem.setBarcode(item.getBarcode());
            orderItem.setSellingPrice(item.getSellingPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnit(item.getUnit());
            orderItem.setSubtotal(item.getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            orderItems.add(orderItem);

            // 更新库存
            updateProductStock(product, item.getQuantity(), operatorId, orderNo);
        }

        return orderItems;
    }

    /**
     * 更新商品库存
     */
    private void updateProductStock(Product product, Integer quantity, Long operatorId, String orderNo) {
        // 更新商品库存
        int newStock = product.getStockQuantity() - quantity;
        product.setStockQuantity(newStock);
        productMapper.updateById(product);

        // 记录库存变动
        StockRecord stockRecord = new StockRecord();
        stockRecord.setProductId(product.getId());
        stockRecord.setChangeType(2); // 出库
        stockRecord.setChangeQuantity(-quantity);
        stockRecord.setBeforeQuantity(product.getStockQuantity() + quantity);
        stockRecord.setAfterQuantity(newStock);
        stockRecord.setReason("销售出库 - 订单号：" + orderNo);
        stockRecord.setOperatorId(operatorId);
        stockRecordMapper.insert(stockRecord);
    }








    @Override
    public String processPayment(PaymentDTO paymentDTO) {
        Long operatorId = userContextUtils.requireCurrentUserId();
        return processPayment(paymentDTO, operatorId);
    }

    @Override
    public String saveHeldTransaction(HeldTransactionDTO heldTransactionDTO) {
        Long operatorId = userContextUtils.requireCurrentUserId();
        return saveHeldTransaction(heldTransactionDTO, operatorId);
    }

}
