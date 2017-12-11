package net.evecom.excel.parsing;

import net.evecom.excel.ExcelDefinitionReader;
import net.evecom.excel.config.ExcelDefinition;
import net.evecom.excel.config.FieldValue;
import net.evecom.excel.constant.CommonConstants;
import net.evecom.excel.constant.ExportTypeEnum;
import net.evecom.excel.constant.OperateValidEnum;
import net.evecom.excel.exception.ExcelException;
import net.evecom.excel.result.ExcelExportResult;
import net.evecom.util.ReflectUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.TypeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel导出实现类
 * @author lisuo Cai 
 * @created 2017 /11/30 09:17:49
 */
public class ExcelExport extends AbstractExcelResolver {

    public ExcelExport(ExcelDefinitionReader definitionReader) {
        super(definitionReader);
    }

    /**
     * 默认的文本样式
     */
    private CellStyle textStyle = null;

    /**
     * 创建导出Excel,如果集合没有数据,返回null
     * @param id	 ExcelXML配置Bean的ID
     * @param beans  ExcelXML配置的bean集合
     * @param header Excel头信息(在标题之前)
     * @param fields 指定导出的字段
     * @return
     * @throws Exception
     */
    public ExcelExportResult createExcel(String id, List<?> beans, ExcelHeader header, List<String> fields)
            throws Exception {
        ExcelExportResult exportResult = null;
        if (CollectionUtils.isNotEmpty(beans)) {
            //从注册信息中获取Bean信息
            ExcelDefinition excelDefinition = definitionReader.getRegistry().get(id);
            if (excelDefinition == null) {
                throw new ExcelException("没有找到 [" + id + "] 的配置信息");
            }
            //实际传入的bean类型
            Class<?> realClass = beans.get(0).getClass();
            //传入的类型是excel配置class的类型,或者是它的子类,直接进行生成
            if (realClass == excelDefinition.getClazz()
                    || TypeUtils.isAssignable(excelDefinition.getClazz(), realClass)) {
                //导出指定字段的标题不是null,动态创建,Excel定义
                excelDefinition = dynamicCreateExcelDefinition(excelDefinition, fields);
            }
            //传入的类型是excel配置class的类型的父类,那么进行向上转型,只获取配置中父类存在的属性
            else if (TypeUtils.isAssignable(realClass, excelDefinition.getClazz())) {
                excelDefinition = extractSuperClassFields(excelDefinition, fields, realClass);
            } else {
                //判断传入的集合与配置文件中的类型拥有共同的父类,如果有则向上转型
                Object superClass = ReflectUtil.getEqSuperClass(realClass, excelDefinition.getClazz());
                if (superClass != Object.class) {
                    excelDefinition = extractSuperClassFields(excelDefinition, fields, realClass);
                } else {
                    throw new ExcelException("传入的参数类型是:" + beans.get(0).getClass().getName() + "但是 配置文件的类型是: "
                            + excelDefinition.getClazz().getName() + ",参数既不是父类,也不是其相同父类下的子类,无法完成转换");
                }

            }
            exportResult = doCreateExcel(excelDefinition, beans, header, ExportTypeEnum.EXPORT_DATA);
        }
        return exportResult;
    }

    /**
     * 创建导入错误信息
     * @param id
     * @param errors
     * @param header
     * @param errorField
     * @return
     * @throws Exception
     */
    public ExcelExportResult createErrorExcel(String id, List<ExcelError> errors, ExcelHeader header,
            FieldValue errorField) throws Exception {
        ExcelExportResult exportResult = null;
        if (CollectionUtils.isNotEmpty(errors)) {
            //从注册信息中获取Bean信息
            ExcelDefinition excelDefinition = definitionReader.getRegistry().get(id);
            if (excelDefinition == null) {
                throw new ExcelException("没有找到 [" + id + "] 的配置信息");
            }
            List<FieldValue> fieldValues = excelDefinition.getFieldValues();
            //设置默认的错误信息栏
            if (errorField == null) {
                errorField = createErrorField();
            } else {
                errorField.setName(CommonConstants.errorFieldName);
            }
            fieldValues.add(errorField);
            excelDefinition.setFieldValues(fieldValues);

            exportResult = doCreateExcel(excelDefinition, errors, header, ExportTypeEnum.EXPORT_ERROR);
        }
        return exportResult;
    }

    /**
     * 创建Excel,模板信息
     * @param id	 ExcelXML配置Bean的ID
     * @param header Excel头信息(在标题之前)
     * @param fields 指定导出的字段
     * @return
     * @throws Exception
     */
    public Workbook createExcelTemplate(String id, ExcelHeader header, List<String> fields) throws Exception {
        //从注册信息中获取Bean信息
        ExcelDefinition excelDefinition = definitionReader.getRegistry().get(id);
        if (excelDefinition == null) {
            throw new ExcelException("没有找到 [" + id + "] 的配置信息");
        }
        excelDefinition = dynamicCreateExcelDefinition(excelDefinition, fields);
        return doCreateExcel(excelDefinition, null, header, ExportTypeEnum.EXPORT_TEMPLATE).build();
    }

    //抽取父类拥用的字段,同时从它的基础只上在进行筛选指定的字段
    private ExcelDefinition extractSuperClassFields(ExcelDefinition excelDefinition, List<String> fields,
            Class<?> realClass) {
        //抽取出父类所拥有的字段
        List<String> fieldNames = ReflectUtil.getFieldNames(realClass);
        excelDefinition = dynamicCreateExcelDefinition(excelDefinition, fieldNames);
        //抽取指定的字段
        //导出指定字段的标题不是null,动态创建,Excel定义
        excelDefinition = dynamicCreateExcelDefinition(excelDefinition, fields);
        return excelDefinition;
    }

    /**
     * 动态创建ExcelDefinition
     * @param excelDefinition 原来的ExcelDefinition
     * @param fields
     * @return
     */
    private ExcelDefinition dynamicCreateExcelDefinition(ExcelDefinition excelDefinition, List<String> fields) {
        if (CollectionUtils.isNotEmpty(fields)) {
            ExcelDefinition newDef = new ExcelDefinition();
            ReflectUtil.copyProps(excelDefinition, newDef, "fieldValues");
            List<FieldValue> oldValues = excelDefinition.getFieldValues();
            List<FieldValue> newValues = new ArrayList<FieldValue>(oldValues.size());
            //按照顺序,进行添加
            for (String name : fields) {
                for (FieldValue field : oldValues) {
                    String fieldName = field.getName();
                    if (fieldName.equals(name)) {
                        newValues.add(field);
                        break;
                    }
                }
            }
            newDef.setFieldValues(newValues);
            return newDef;
        }
        return excelDefinition;

    }

    protected ExcelExportResult doCreateExcel(ExcelDefinition excelDefinition, List<?> beans, ExcelHeader header,
            ExportTypeEnum exportType) throws Exception {
        // 创建Workbook
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = null;
        if (excelDefinition.getSheetname() != null) {
            sheet = workbook.createSheet(excelDefinition.getSheetname());
        } else {
            sheet = workbook.createSheet();
        }
        //创建标题之前,调用buildHeader方法,完成其他数据创建的一些信息
        if (header != null) {
            header.buildHeader(sheet, excelDefinition, beans);
        }

        Row titleRow = createTitle(excelDefinition, sheet, workbook, exportType);
        //如果listBean不为空,创建数据行
        if (beans != null) {
            createRows(excelDefinition, sheet, beans, workbook, titleRow, exportType);
        }
        ExcelExportResult exportResult = new ExcelExportResult(excelDefinition, sheet, workbook, titleRow, this);
        return exportResult;
    }

    /**
     * 创建Excel标题
     * @param excelDefinition
     * @param sheet
     * @return 标题行
     */
    protected Row createTitle(ExcelDefinition excelDefinition, Sheet sheet, Workbook workbook,
            ExportTypeEnum exportType) {
        //标题索引号
        int titleIndex = sheet.getPhysicalNumberOfRows();
        Row titleRow = sheet.createRow(titleIndex);
        List<FieldValue> originalField = excelDefinition.getFieldValues();
        List<FieldValue> fieldValues = new ArrayList<FieldValue>();
        //根据导出类型设置标题信息
        if (exportType != null) {
            for (int i = 0; i < originalField.size(); i++) {
                FieldValue field = originalField.get(i);
                //导出结果数据
                if (exportType.equals(ExportTypeEnum.EXPORT_DATA)) {
                    if (field.getOperateValid().equals(OperateValidEnum.ALL)
                            || field.getOperateValid().equals(OperateValidEnum.EXPORT)) {
                        fieldValues.add(field);

                    }
                }
                if (exportType.equals(ExportTypeEnum.EXPORT_ERROR)) {
                    if (field.getOperateValid().equals(OperateValidEnum.ALL)
                            || field.getOperateValid().equals(OperateValidEnum.IMPORT)
                            || field.getName().equals(CommonConstants.errorFieldName)) {
                        fieldValues.add(field);
                    }
                }
                if (exportType.equals(ExportTypeEnum.EXPORT_TEMPLATE)) {
                    if (field.getOperateValid().equals(OperateValidEnum.ALL)
                            || field.getOperateValid().equals(OperateValidEnum.IMPORT)) {
                        fieldValues.add(field);
                    }
                }
            }
        }
        excelDefinition.setFieldValues(fieldValues);

        for (int i = 0; i < fieldValues.size(); i++) {
            FieldValue fieldValue = fieldValues.get(i);
            //设置单元格宽度
            if (fieldValue.getColumnWidth() != null) {
                sheet.setColumnWidth(i, fieldValue.getColumnWidth());
            }
            //如果默认的宽度不为空,使用默认的宽度
            else if (excelDefinition.getDefaultColumnWidth() != null) {
                sheet.setColumnWidth(i, excelDefinition.getDefaultColumnWidth());
            }
            Cell cell = titleRow.createCell(i);
            if (excelDefinition.getEnableStyle()) {
                if (fieldValue.getAlign() != null || fieldValue.getTitleBgColor() != null
                        || fieldValue.getTitleFountColor() != null || excelDefinition.getDefaultAlign() != null) {
                    cell.setCellStyle(workbook.createCellStyle());
                    //设置单元格为文本形式
                    setCellText(workbook,cell);
                    //设置cell 对齐方式
                    setAlignStyle(fieldValue, workbook, cell, excelDefinition);
                    //设置标题背景色
                    setTitleBgColorStyle(fieldValue, workbook, cell);
                    //设置标题字体色
                    setTitleFountColorStyle(fieldValue, workbook, cell);
                }
            }
            setCellValue(cell, fieldValue.getTitle());
        }
        return titleRow;
    }

    /**
     * 创建行
     * @param excelDefinition
     * @param sheet
     * @param beans
     * @param workbook
     * @param titleRow
     * @throws Exception
     */
    public void createRows(ExcelDefinition excelDefinition, Sheet sheet, List<?> beans, Workbook workbook,
            Row titleRow, ExportTypeEnum exportType) throws Exception {
        int num = sheet.getPhysicalNumberOfRows();
        int startRow = num;
        if (exportType.equals(ExportTypeEnum.EXPORT_DATA)) {
            for (int i = 0; i < beans.size(); i++) {
                Row row = sheet.createRow(i + num);
                createRow(excelDefinition, row, beans.get(i), workbook, sheet, titleRow, startRow++);
            }
        }
        if (exportType.equals(ExportTypeEnum.EXPORT_ERROR)) {
            for (int i = 0; i < beans.size(); i++) {
                Row row = sheet.createRow(i + num);
                createErrorRow(excelDefinition, row, (ExcelError) beans.get(i), workbook, sheet, titleRow,
                        startRow++);
            }
        }
    }

    /**
     * 创建行
     * @param excelDefinition
     * @param row
     * @param bean
     * @param workbook
     * @param sheet
     * @param titleRow
     * @param rowNum
     * @throws Exception
     */
    protected void createRow(ExcelDefinition excelDefinition, Row row, Object bean, Workbook workbook, Sheet sheet,
            Row titleRow, int rowNum) throws Exception {
        List<FieldValue> fieldValues = excelDefinition.getFieldValues();
        for (int i = 0; i < fieldValues.size(); i++) {
            FieldValue fieldValue = fieldValues.get(i);
            String name = fieldValue.getName();
            Object value = ReflectUtil.getProperty(bean, name);
            //从解析器获取值
            Object val = convert(bean, value, fieldValue, Type.EXPORT, rowNum);
            Cell cell = row.createCell(i);
            //cell样式是否与标题一致,如果一致,找到对应的标题样式进行设置
            if (excelDefinition.getEnableStyle()) {
                if (fieldValue.isUniformStyle()) {
                    //获取标题行
                    //获取对应的标题行样式
                    Cell titleCell = titleRow.getCell(i);
                    CellStyle cellStyle = titleCell.getCellStyle();
                    cell.setCellStyle(cellStyle);
                }else{
                    //默认格式
                    setRowsCellTextStyle(workbook, cell);
                }
            }
            setCellValue(cell, val);
        }
    }

    /**
     * 导出 导入的错误数据信息
     * @param excelDefinition
     * @param row
     * @param excelError
     * @param workbook
     * @param sheet
     * @param titleRow
     * @param rowNum
     * @throws Exception
     */
    protected void createErrorRow(ExcelDefinition excelDefinition, Row row, ExcelError excelError, Workbook workbook,
            Sheet sheet, Row titleRow, int rowNum) throws Exception {
        List<FieldValue> fieldValues = excelDefinition.getFieldValues();
        for (int i = 0; i < fieldValues.size(); i++) {
            FieldValue fieldValue = fieldValues.get(i);
            String name = fieldValue.getName();
            Object value;
            if (name.equals(CommonConstants.errorFieldName)) {
                StringBuilder sb = new StringBuilder();
                List<String> errorMsg = excelError.getErrorMsg();
                for (int j = 0; j < errorMsg.size(); j++) {
                    String error = errorMsg.get(j);
                    sb.append(error);
                    if (!error.endsWith("；") && !error.endsWith(";")) {
                        sb.append(";");
                    }
                }
                value = sb.toString();
            } else {
                value = excelError.getErrorData().get(name);
            }
            Cell cell = row.createCell(i);
            setRowsCellTextStyle(workbook, cell);
            setCellValue(cell, value);
        }
    }

    //设置cell 为文本型
    private void setCellText(Workbook workbook, Cell cell) {
        CellStyle textStyle = cell.getCellStyle();
        DataFormat format = workbook.createDataFormat();
        textStyle.setDataFormat(format.getFormat("@"));
    }

    //定义默认的单元格格式（文本类型），第一次创建时将格式保存起来，防止多次调用导致内存溢出。（POI框架所致）
    private void setRowsCellTextStyle(Workbook workbook,Cell cell){
        if(textStyle==null){
            CellStyle style = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            style.setDataFormat(format.getFormat("@"));
            textStyle =style;
        }
        if(textStyle!=null) {
            cell.setCellStyle(textStyle);//设置单元格格式为"文本"
        }
    }

    //设置cell 对齐方式
    private void setAlignStyle(FieldValue fieldValue, Workbook workbook, Cell cell, ExcelDefinition excelDefinition) {
        if (fieldValue.getAlign() != null) {
            CellStyle cellStyle = cell.getCellStyle();
            cellStyle.setAlignment(fieldValue.getAlign());
            cell.setCellStyle(cellStyle);
        } else if (excelDefinition.getDefaultAlign() != null) {
            CellStyle cellStyle = cell.getCellStyle();
            cellStyle.setAlignment(excelDefinition.getDefaultAlign());
            cell.setCellStyle(cellStyle);
        }
    }

    //设置cell 背景色方式
    private void setTitleBgColorStyle(FieldValue fieldValue, Workbook workbook, Cell cell) {
        if (fieldValue.getTitleBgColor() != null) {
            CellStyle cellStyle = cell.getCellStyle();
            cellStyle.setFillForegroundColor(fieldValue.getTitleBgColor());
            cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        }
    }

    //设置cell 字体颜色
    private void setTitleFountColorStyle(FieldValue fieldValue, Workbook workbook, Cell cell) {
        if (fieldValue.getTitleFountColor() != null) {
            CellStyle cellStyle = cell.getCellStyle();
            Font font = workbook.createFont();
            font.setColor(fieldValue.getTitleFountColor());
            cellStyle.setFont(font);
        }
    }

    /**
     * 设置导入错误标题信息
     * @return
     */
    private FieldValue createErrorField() {
        FieldValue fieldValue = new FieldValue();
        fieldValue.setName(CommonConstants.errorFieldName);
        fieldValue.setColumnWidth(40000);
        fieldValue.setTitleBgColor(Short.valueOf(10 + ""));
        fieldValue.setTitleFountColor(Short.valueOf(13 + ""));
        fieldValue.setTitle("错误信息");
        return fieldValue;
    }

}
