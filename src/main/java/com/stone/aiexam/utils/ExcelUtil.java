package com.stone.aiexam.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.stone.aiexam.dto.QuestionImportDTO;
import com.stone.aiexam.exception.BusinessException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.util.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.web.multipart.MultipartFile;

public class ExcelUtil {

    private ExcelUtil() {}

    /**
     * 生成题目模板
     * @return
     */
    public static byte[] createQuestionTemplate(){
        //1. 创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        //2. 创建sheet
        XSSFSheet sheet = workbook.createSheet("题目导入模板");

        //3. 定义列宽（单位：字符宽度 * 256）
        int[] columnWidths = {
                40 * 256,  // 题目内容 - 最宽
                15 * 256,  // 题目类型
                12 * 256,  // 是否多选
                12 * 256,  // 分类ID
                12 * 256,  // 难度
                10 * 256,  // 分值
                20 * 256,  // 选项A
                20 * 256,  // 选项B
                20 * 256,  // 选项C
                20 * 256,  // 选项D
                15 * 256,  // 正确答案
                40 * 256   // 解析 - 较宽
        };

        for (int i = 0; i < columnWidths.length; i++) {
            sheet.setColumnWidth(i, columnWidths[i]);
        }

        //4. 创建样式
        // 表头样式
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // 数据行样式 - 普通
        XSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.LEFT);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setWrapText(true);

        // 数据行样式 - 交替行（浅灰色）
        XSSFCellStyle altDataStyle = workbook.createCellStyle();
        altDataStyle.cloneStyleFrom(dataStyle);
        altDataStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        altDataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 示例说明样式
        XSSFCellStyle noteStyle = workbook.createCellStyle();
        XSSFFont noteFont = workbook.createFont();
        noteFont.setColor(IndexedColors.RED.getIndex());
        noteFont.setItalic(true);
        noteStyle.setFont(noteFont);
        noteStyle.setWrapText(true);

        //5. 创建表头
        XSSFRow headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(25);
        String[] headers = {
                "题目内容", "题目类型", "是否多选", "分类ID", "难度", "分值",
                "选项A", "选项B", "选项C", "选项D", "正确答案", "解析"
        };
        for(int i = 0; i < headers.length; i++){
            headerRow.createCell(i).setCellValue(headers[i]);
            headerRow.getCell(i).setCellStyle(headerStyle);
        }

        //6. 填充样例数据
        XSSFRow sampleRow = sheet.createRow(1);
        sampleRow.setHeightInPoints(20);
        String[] sampleData = {
                "以下哪个是Java的基本数据类型？", "CHOICE", "否", "1", "EASY", "2",
                "String", "int", "List", "Map", "B", "int是Java的8种基本数据类型之一"
        };
        for(int i = 0; i < sampleData.length; i++){
            sampleRow.createCell(i).setCellValue(sampleData[i]);
            sampleRow.getCell(i).setCellStyle(dataStyle);
        }

        //7. 添加说明行
        XSSFRow noteRow = sheet.createRow(2);
        noteRow.setHeightInPoints(30);
        String note = "说明：题目类型可选值：CHOICE(选择题)、JUDGE(判断题)、TEXT(文本题)；难度可选值：EASY、MEDIUM、HARD；判断题答案：TRUE,FALSE";
        noteRow.createCell(0).setCellValue(note);
        noteRow.getCell(0).setCellStyle(noteStyle);

        // 合并说明行的单元格
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, headers.length - 1));

        //8. 返回字节数组
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("生成Excel模板失败", e);
        }
    }


    /**
     * 解析题目模板
     * @param file
     * @return
     * @throws IOException
     */
    public static List<QuestionImportDTO> parseQuestionTemplate (MultipartFile file) throws IOException{
        List<QuestionImportDTO> importDTOs = new ArrayList<>();

        //1. 获取工作簿，确定是2007+版本还是2003版本
        Workbook workbook = null;
        InputStream inputStream = file.getInputStream();
        try{
            String filename = file.getOriginalFilename();
            if(filename!=null && filename.endsWith(".xlsx")){
                workbook = new XSSFWorkbook(inputStream);
            }else if(filename!=null && filename.endsWith(".xls")){
                workbook = new HSSFWorkbook(inputStream);
            }else{
                throw new BusinessException("文件格式错误，只能上传.xlsx或.xls文件");
            }


            //2. 获取工作簿中的第一个工作表
            Sheet sheet = workbook.getSheetAt(0);

            //3. 读取工作表的信息转换成QuestionImportDTO对象
            //第一行是表头，跳过，最后一行是说明，跳过
            for(int i=1; i<= sheet.getLastRowNum() -1 ;i++){
                QuestionImportDTO importDTO = new QuestionImportDTO();
                Row row = sheet.getRow(i);
                if(row==null){
                    continue;
                }
                //题目内容
                importDTO.setTitle(getCellValue(row.getCell(0)));
                //题目类型
                importDTO.setType(getCellValue(row.getCell(1)));
                //是否多选
                String multiValue = getCellValue(row.getCell(2));
                importDTO.setMulti("true".equalsIgnoreCase(multiValue)||"是".equals(multiValue));
                //分类ID
                String categoryIdValue = getCellValue(row.getCell(3));
                if(StringUtils.hasText(categoryIdValue)){
                    try {
                        importDTO.setCategoryId(Long.parseLong(categoryIdValue));
                    } catch (NumberFormatException e) {
                        importDTO.setCategoryId(1L);// 默认分类ID为1
                    }
                }
                //难度
                importDTO.setDifficulty(getCellValue(row.getCell(4)));
                //分值
                String scoreValue = getCellValue(row.getCell(5));
                if(StringUtils.hasText(scoreValue)){
                    try {
                        importDTO.setScore(Integer.parseInt(scoreValue));
                    } catch (NumberFormatException e) {
                        importDTO.setScore(5); //默认分值为5
                    }
                }
                //选项、答案
                if("CHOICE".equals(importDTO.getType())){
                    List<QuestionImportDTO.ChoiceImportDTO> choices= new ArrayList<>();

                    String answer = getCellValue(row.getCell(10));
                    for(int j=0;j<4;j++){
                        String content = getCellValue(row.getCell(6 + j));
                        if(StringUtils.hasText(content)){
                            QuestionImportDTO.ChoiceImportDTO choice = new QuestionImportDTO.ChoiceImportDTO();
                            choice.setContent(content);
                            choice.setSort(j+1);

                            // 判断是否为正确答案
                            char optionLabel = (char) ('A' + j);
                            boolean isCorrect = answer != null && answer.contains(String.valueOf(optionLabel));
                            choice.setIsCorrect(isCorrect);

                            choices.add(choice);
                        }
                    }
                    importDTO.setChoices(choices);
                }else{
                    importDTO.setAnswer(getCellValue(row.getCell(10)));
                }

                //解析
                importDTO.setAnalysis(getCellValue(row.getCell(11)));

                if (StringUtils.hasText(importDTO.getTitle()) && StringUtils.hasText(importDTO.getType())) {
                    importDTOs.add(importDTO);
                }
            }
        }finally{
            inputStream.close();
            if(workbook!=null) {
                workbook.close();
            }
        }

        return importDTOs;
    }

    /**
     * 获取单元格的字符串值
     * @param cell
     * @return
     */
    private static String getCellValue(Cell cell){
        if (cell == null) {
            return null;
        }
        switch(cell.getCellType()){
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue();
                }
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                double val = cell.getNumericCellValue();
                return val == Math.floor(val) && !Double.isInfinite(val)
                        ? String.valueOf((long) val)
                        : String.valueOf(val);
            default:
                return null;
        }
    }


}