package com.supermarket.controller;

import com.supermarket.common.Result;
import com.supermarket.dto.PaymentDTO;
import com.supermarket.dto.HeldTransactionDTO;
import com.supermarket.entity.Product;
import com.supermarket.entity.ProductCategory;
import com.supermarket.entity.SysUser;
import com.supermarket.mapper.SysUserMapper;
import com.supermarket.service.ProductService;
import com.supermarket.service.ProductCategoryService;
import com.supermarket.service.CashierService;
import com.supermarket.utils.JwtUtils;
import com.supermarket.utils.UserContextUtils;
import com.supermarket.vo.CashierInfoVO;
import com.supermarket.vo.ProductVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 收银台控制器
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Tag(name = "收银台管理", description = "收银台相关接口")
@RestController
@RequestMapping("/cashier")
@RequiredArgsConstructor
public class CashierController {

    private final ProductService productService;
    private final ProductCategoryService categoryService;
    private final CashierService cashierService;
    private final JwtUtils jwtUtils;
    private final SysUserMapper sysUserMapper;
    private final UserContextUtils userContextUtils;

    @Operation(summary = "根据条码查询商品")
    @GetMapping("/product")
    public Result<ProductVO> getProductByBarcode(@RequestParam String barcode) {
        try {
            ProductVO product = productService.getProductVOByBarcode(barcode);
            if (product != null) {
                return Result.success(product);
            } else {
                return Result.error("商品不存在或已下架");
            }
        } catch (Exception e) {
            return Result.error("查询商品失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取商品列表")
    @GetMapping("/products")
    public Result<Page<ProductVO>> getProductList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            
            // 只查询启用的商品
            queryWrapper.eq("status", 1);
            
            // 关键词搜索
            if (StringUtils.hasText(keyword)) {
                queryWrapper.and(wrapper -> wrapper
                    .like("product_name", keyword)
                    .or()
                    .like("barcode", keyword)
                );
            }
            
            // 分类筛选
            if (categoryId != null) {
                queryWrapper.eq("category_id", categoryId);
            }
            
            // 按创建时间倒序
            queryWrapper.orderByDesc("create_time");
            
            Page<Product> page = new Page<>(pageNum, pageSize);
            Page<Product> productPage = productService.page(page, queryWrapper);
            
            // 转换为VO
            Page<ProductVO> result = new Page<>();
            result.setCurrent(productPage.getCurrent());
            result.setSize(productPage.getSize());
            result.setTotal(productPage.getTotal());
            result.setPages(productPage.getPages());
            
            List<ProductVO> productVOList = productPage.getRecords().stream()
                .map(productService::convertToProductVO)
                .toList();
            result.setRecords(productVOList);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取商品列表失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取商品分类列表")
    @GetMapping("/categories")
    public Result<List<ProductCategory>> getCategoryList() {
        try {
            QueryWrapper<ProductCategory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", 1);
            queryWrapper.orderBy(true, true, "sort_order", "id");
            
            List<ProductCategory> categories = categoryService.list(queryWrapper);
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error("获取分类列表失败：" + e.getMessage());
        }
    }

    @Operation(summary = "处理支付")
    @PostMapping("/payment")
    public Result<String> processPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        try {
            String result = cashierService.processPayment(paymentDTO);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("支付处理失败：" + e.getMessage());
        }
    }

    @Operation(summary = "保存挂单")
    @PostMapping("/hold")
    public Result<String> saveHeldTransaction(@Valid @RequestBody HeldTransactionDTO heldTransactionDTO) {
        try {
            String transactionId = cashierService.saveHeldTransaction(heldTransactionDTO);
            return Result.success(transactionId);
        } catch (Exception e) {
            return Result.error("保存挂单失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取挂单列表")
    @GetMapping("/held-transactions")
    public Result<List<Object>> getHeldTransactions(@RequestParam Long cashierId,
                                                   @RequestParam String terminalId) {
        try {
            List<Object> transactions = cashierService.getHeldTransactions(cashierId, terminalId);
            return Result.success(transactions);
        } catch (Exception e) {
            return Result.error("获取挂单列表失败：" + e.getMessage());
        }
    }

    @Operation(summary = "恢复挂单")
    @PostMapping("/resume/{transactionId}")
    public Result<Object> resumeHeldTransaction(@PathVariable String transactionId) {
        try {
            Object transaction = cashierService.resumeHeldTransaction(transactionId);
            return Result.success(transaction);
        } catch (Exception e) {
            return Result.error("恢复挂单失败：" + e.getMessage());
        }
    }

    @Operation(summary = "删除挂单")
    @DeleteMapping("/held-transaction/{transactionId}")
    public Result<String> deleteHeldTransaction(@PathVariable String transactionId) {
        try {
            cashierService.deleteHeldTransaction(transactionId);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除挂单失败：" + e.getMessage());
        }
    }

    @Operation(summary = "开钱箱")
    @PostMapping("/open-drawer")
    public Result<String> openCashDrawer(@RequestBody Object request) {
        try {
            // TODO: 实现开钱箱功能
            return Result.success("钱箱已打开");
        } catch (Exception e) {
            return Result.error("开钱箱失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取收银员信息")
    @GetMapping("/info")
    public Result<CashierInfoVO> getCashierInfo() {
        try {
            SysUser user = userContextUtils.getCurrentUser();
            if (user == null) {
                return Result.error("用户未登录或登录已过期");
            }

            CashierInfoVO cashierInfo = new CashierInfoVO();
            cashierInfo.setId(user.getId());
            cashierInfo.setUsername(user.getUsername());
            cashierInfo.setRealName(user.getRealName());
            cashierInfo.setPhone(user.getPhone());
            cashierInfo.setEmail(user.getEmail());
            cashierInfo.setStatus(user.getStatus());

            return Result.success(cashierInfo);
        } catch (Exception e) {
            return Result.error("获取收银员信息失败：" + e.getMessage());
        }
    }

    /**
     * 从Token中获取用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            // 验证Token有效性
            if (!jwtUtils.validateToken(token)) {
                throw new RuntimeException("访问令牌无效或已过期");
            }

            String username = jwtUtils.getUsernameFromToken(token);
            if (username == null) {
                throw new RuntimeException("无法从令牌中获取用户信息");
            }

            // 从数据库查询用户信息获取真实的用户ID
            SysUser user = sysUserMapper.selectByUsername(username);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }

            if (user.getStatus() != 1) {
                throw new RuntimeException("用户已被禁用");
            }

            return user.getId();
        }
        throw new RuntimeException("未提供有效的访问令牌");
    }
}
