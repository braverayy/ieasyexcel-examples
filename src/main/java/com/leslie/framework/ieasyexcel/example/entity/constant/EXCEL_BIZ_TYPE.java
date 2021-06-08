package com.leslie.framework.ieasyexcel.example.entity.constant;

import com.leslie.framework.ieasyexcel.example.entity.excel.CityExcel;
import com.leslie.framework.ieasyexcel.read.BasedExcelReadModel;

/**
 * @author leslie
 * @date 2021/6/7
 */
public enum EXCEL_BIZ_TYPE {
    CITY(CityExcel.class);

    public final Class<? extends BasedExcelReadModel> excelClazz;

    EXCEL_BIZ_TYPE(Class<? extends BasedExcelReadModel> excelClazz) {
        this.excelClazz = excelClazz;
    }
}
