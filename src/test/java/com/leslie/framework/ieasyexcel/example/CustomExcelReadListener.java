package com.leslie.framework.ieasyexcel.example;

import com.alibaba.excel.context.AnalysisContext;
import com.leslie.framework.ieasyexcel.context.ReadContext;
import com.leslie.framework.ieasyexcel.read.BasedExcelReadModel;
import com.leslie.framework.ieasyexcel.read.ExcelReadParam;
import com.leslie.framework.ieasyexcel.read.listener.ExcelReadListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @author leslie
 * @date 2021/6/8
 */
@Slf4j
public class CustomExcelReadListener<T extends BasedExcelReadModel> extends ExcelReadListener<T> {

    public CustomExcelReadListener(ExcelReadParam readParam) {
        super(readParam);
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        log.warn("ReadContext: {}", contextHolder().getContext(key()).orElse(ReadContext.builder().build()));
        super.invoke(data, context);
    }
}
