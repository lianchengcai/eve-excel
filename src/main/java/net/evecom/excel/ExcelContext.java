package net.evecom.excel;

import net.evecom.excel.config.FieldValue;
import net.evecom.excel.exception.ExcelException;
import net.evecom.excel.parsing.ExcelError;
import net.evecom.excel.parsing.ExcelExport;
import net.evecom.excel.parsing.ExcelHeader;
import net.evecom.excel.parsing.ExcelImport;
import net.evecom.excel.result.ExcelImportResult;
import net.evecom.excel.xml.XMLExcelDefinitionReader;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel上下文支持,只需指定location配置文件路径,即可使用
 * @author lisuo Cai    
 * @created 2017 /11/22 14:36:08
 */
public class ExcelContext {

    private ExcelDefinitionReader definitionReader;

    /** 用于缓存Excel配置 */
    private Map<String, List<FieldValue>> fieldValueMap = new HashMap<String, List<FieldValue>>();

    /**导出*/
    private ExcelExport excelExport;
    /**导入*/
    private ExcelImport excelImport;

    /**
     * 加载Excel配置文件信息
     * @param locations
     */
    public ExcelContext(String locations) {
        try {
            //默认使用XMLExcelDefinitionReader
            definitionReader = new XMLExcelDefinitionReader(locations);
            excelExport = new ExcelExport(definitionReader);
            excelImport = new ExcelImport(definitionReader);
        } catch (ExcelException e) {
            throw e;
        } catch (Exception e) {
            throw new ExcelException(e);
        }
    }

    /**
     * 自定义实现ExcelDefinitionReader
     * @param definitionReader
     */
    public ExcelContext(ExcelDefinitionReader definitionReader) {
        try {
            if (definitionReader == null) {
                throw new ExcelException("definitionReader 不能为空");
            }
            if (MapUtils.isEmpty(this.definitionReader.getRegistry())) {
                throw new ExcelException("definitionReader Registry 不能为空");
            }
            this.definitionReader = definitionReader;
            excelExport = new ExcelExport(definitionReader);
            excelImport = new ExcelImport(definitionReader);
        } catch (ExcelException e) {
            throw e;
        } catch (Exception e) {
            throw new ExcelException(e);
        }
    }

    /***
     * 读取Excel信息
     * @param id 配置ID
     * @param titleIndex 标题索引,从0开始
     * @param excelStream Excel文件流
     * @param multivalidate 是否逐条校验，默认单行出错立即抛出ExcelException，为true时为批量校验,
     *                         可通过ExcelImportResult.hasErrors,和getErrors获取具体错误信息
     * @return ExcelImportResult
     * @throws Exception
     */
    public ExcelImportResult readExcel(String id, int titleIndex, InputStream excelStream, boolean multivalidate)
            throws Exception {
        return excelImport.readExcel(id, titleIndex, excelStream, null, multivalidate);
    }

    /**
     * 创建Excel
     * @param id 配置ID
     * @param beans 配置class对应的List
     * @return Workbook
     * @throws Exception
     */
    public Workbook createExcel(String id, List<?> beans) throws Exception {
        return createExcel(id, beans, null, null);
    }

    /**
     * 创建Excel
     * @param id 配置ID
     * @param beans 配置class对应的List
     * @param header 导出之前,在标题前面做出一些额外的操作,比如增加文档描述等,可以为null
     * @param fields 指定Excel导出的字段(bean对应的字段名称),可以为null
     * @return Workbook
     * @throws Exception
     */
    public Workbook createExcel(String id, List<?> beans, ExcelHeader header, List<String> fields) throws Exception {
        return excelExport.createExcel(id, beans, header, fields).build();
    }

    /**
     * 导出 导入的错误信息
     * @param id
     * @param errors
     * @param header
     * @param errorField
     * @return
     * @throws Exception
     */
    public Workbook createErrorExcel(String id, List<ExcelError> errors, ExcelHeader header, FieldValue errorField)
            throws Exception {
        return excelExport.createErrorExcel(id, errors, header, errorField).build();
    }

    /**
     * 导出模板
     * @param id
     * @param header
     * @param fields
     * @return
     * @throws Exception
     */
    public Workbook createExcelTemplate(String id, ExcelHeader header, List<String> fields) throws Exception {
        return excelExport.createExcelTemplate(id, header, fields);
    }

}
