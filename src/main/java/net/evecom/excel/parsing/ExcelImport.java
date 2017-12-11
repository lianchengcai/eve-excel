package net.evecom.excel.parsing;

import net.evecom.excel.constant.OperateValidEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import net.evecom.excel.ExcelDefinitionReader;
import net.evecom.excel.config.ExcelDefinition;
import net.evecom.excel.config.FieldValue;
import net.evecom.excel.exception.ExcelException;
import net.evecom.excel.result.ExcelImportResult;
import net.evecom.util.ReflectUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel导入实现类
 * @author lisuo
 *
 */
public class ExcelImport extends AbstractExcelResolver {

    public ExcelImport(ExcelDefinitionReader definitionReader) {
        super(definitionReader);
    }

    /**
     * 读取Excel信息
     * @param id 注册的ID
     * @param titleIndex 标题索引
     * @param excelStream Excel文件流
     * @param sheetIndex Sheet索引位置
     * @param multivalidate 是否逐条校验，默认单行出错立即抛出ExcelException，为true时为批量校验,可通过ExcelImportResult.hasErrors,和getErrors获取具体错误信息
     * @return
     * @throws Exception
     */
    public ExcelImportResult readExcel(String id, int titleIndex, InputStream excelStream, Integer sheetIndex,
            boolean multivalidate) throws Exception {
        //从注册信息中获取Bean信息
        ExcelDefinition excelDefinition = definitionReader.getRegistry().get(id);
        if (excelDefinition == null) {
            throw new ExcelException("没有找到 [" + id + "] 的配置信息");
        }
        return doReadExcel(excelDefinition, titleIndex, excelStream, sheetIndex, multivalidate);
    }

    protected ExcelImportResult doReadExcel(ExcelDefinition excelDefinition, int titleIndex, InputStream excelStream,
            Integer sheetIndex, boolean multivalidate) throws Exception {
        Workbook workbook = WorkbookFactory.create(excelStream);
        ExcelImportResult result = new ExcelImportResult();
        //读取sheet,sheetIndex参数优先级大于ExcelDefinition配置sheetIndex
        Sheet sheet = workbook.getSheetAt(sheetIndex == null ? excelDefinition.getSheetIndex() : sheetIndex);
        //标题之前的数据处理
        List<List<Object>> header = readHeader(excelDefinition, sheet, titleIndex);
        result.setHeader(header);
        //获取标题
        List<String> titles = readTitle(excelDefinition, sheet, titleIndex);
        //获取Bean
        List<Object> listBean = readRows(result.getErrors(), excelDefinition, titles, sheet, titleIndex,
                multivalidate);
        result.setListBean(listBean);
        return result;
    }

    /**
     * 解析标题之前的内容,如果ExcelDefinition中titleIndex 不是0
     * @param excelDefinition
     * @param sheet
     * @return
     */
    protected List<List<Object>> readHeader(ExcelDefinition excelDefinition, Sheet sheet, int titleIndex) {
        List<List<Object>> header = null;
        if (titleIndex != 0) {
            header = new ArrayList<List<Object>>(titleIndex);
            for (int i = 0; i < titleIndex; i++) {
                Row row = sheet.getRow(i);
                short cellNum = row.getLastCellNum();
                List<Object> item = new ArrayList<Object>(cellNum);
                for (int j = 0; j < cellNum; j++) {
                    Cell cell = row.getCell(j);
                    Object value = getCellValue(cell);
                    item.add(value);
                }
                header.add(item);
            }
        }
        return header;
    }

    /**
     * 读取多行数据
     * @param excelDefinition
     * @param titles
     * @param sheet
     * @param titleIndex
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> readRows(List<ExcelError> errors, ExcelDefinition excelDefinition, List<String> titles,
            Sheet sheet, int titleIndex, boolean multivalidate) throws Exception {
        int rowNum = sheet.getLastRowNum();
        //读取数据的总共次数
        int totalNum = rowNum - titleIndex;
        int startRow = -titleIndex;
        List<T> listBean = new ArrayList<T>(totalNum);
        for (int i = titleIndex + 1; i <= rowNum; i++) {
            Row row = sheet.getRow(i);
            Object bean = readRow(errors, excelDefinition, row, titles, startRow + i, multivalidate);
            //如果bean 为null 则说明此数据未通过校验，为错误数据不进行导入。
            if (bean != null) {
                listBean.add((T) bean);
            }
        }
        return listBean;
    }

    /**
     * 读取1行
     * @param excelDefinition
     * @param row
     * @param titles
     * @param rowNum 第几行
     * @return
     * @throws Exception
     */
    protected Object readRow(List<ExcelError> errors, ExcelDefinition excelDefinition, Row row, List<String> titles,
            int rowNum, boolean multivalidate) throws Exception {
        //创建注册时配置的bean类型
        Object bean = ReflectUtil.newInstance(excelDefinition.getClazz());
        Map<String, Object> data = new HashMap<String, Object>();
        List<String> errorMsg = null;
        for (FieldValue fieldValue : excelDefinition.getFieldValues()) {
            String title = fieldValue.getTitle();
            for (int j = 0; j < titles.size(); j++) {
                if (title.equals(titles.get(j))) {
                    try {
                        Cell cell = row.getCell(j);
                        //获取Excel原生value值
                        Object value = getCellValue(cell);
                        //将值暂存到 Map 中
                        data.put(fieldValue.getName(), value);
                        //校验
                        validate(fieldValue, value, rowNum);
                        if (value != null) {
                            if (value instanceof String) {
                                //去除前后空格
                                value = value.toString().trim();
                            }
                            value = super.convert(bean, value, fieldValue, Type.IMPORT, rowNum);
                            ReflectUtil.setProperty(bean, fieldValue.getName(), value);
                        }
                        break;
                    } catch (ExcelException e) {
                        if (errorMsg == null)
                            errorMsg = new ArrayList<String>();
                        errorMsg.add(e.getMessage());
                        //应用multivalidate
                        if (multivalidate) {
                            continue;
                        } else {
                            errors.add(new ExcelError(data, rowNum, errorMsg));
                            throw e;
                        }
                    }
                }
            }
        }
        if (errorMsg != null && errorMsg.size() != 0) {
            errors.add(new ExcelError(data, rowNum, errorMsg));
            return null;
        } else {
            return bean;
        }
    }

    /**
     * 获取标题
     * @param excelDefinition
     * @param sheet
     * @param titleIndex
     * @return
     */
    protected List<String> readTitle(ExcelDefinition excelDefinition, Sheet sheet, int titleIndex) {
        // 获取Excel标题数据
        Row hssfRowTitle = sheet.getRow(titleIndex);
        int cellNum = hssfRowTitle.getLastCellNum();
        //Excel字段
        List<FieldValue> fieldValues = excelDefinition.getFieldValues();
        List<String> titles = new ArrayList<String>();
        //模板校验并获取标题
        for (int i = 0; i < fieldValues.size(); i++) {
            FieldValue fieldValue = fieldValues.get(i);
            //字段导入标识验证
            if (fieldValue.getOperateValid().equals(OperateValidEnum.ALL)
                    || fieldValue.getOperateValid().equals(OperateValidEnum.IMPORT)) {
                Boolean hasTitle = false; //模板校验标识
                for (int j = 0; j < cellNum; j++) {
                    Cell cell = hssfRowTitle.getCell(j);
                    Object value = getCellValue(cell);
                    if (value != null) {
                        //若名称相同则处理该字段
                        if (value.toString().trim().equals(fieldValue.getTitle().trim())) {
                            titles.add(value.toString().trim());
                            hasTitle = true;
                        }
                    }
                }
                if (!hasTitle) {
                    throw new ExcelException("模板校验错误，请确认所用模板是否正确！");
                }
            }
        }
        return titles;
    }

    /**
     * 数据有效性校验
     * @param fieldValue
     * @param value
     * @param rowNum
     */
    private void validate(FieldValue fieldValue, Object value, int rowNum) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            //空校验
            if (!fieldValue.isNull()) {
                String err = getErrorMsg(fieldValue, "不能为空", rowNum);
                throw new ExcelException(err);
            }
        } else {
            //正则校验
            String regex = fieldValue.getRegex();
            if (StringUtils.isNotBlank(regex)) {
                String val = value.toString().trim();
                if (!val.matches(regex)) {
                    String errMsg = fieldValue.getRegexErrMsg() == null ? "格式错误" : fieldValue.getRegexErrMsg();
                    String err = getErrorMsg(fieldValue, errMsg, rowNum);
                    throw new ExcelException(err);
                }
            }

            //长度校验
            if (fieldValue.getLength() != null) {
                if (value.toString().toString().length() > fieldValue.getLength()) {
                    String err = getErrorMsg(fieldValue, "长度不能大于" + fieldValue.getLength(), rowNum);
                    throw new ExcelException(err);
                }
            }
        }
    }
}
