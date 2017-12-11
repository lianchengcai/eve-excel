/*
 * Copyright (c) 2005, 2016, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package net.evecom.excel.constant;

/**
 * 字段有效操作范围枚举类
 * @author Iverson Cai  
 * @created 2017 /11/22 14:46:18
 */
public enum OperateValidEnum {

    /**
     *  所有
     */
    ALL("all", 1),
    /**
     * 导入
     */
    IMPORT("import", 2),
    /**
     * 导出
     */
    EXPORT("export", 3);

    /**
     * 名称
     */
    private String name;
    /**
     * 值
     */
    private int value;

    OperateValidEnum(String name, int value) {
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
