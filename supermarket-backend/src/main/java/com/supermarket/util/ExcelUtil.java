package com.supermarket.util;

import com.supermarket.vo.SupplierVO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Excel导出工具类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public class ExcelUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 导出供货商数据到Excel
     *
     * @param suppliers 供货商列表
     * @param response  HTTP响应
     * @throws IOException IO异常
     */
    public static void exportSuppliersToExcel(List<SupplierVO> suppliers, HttpServletResponse response) throws IOException {
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("供货商数据");

        // 创建样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        // 创建表头
        createSupplierHeader(sheet, headerStyle);

        // 填充数据
        fillSupplierData(sheet, suppliers, dataStyle);

        // 自动调整列宽
        autoSizeColumns(sheet, 12);

        // 设置响应头
        setExcelResponseHeaders(response, "供货商数据_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");

        // 写入响应流
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 创建表头样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 设置背景色
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // 设置对齐方式
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // 设置字体
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        
        return style;
    }

    /**
     * 创建数据样式
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // 设置对齐方式
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // 设置字体
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        
        return style;
    }

    /**
     * 创建供货商表头
     */
    private static void createSupplierHeader(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "序号", "供货商名称", "联系人", "联系电话", "邮箱地址", 
            "详细地址", "信用等级", "信用等级文本", "付款条件", "供货周期(天)", 
            "状态", "状态文本", "创建时间"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * 填充供货商数据
     */
    private static void fillSupplierData(Sheet sheet, List<SupplierVO> suppliers, CellStyle dataStyle) {
        int rowNum = 1;
        for (int i = 0; i < suppliers.size(); i++) {
            SupplierVO supplier = suppliers.get(i);
            Row row = sheet.createRow(rowNum++);

            // 序号
            createCell(row, 0, i + 1, dataStyle);
            // 供货商名称
            createCell(row, 1, supplier.getSupplierName(), dataStyle);
            // 联系人
            createCell(row, 2, supplier.getContactPerson(), dataStyle);
            // 联系电话
            createCell(row, 3, supplier.getPhone(), dataStyle);
            // 邮箱地址
            createCell(row, 4, supplier.getEmail(), dataStyle);
            // 详细地址
            createCell(row, 5, supplier.getAddress(), dataStyle);
            // 信用等级
            createCell(row, 6, supplier.getCreditRating(), dataStyle);
            // 信用等级文本
            createCell(row, 7, supplier.getCreditRatingText(), dataStyle);
            // 付款条件
            createCell(row, 8, supplier.getPaymentTerms(), dataStyle);
            // 供货周期
            createCell(row, 9, supplier.getDeliveryCycle(), dataStyle);
            // 状态
            createCell(row, 10, supplier.getStatus(), dataStyle);
            // 状态文本
            createCell(row, 11, supplier.getStatusText(), dataStyle);
            // 创建时间
            createCell(row, 12, formatDateTime(supplier.getCreateTime()), dataStyle);
        }
    }

    /**
     * 创建单元格并设置值
     */
    private static void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value != null) {
            if (value instanceof String) {
                cell.setCellValue((String) value);
            } else if (value instanceof Integer) {
                cell.setCellValue((Integer) value);
            } else if (value instanceof Long) {
                cell.setCellValue((Long) value);
            } else if (value instanceof Double) {
                cell.setCellValue((Double) value);
            } else {
                cell.setCellValue(value.toString());
            }
        } else {
            cell.setCellValue("");
        }
        cell.setCellStyle(style);
    }

    /**
     * 自动调整列宽
     */
    private static void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
            // 设置最大宽度，避免列过宽
            int columnWidth = sheet.getColumnWidth(i);
            if (columnWidth > 8000) {
                sheet.setColumnWidth(i, 8000);
            }
        }
    }

    /**
     * 设置Excel响应头
     */
    private static void setExcelResponseHeaders(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }

    /**
     * 格式化日期时间
     */
    private static String formatDateTime(Object dateTime) {
        if (dateTime == null) {
            return "";
        }
        
        try {
            // 处理后端返回的数组格式 [2025, 7, 18, 11, 24, 8]
            if (dateTime instanceof int[]) {
                int[] dateArray = (int[]) dateTime;
                if (dateArray.length >= 6) {
                    LocalDateTime localDateTime = LocalDateTime.of(
                        dateArray[0], dateArray[1], dateArray[2],
                        dateArray[3], dateArray[4], dateArray[5]
                    );
                    return localDateTime.format(DATE_FORMATTER);
                }
            }
            
            // 处理字符串格式
            if (dateTime instanceof String) {
                return (String) dateTime;
            }
            
            return dateTime.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
