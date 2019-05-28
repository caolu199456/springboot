package com.example.util.common;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * excel帮助类
 *
 * @author: CL
 * @email: caolu@sunseaaiot.com
 * @date: 2019-03-14 16:23:00
 */
public class POIUtils {
    /**
     * 导出excel
     * @param response
     * @param title sheet名称
     * @param headers 字段名称数组
     * @param dataList 数据
     * @param excelName excel下载的时候显示什么名字
     */
    public static void exportExcel(HttpServletResponse response,String title, String[] headers, List<String[]> dataList, String excelName) {

        try {
            response.setContentType("application/vnd.ms-excel;charset=utf8");
            String headerValue = "attachment;";
            headerValue += " filename=\"" + encodeURIComponent(excelName) +"\";";
            headerValue += " filename*=utf-8''" + encodeURIComponent(excelName);
            response.setHeader("Content-Disposition", headerValue);
            exportExcel(title, headers, dataList,response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * <pre>
     * 符合 RFC 3986 标准的“百分号URL编码”
     * 在这个方法里，空格会被编码成%20，而不是+
     * 和浏览器的encodeURIComponent行为一致
     * </pre>
     * @param value
     * @return
     */
    private static String encodeURIComponent(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void exportExcel(String title,String[] headers,  List<String[]> dataList, OutputStream out) throws Exception {
        HSSFWorkbook hssfWorkbook = null;
        try {
            hssfWorkbook = new HSSFWorkbook();
            HSSFSheet sheet = hssfWorkbook.createSheet(title);
            sheet.setDefaultColumnWidth(20);
            sheet.setDefaultRowHeightInPoints(20);
            /*第二行*/
            HSSFRow headerRow = sheet.createRow(0);
            HSSFCellStyle headerCellStyle = hssfWorkbook.createCellStyle();
            headerCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
            Font headerFont = hssfWorkbook.createFont();
            headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
            headerCellStyle.setFont(headerFont);
            for (int i = 0; i < headers.length; i++) {
                HSSFCell headerCell = headerRow.createCell(i);
                headerCell.setCellStyle(headerCellStyle);
                headerCell.setCellValue(headers[i]);
            }
            /*数据部分*/

            if (dataList != null && dataList.size() > 0) {
                for (int i = 0; i < dataList.size(); i++) {
                    String[] itemArray = dataList.get(i);
                    HSSFRow dataRow = sheet.createRow(i + 1);
                    int j = 0;
                    for (String item : itemArray) {
                        dataRow.createCell(j).setCellValue(item == null ? "" : item);
                        j++;
                    }
                }
            }
            /*写入数据*/
            hssfWorkbook.write(out);

        } catch (Exception e) {
            throw new Exception("生成Excel错误");
        }
    }

    /**
     * 解析Excel文件到List
     * @param excleFile 要解析的文件
     * @param startRow 真实数据的开始行 从0开始数
     * @param startCol 真实数据的开始列 从0开始数
     * @return List<Map>  map的key就是列数  从0开始计数
     */
    public static List<Map> analysis(File excleFile,Integer startRow,Integer startCol) throws Exception {
        try (FileInputStream fis = new FileInputStream(excleFile)) {
            List<Map> analysis = analysis(fis, startRow, startCol);
            return analysis;
        } catch (Exception e) {
            throw new Exception("解析Excle出错");
        }
    }
    /**
     * 解析Excel文件到List
     * @param is 要解析的流如果是servlet的输入流不需要手动关闭，其他自己手动创建的流需要自己关掉，在此方法中没有做关闭处理
     * @param startRow 真实数据的开始行 从0开始数
     * @param startCol 真实数据的开始列 从0开始数
     * @return List<Map>  map的key就是列数  从0开始计数
     */
    public static List<Map> analysis(InputStream is, Integer startRow, Integer startCol) throws Exception {
        Workbook workbook = null;

        List<Map> dataMapList = new ArrayList<Map>();
        try {
            workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return null;
            }
            for(int i=startRow;i<=sheet.getLastRowNum();i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                LinkedHashMap dataMap = new LinkedHashMap();
                for (int j = startCol;j<row.getLastCellNum();j++) {
                    String cellValue = getCellValue((HSSFCell) row.getCell(j));
                    dataMap.put(j, cellValue);

                }
                dataMapList.add(dataMap);

            }
        } catch (Exception e) {
            throw new Exception("解析Excle出错");
        }finally {
        }
        return dataMapList;
    }
    private static String getCellValue(HSSFCell cell) {
        if(cell==null||"".equals(cell)){
            return "";
        }
        String cellValue = "";
        DecimalFormat df = new DecimalFormat("#");

        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                cellValue =cell.getRichStringCellValue().getString().trim();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                cellValue =df.format(cell.getNumericCellValue()).toString();
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                cellValue =String.valueOf(cell.getBooleanCellValue()).trim();
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                cellValue =cell.getCellFormula();
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                cellValue ="";
                break;
            case HSSFCell.CELL_TYPE_ERROR:
                cellValue ="";
                break;
            default:
                cellValue = "";
        }
        return cellValue;
    }
}
