package com.leslie.framework.ieasyexcel.example;

import com.alibaba.excel.context.AnalysisContext;
import com.leslie.framework.ieasyexcel.read.ExcelReadParam;
import com.leslie.framework.ieasyexcel.read.ExcelReadValidation;
import com.leslie.framework.ieasyexcel.read.ExcelReader;
import com.leslie.framework.ieasyexcel.read.listener.ExcelReadListener;

import java.util.List;

/**
 * @author leslie
 * @date 2021/6/7
 */
public class CustomExcelReadListener<T extends ExcelReadValidation> extends ExcelReadListener<T> {

    public CustomExcelReadListener(ExcelReadParam readParam) {
        super(readParam);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void readAndSetContext(List<T> excelDataList, AnalysisContext context) {
        ExcelReader<T> excelReader = (ExcelReader<T>) readParam.getExcelReader();
        excelReader.read(excelDataList,context);
    }
}
