/*
 * Copyright (c) 2005, 2016, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package converter;


import net.evecom.excel.config.FieldValue;
import net.evecom.excel.exception.ExcelException;
import net.evecom.excel.parsing.CellValueConverter;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * Created by lenovo on 2017/8/7.
 */
public class TestCellValueConverter  implements CellValueConverter {

    @Override
    public Object convert(Object bean, Object value, FieldValue fieldValue, Type type, int rowNum)
            throws Exception {
        //如果是导入
        if(type==Type.IMPORT){

            if(value.toString().equals("经商222")){
                return "222";
            }else{
                StringBuilder err = new StringBuilder()
                        .append("第[").append(rowNum).append("行],[")
                        .append(fieldValue.getTitle()).append("]")
                        .append("在数据库中没有找到["+value.toString()+"]的信息");
                Method set = bean.getClass().getMethod("setExcelInfo",
                        String.class);
                Method get = bean.getClass().getMethod("getExcelInfo");
                String getValue = (String)get.invoke(bean);
                System.out.println(getValue);
                System.out.println(getValue!=null);
                System.out.println(getValue==null);
                if(getValue!=null&& !StringUtils.isEmpty(getValue)&&!getValue.equals("null")) {
                    err.append(";").append(getValue);
                }
                set.invoke(bean,err.toString());
                throw new ExcelException(err.toString());
                /*throw new ExcelException(err.toString());*/
            }
        }
        return value;
    }
}
