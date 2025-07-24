package com.supermarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 供货商查询数据传输对象
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@Schema(description = "供货商查询数据传输对象")
public class SupplierQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "供货商名称（模糊查询）")
    private String supplierName;

    @Schema(description = "联系人（模糊查询）")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "信用等级")
    private Integer creditRating;

    @Schema(description = "状态：1-正常，0-停用")
    private Integer status;

    @Schema(description = "创建时间开始")
    private String createTimeStart;

    @Schema(description = "创建时间结束")
    private String createTimeEnd;
}
