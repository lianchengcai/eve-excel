package net.evecom.excel.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Excel 导入时产生的错误消息
 * @author lisuo
 */
public class ExcelError {

    /** 错误数据 */
    private Map<String, Object> errorData;
    /** 第几行 */
    private int row;
    /** 错误消息 */
    private List<String> errorMsg = new ArrayList<String>();

    public ExcelError(Map<String, Object> map, int row, List<String> errorMsg) {
        this.errorData=map;
        this.row = row;
        this.errorMsg = errorMsg;
    }

    public Map<String, Object> getErrorData() {
        return errorData;
    }

    public int getRow() {
        return row;
    }

    public List<String> getErrorMsg() {
        return errorMsg;
    }

    @Override
    public String toString() {
        return "ExcelError{" +
                "errorData=" + errorData +
                ", row=" + row +
                ", errorMsg=" + errorMsg +
                '}';
    }
}
