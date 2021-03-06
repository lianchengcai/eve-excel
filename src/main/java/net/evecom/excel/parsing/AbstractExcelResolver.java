package net.evecom.excel.parsing;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import net.evecom.excel.ExcelDefinitionReader;
import net.evecom.excel.config.FieldValue;
import net.evecom.excel.exception.ExcelException;
import net.evecom.excel.util.ExcelUtil;
import net.evecom.util.ReflectUtil;
import net.evecom.util.SpringUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Excel抽象解析器
 * 
 * @author lisuo
 *
 */
public abstract class AbstractExcelResolver implements CellValueConverter {

    protected ExcelDefinitionReader definitionReader;

    /** 注册字段解析信息 */
    private Map<String, CellValueConverter> cellValueConverters = new HashMap<String, CellValueConverter>();

    public AbstractExcelResolver(ExcelDefinitionReader definitionReader) {
        this.definitionReader = definitionReader;
    }

    /**
     * 解析表达式format 属性
     * 
     * @param value
     * @param format
     * @param fieldValue
     * @param rowNum
     * @return
     */
    protected String resolverExpression(Object bean, String value, String format, Type type, FieldValue fieldValue,
            int rowNum) {
        StringBuilder importString = new StringBuilder();
        try {
            String[] expressions = StringUtils.split(format, ",");
           int length = expressions.length;
            for (int i = 0; i < expressions.length; i++) {
                String expression = expressions[i];
                String[] val = StringUtils.split(expression, ":");
                String v1 = val[0];
                String v2 = val[1];
                importString.append(v2);
                if ((i + 1) != length) {
                    importString.append(",");
                }
                if (Type.EXPORT == type) {
                    if (value.equals(v1)) {
                        return v2;
                    }
                } else if (Type.IMPORT == type) {
                    if (value.equals(v2)) {
                        return v1;
                    }
                }
            }
        } catch (ExcelException e) {
            new ExcelException(getErrorMsg(fieldValue, "表达式:" + format + "错误,正确的格式应该以[,]号分割,[:]号取值", rowNum));
        }
        if (Type.IMPORT == type) {
            ReflectUtil.setProperty(bean, fieldValue.getName(), value);
            throw new ExcelException(
                    getErrorMsg(fieldValue, "[" + value + "]取值错误,应该为：" + importString.toString(), rowNum));
        }
        return value;
    }

    /**
     * 设置Cell单元的值
     * 
     * @param cell
     * @param value
     */
    protected void setCellValue(Cell cell, Object value) {
        ExcelUtil.setCellValue(cell, value);
    }

    /**
     * 获取cell值
     * 
     * @param cell
     * @return
     */
    protected Object getCellValue(Cell cell) {
        return ExcelUtil.getCellValue(cell);
    }

    //默认实现
    @Override
    public Object convert(Object bean, Object value, FieldValue fieldValue, Type type, int rowNum) throws Exception {
        if (value != null) {
            //解析器实现，读取数据
            String convName = fieldValue.getCellValueConverterName();
            if (convName == null) {
                //执行默认
                String name = fieldValue.getName();
                String pattern = fieldValue.getPattern();
                String format = fieldValue.getFormat();
                DecimalFormat decimalFormat = fieldValue.getDecimalFormat();
                if (StringUtils.isNotBlank(pattern)) {
                    String[] patterns = StringUtils.split(pattern, ",");
                    if (Type.EXPORT == type) {
                        //导出使用第一个pattern
                        return DateFormatUtils.format((Date) value, patterns[0]);
                    } else if (Type.IMPORT == type) {
                        if (value instanceof String) {
                            Date date = null;
                            try {
                                date = DateUtils.parseDate((String) value, patterns);
                            } catch (Exception e) {
                                StringBuilder errMsg = new StringBuilder("[");
                                errMsg.append(value.toString()).append("]").append("不能转换成日期,正确的格式应该是:[")
                                        .append(pattern + "];");
                                String err = getErrorMsg(fieldValue, errMsg.toString(), rowNum);
                                throw new ExcelException(err);
                            }
                            if (date == null) {
                                StringBuilder errMsg = new StringBuilder("[");
                                errMsg.append(value.toString()).append("]").append("不能转换成日期,正确的格式应该是:[")
                                        .append(pattern + "];");
                                String err = getErrorMsg(fieldValue, errMsg.toString(), rowNum);
                                throw new ExcelException(err);
                            }
                            return date;
                        } else if (value instanceof Date) {
                            return value;
                        } else if (value instanceof Number) {
                            Number val = (Number) value;
                            return new Date(val.longValue());
                        } else {
                            throw new ExcelException(getErrorMsg(fieldValue,
                                    "数据格式错误,[ " + name + " ]的类型是:" + value.getClass() + ",无法转换成日期;", rowNum));
                        }
                    }
                } else if (format != null) {
                    return resolverExpression(bean, value.toString(), format, type, fieldValue, rowNum);
                } else if (decimalFormat != null) {
                    if (Type.IMPORT == type) {
                        if (value instanceof String) {
                            return decimalFormat.parse(value.toString());
                        }
                    } else if (Type.EXPORT == type) {
                        if (value instanceof String) {
                            value = ConvertUtils.convert(value, BigDecimal.class);
                        }
                        return decimalFormat.format(value);
                    }
                } else {
                    return value;
                }
            } else {
                //自定义
                CellValueConverter conv = cellValueConverters.get(convName);
                if (conv == null) {
                    synchronized (this) {
                        if (conv == null) {
                            conv = getBean(convName);
                            cellValueConverters.put(convName, conv);
                        }
                    }
                    conv = cellValueConverters.get(convName);
                }
                value = conv.convert(bean, value, fieldValue, type, rowNum);
                return value;
            }
        }
        return fieldValue.getDefaultValue();

    }

    //获取bean
    private CellValueConverter getBean(String convName) throws ClassNotFoundException {
        CellValueConverter bean = null;
        if (SpringUtil.isInited()) {
            bean = (CellValueConverter) SpringUtil.getBean(Class.forName(convName));
        } else {
            bean = (CellValueConverter) ReflectUtil.newInstance(Class.forName(convName));
        }
        return bean;
    }

    /**
     * 获取错误消息
     * @param fieldValue
     * @param errMsg 消息提示内容
     * @param rowNum
     * @return
     */
    protected String getErrorMsg(FieldValue fieldValue, String errMsg, int rowNum) {
        StringBuilder err = new StringBuilder();
        /*err.append("第[").append(rowNum).append("行],[").append(fieldValue.getTitle()).append("]").append(errMsg);*/
        err.append("[").append(fieldValue.getTitle()).append("]").append(errMsg).append(";");
        return err.toString();
    }

}
