/*
 * Copyright (c) 2005, 2016, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package net.evecom.excel.constant;

/**
 * 导出类型枚举
 * @author Iverson Cai  
 * @created 2017 /11/30 09:31:45
 */
public enum ExportTypeEnum {

    /**
     * 导出  导入的错误数据
     */
    EXPORT_ERROR("exportError", 1),
    /**
     * 导出数据
     */
    EXPORT_DATA("exportData", 2),

    /**
     * 导出模板
     */
    EXPORT_TEMPLATE("exportTemplate", 3);

    /**
     * 名称
     */
    private String name;
    /**
     * 值
     */
    private int value;

    ExportTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
