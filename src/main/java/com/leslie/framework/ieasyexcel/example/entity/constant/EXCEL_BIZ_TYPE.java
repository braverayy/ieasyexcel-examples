package com.leslie.framework.ieasyexcel.example.entity.constant;

import com.leslie.framework.ieasyexcel.example.entity.excel.CityExcel;
import com.leslie.framework.ieasyexcel.read.ExcelReadValidation;

/**
 * @author leslie
 * @date 2021/6/7
 */
public enum EXCEL_BIZ_TYPE {
    CITY(CityExcel.class);

    public final Class<? extends ExcelReadValidation> excelClazz;

    EXCEL_BIZ_TYPE(Class<? extends ExcelReadValidation> excelClazz) {
        this.excelClazz = excelClazz;
    }
}
