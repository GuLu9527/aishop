package com.supermarket.service;

import com.supermarket.dto.PaymentDTO;
import com.supermarket.dto.HeldTransactionDTO;
import java.util.List;

/**
 * 收银台服务接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public interface CashierService {

    /**
     * 处理支付
     *
     * @param paymentDTO 支付信息
     * @param operatorId 操作员ID
     * @return 订单号
     */
    String processPayment(PaymentDTO paymentDTO, Long operatorId);

    /**
     * 保存挂单
     *
     * @param heldTransactionDTO 挂单信息
     * @param operatorId 操作员ID
     * @return 挂单ID
     */
    String saveHeldTransaction(HeldTransactionDTO heldTransactionDTO, Long operatorId);

    /**
     * 获取挂单列表
     *
     * @param cashierId 收银员ID
     * @param terminalId 终端ID
     * @return 挂单列表
     */
    List<Object> getHeldTransactions(Long cashierId, String terminalId);

    /**
     * 恢复挂单
     *
     * @param transactionId 挂单ID
     * @return 挂单数据
     */
    Object resumeHeldTransaction(String transactionId);

    /**
     * 删除挂单
     *
     * @param transactionId 挂单ID
     */
    void deleteHeldTransaction(String transactionId);

    /**
     * 处理支付（自动获取当前用户信息）
     *
     * @param paymentDTO 支付信息
     * @return 订单号
     */
    String processPayment(PaymentDTO paymentDTO);

    /**
     * 保存挂单（自动获取当前用户信息）
     *
     * @param heldTransactionDTO 挂单信息
     * @return 挂单ID
     */
    String saveHeldTransaction(HeldTransactionDTO heldTransactionDTO);

}
