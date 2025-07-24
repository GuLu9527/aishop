package com.supermarket.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 供货商数据传输对象
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@Schema(description = "供货商数据传输对象")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierDTO {

    @Schema(description = "供货商ID")
    private Long id;

    @Schema(description = "供货商名称", required = true)
    @NotBlank(message = "供货商名称不能为空")
    @Size(max = 100, message = "供货商名称长度不能超过100个字符")
    private String supplierName;

    @Schema(description = "联系人")
    @Size(max = 50, message = "联系人姓名长度不能超过50个字符")
    private String contactPerson;

    @Schema(description = "联系电话")
    @Pattern(regexp = "^[1][3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", message = "请输入正确的手机号或固定电话")
    private String phone;

    @Schema(description = "地址")
    @Size(max = 200, message = "地址长度不能超过200个字符")
    private String address;

    @Schema(description = "邮箱")
    @Email(message = "请输入正确的邮箱格式")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @Schema(description = "信用等级：1-5星")
    @Min(value = 1, message = "信用等级最低为1星")
    @Max(value = 5, message = "信用等级最高为5星")
    private Integer creditRating;

    @Schema(description = "付款条件")
    @Size(max = 100, message = "付款条件长度不能超过100个字符")
    private String paymentTerms;

    @Schema(description = "供货周期（天）")
    @Min(value = 1, message = "供货周期至少为1天")
    @Max(value = 365, message = "供货周期不能超过365天")
    private Integer deliveryCycle;

    @Schema(description = "状态：1-正常，0-停用")
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值错误")
    @Max(value = 1, message = "状态值错误")
    private Integer status;
}
