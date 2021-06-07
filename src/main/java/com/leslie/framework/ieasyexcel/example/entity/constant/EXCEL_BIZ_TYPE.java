package com.leslie.framework.ieasyexcel.example.entity.constant;

import com.leslie.framework.ieasyexcel.example.entity.excel.CityExcel;
import com.leslie.framework.ieasyexcel.read.BasedReadBean;

/**
 * @author leslie
 * @date 2021/6/7
 */
public enum EXCEL_BIZ_TYPE {
    CITY(CityExcel.class);

    public final Class<? extends BasedReadBean> excelClazz;

    EXCEL_BIZ_TYPE(Class<? extends BasedReadBean> excelClazz) {
        this.excelClazz = excelClazz;
    }
}
