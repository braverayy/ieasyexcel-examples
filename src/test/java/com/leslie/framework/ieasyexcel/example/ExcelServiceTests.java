package com.leslie.framework.ieasyexcel.example;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
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
import com.leslie.framework.ieasyexcel.read.BasedReadBean;
import com.leslie.framework.ieasyexcel.read.ExcelReadParam;
import com.leslie.framework.ieasyexcel.read.listener.ExcelReadListener;
import com.leslie.framework.ieasyexcel.util.JsonUtils;
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

        ExcelReadParam readParam = ExcelReadParam.builder()
                .key(key)
                .batchCount(50)
                .contextHolder(contextHolder)
                .excelReader((excelDataList, context) -> {

                    excelDataList.forEach(data -> {

                        BasedReadBean readData = (BasedReadBean) data;

                        ExcelRow excelRow = new ExcelRow();
                        excelRow.setExcelRecordId(excelRecordId);
                        excelRow.setRowData(JsonUtils.toJsonString(readData));
                        excelRow.setStatus(readData.getAvailable() ? EXCEL_ROW_STATUS.UNAPPLY : EXCEL_ROW_STATUS.FAILURE_PRECHECK);
                        excelRow.setMsg(readData.getMsg());

                        excelRowRepository.save(excelRow);

                        contextHolder.getContext(key).ifPresent(System.out::println);
                    });
                }).build();

        ExcelReadListener<? extends BasedReadBean> readListener = new ExcelReadListener<>(readParam);
        read(readListener, EXCEL_BIZ_TYPE.CITY.excelClazz, inputStream);

        List<ExcelRow> rows = excelRowRepository.findByExcelRecordIdAndStatus(excelRecord.getId(), EXCEL_ROW_STATUS.FAILURE_PRECHECK);
        assertThat(rows).isNotEmpty();
        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).getMsg()).isEqualTo("数据重复");
        assertThat(rows.get(1).getMsg()).isEqualTo("省份编码不能为空");
    }

    @Test
    @Order(2)
    void readTestByCustom() {
        ExcelRecord excelRecord = save(EXCEL_BIZ_TYPE.CITY, "leslie");

        Long excelRecordId = excelRecord.getId();
        String key = String.format("excel:read:%s", excelRecordId);

        ContextHolder<String, ReadContext> contextHolder = ContextHolderBuilder.<ReadContext>create().build();

        ExcelReadParam readParam = ExcelReadParam.builder()
                .key(key)
                .batchCount(50)
                .contextHolder(contextHolder)
                .excelReader((excelDataList, context) -> {

                    for (int i = 0; i < excelDataList.size(); i++) {
                        BasedReadBean readData = (BasedReadBean) excelDataList.get(i);
                        ExcelRow excelRow = new ExcelRow();
                        excelRow.setExcelRecordId(excelRecordId);
                        excelRow.setRowData(JsonUtils.toJsonString(readData));
                        excelRow.setStatus(readData.getAvailable() ? EXCEL_ROW_STATUS.UNAPPLY : EXCEL_ROW_STATUS.FAILURE_PRECHECK);
                        excelRow.setMsg(readData.getMsg());

                        excelRowRepository.save(excelRow);

                        ReadRowHolder readRowHolder = context.readRowHolder();
                        ReadSheetHolder readSheetHolder = context.readSheetHolder();

                        ReadContext readContext = ReadContext.builder()
                                .sheetName(readSheetHolder.getSheetName())
                                .sheetNo(readSheetHolder.getSheetNo())
                                .rowTotal(readSheetHolder.getApproximateTotalRowNumber())
                                .rowIndex(readRowHolder.getRowIndex() - 50 + i)
                                .build();

                        contextHolder.setContext(key, readContext);
                        contextHolder.getContext(key).ifPresent(System.out::println);

                    }
                }).build();

        ExcelReadListener<? extends BasedReadBean> readListener = new CustomExcelReadListener<>(readParam);
        read(readListener, EXCEL_BIZ_TYPE.CITY.excelClazz, inputStream);

        List<ExcelRow> rows = excelRowRepository.findByExcelRecordIdAndStatus(excelRecord.getId(), EXCEL_ROW_STATUS.FAILURE_PRECHECK);
        assertThat(rows).isNotEmpty();
        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).getMsg()).isEqualTo("数据重复");
        assertThat(rows.get(1).getMsg()).isEqualTo("省份编码不能为空");
    }

    @Test
    @Order(2)
    void applyTest() {
//        ExcelRecord excelRecord = read(EXCEL_BIZ_TYPE.CITY, "leslie");

        String key = String.format("excel:apply:%s", 1);

        ContextHolder<String, ApplyContext> contextHolder = ContextHolderBuilder.<ApplyContext>create().build();

        ExcelApplyParam applyParam = ExcelApplyParam.builder()
                .key(key)
                .contextHolder(contextHolder)
                .contextLoader(new ApplyContextPageLoaderAdapter<>(3, pageable -> excelRowRepository.findByExcelRecordIdAndStatus(1L, EXCEL_ROW_STATUS.UNAPPLY, pageable)))
                .excelApplier((data, context) -> {

                    System.out.println(JsonUtils.toJsonString(data));
                    contextHolder.getContext(key).ifPresent(System.out::println);

                }).build();

        ExcelApplyExecutor<ExcelRow> applyExecutor = new ExcelApplyExecutor<>(applyParam);
        applyExecutor.execute();
    }

    public ExcelRecord save(EXCEL_BIZ_TYPE excelBizType, String opUname) {
        ExcelRecord excelRecord = new ExcelRecord();
        excelRecord.setExcelBizType(excelBizType);
        excelRecord.setOpUname(opUname);

        excelRecordRepository.save(excelRecord);
        return excelRecord;
    }

    public ExcelReadParam buildReadParam(Long excelRecordId) {
        String key = String.format("excel:read:%s", excelRecordId);

        ContextHolder<String, ReadContext> contextHolder = ContextHolderBuilder.<ReadContext>create().build();

        return ExcelReadParam.builder()
                .key(key)
                .batchCount(50)
                .contextHolder(contextHolder)
                .excelReader((excelDataList, context) -> {

                    excelDataList.forEach(data -> {

                        BasedReadBean readData = (BasedReadBean) data;

                        ExcelRow excelRow = new ExcelRow();
                        excelRow.setExcelRecordId(excelRecordId);
                        excelRow.setRowData(JsonUtils.toJsonString(readData));
                        excelRow.setStatus(readData.getAvailable() ? EXCEL_ROW_STATUS.UNAPPLY : EXCEL_ROW_STATUS.FAILURE_PRECHECK);
                        excelRow.setMsg(readData.getMsg());

                        excelRowRepository.save(excelRow);

                        contextHolder.getContext(key).ifPresent(System.out::println);
                    });
                }).build();
    }

    public <T extends BasedReadBean> void read(ExcelReadListener<? extends BasedReadBean> excelReadListener, Class<T> excelClass, InputStream inputStream) {
        EasyExcel.read(inputStream, excelClass, excelReadListener).sheet().doRead();
    }
}
