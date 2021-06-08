package com.leslie.framework.ieasyexcel.example;

import com.alibaba.excel.EasyExcel;
import com.leslie.framework.ieasyexcel.apply.ExcelApplyExecutor;
import com.leslie.framework.ieasyexcel.apply.ExcelApplyParam;
import com.leslie.framework.ieasyexcel.apply.loader.ApplyContextPageLoaderAdapter;
import com.leslie.framework.ieasyexcel.context.ApplyContext;
import com.leslie.framework.ieasyexcel.context.ReadContext;
import com.leslie.framework.ieasyexcel.context.holder.ContextHolder;
import com.leslie.framework.ieasyexcel.context.holder.ContextHolderBuilder;
import com.leslie.framework.ieasyexcel.example.entity.ExcelRecord;
import com.leslie.framework.ieasyexcel.example.entity.ExcelRow;
import com.leslie.framework.ieasyexcel.example.entity.constant.EXCEL_BIZ_TYPE;
import com.leslie.framework.ieasyexcel.example.entity.constant.EXCEL_ROW_STATUS;
import com.leslie.framework.ieasyexcel.example.repository.ExcelRecordRepository;
import com.leslie.framework.ieasyexcel.example.repository.ExcelRowRepository;
import com.leslie.framework.ieasyexcel.read.BasedExcelReadModel;
import com.leslie.framework.ieasyexcel.read.ExcelReadParam;
import com.leslie.framework.ieasyexcel.read.listener.ExcelReadListener;
import com.leslie.framework.ieasyexcel.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author leslie
 * @date 2021/6/7
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class ExcelServiceTests {

    @Autowired
    private ExcelRecordRepository excelRecordRepository;

    @Autowired
    private ExcelRowRepository excelRowRepository;

    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("cities.xlsx");

    @Test
    @Order(1)
    void readTest() {
        ExcelRecord excelRecord = save(EXCEL_BIZ_TYPE.CITY, "leslie");

        Long excelRecordId = excelRecord.getId();
        String key = String.format("excel:read:%s", excelRecordId);

        ContextHolder<String, ReadContext> contextHolder = ContextHolderBuilder.<ReadContext>create().build();

        // 构建 Excel 读参数
        ExcelReadParam readParam = buildReadParam(excelRecordId, key, contextHolder);

        // 创建监听器
        ExcelReadListener<? extends BasedExcelReadModel> readListener = new ExcelReadListener<>(readParam);
        // 读取 Excel
        EasyExcel.read(inputStream, EXCEL_BIZ_TYPE.CITY.excelClazz, readListener).sheet().doRead();

        // 验证
        List<ExcelRow> rows = excelRowRepository.findByExcelRecordIdAndStatus(excelRecord.getId(), EXCEL_ROW_STATUS.FAILURE_PRECHECK);
        assertThat(rows).isNotEmpty();
        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).getMsg()).isEqualTo("数据重复");
        assertThat(rows.get(1).getMsg()).isEqualTo("省份编码不能为空");
    }

    @Test
    @Order(2)
    void readTestWithMonitor() {
        ExcelRecord excelRecord = save(EXCEL_BIZ_TYPE.CITY, "leslie");

        Long excelRecordId = excelRecord.getId();
        String key = String.format("excel:read:%s", excelRecordId);

        ContextHolder<String, ReadContext> contextHolder = ContextHolderBuilder.<ReadContext>create().build();

        // 构建 Excel 读参数
        ExcelReadParam readParam = buildReadParam(excelRecordId, key, contextHolder);

        // 创建监听器
        ExcelReadListener<? extends BasedExcelReadModel> readListener = new CustomExcelReadListener<>(readParam);
        // 读取 Excel
        EasyExcel.read(inputStream, EXCEL_BIZ_TYPE.CITY.excelClazz, readListener).sheet().doRead();

        // 验证
        List<ExcelRow> rows = excelRowRepository.findByExcelRecordIdAndStatus(excelRecord.getId(), EXCEL_ROW_STATUS.FAILURE_PRECHECK);
        assertThat(rows).isNotEmpty();
        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).getMsg()).isEqualTo("数据重复");
        assertThat(rows.get(1).getMsg()).isEqualTo("省份编码不能为空");
    }

    @Test
    @Order(3)
    void applyTest() {
        // read excel
        ExcelRecord excelRecord = save(EXCEL_BIZ_TYPE.CITY, "leslie");

        Long excelRecordId = excelRecord.getId();
        String readKey = String.format("excel:read:%s", excelRecordId);

        ContextHolder<String, ReadContext> readContextHolder = ContextHolderBuilder.<ReadContext>create().build();

        // 构建 Excel 读参数
        ExcelReadParam readParam = buildReadParam(excelRecordId, readKey, readContextHolder);

        // 创建监听器
        ExcelReadListener<? extends BasedExcelReadModel> readListener = new ExcelReadListener<>(readParam);
        // 读取 Excel
        EasyExcel.read(inputStream, EXCEL_BIZ_TYPE.CITY.excelClazz, readListener).sheet().doRead();


        // apply excel
        String applyKey = String.format("excel:apply:%s", excelRecordId);

        ContextHolder<String, ApplyContext> applyContextHolder = ContextHolderBuilder.<ApplyContext>create().build();

        // 构建 Excel 入库参数
        ExcelApplyParam applyParam = ExcelApplyParam.builder()
                .key(applyKey)
                .contextHolder(applyContextHolder)
                .contextLoader(new ApplyContextPageLoaderAdapter<>(pageable -> excelRowRepository.findByExcelRecordIdAndStatus(excelRecordId, EXCEL_ROW_STATUS.UNAPPLY, pageable)))
                .excelApplier((data, context) -> {

                    // 验证数据合法性并保存到业务表
                    log.info("Apply data: {}", data);
                    log.warn("ApplyContext: {}", applyContextHolder.getContext(applyKey).orElse(ApplyContext.builder().build()));

                }).build();

        // 入库执行器
        ExcelApplyExecutor<ExcelRow> applyExecutor = new ExcelApplyExecutor<>(applyParam);
        // 入库
        applyExecutor.execute();
    }

    public ExcelRecord save(EXCEL_BIZ_TYPE excelBizType, String opUname) {
        ExcelRecord excelRecord = new ExcelRecord();
        excelRecord.setExcelBizType(excelBizType);
        excelRecord.setOpUname(opUname);

        excelRecordRepository.save(excelRecord);
        return excelRecord;
    }

    public ExcelReadParam buildReadParam(Long excelRecordId, String key, ContextHolder<String, ReadContext> contextHolder) {
        return ExcelReadParam.builder()
                .key(key)
                .batchCount(50)
                .contextHolder(contextHolder)
                .excelReader((excelDataList, context) -> excelDataList.forEach(data -> {

                    BasedExcelReadModel readData = (BasedExcelReadModel) data;

                    ExcelRow excelRow = new ExcelRow();
                    excelRow.setExcelRecordId(excelRecordId);
                    excelRow.setRowData(JsonUtils.toJsonString(readData));
                    excelRow.setStatus(readData.getAvailable() ? EXCEL_ROW_STATUS.UNAPPLY : EXCEL_ROW_STATUS.FAILURE_PRECHECK);
                    excelRow.setMsg(readData.getMsg());

                    excelRowRepository.save(excelRow);
                })).build();
    }
}
