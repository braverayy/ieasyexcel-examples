package com.leslie.framework.ieasyexcel.example.repository;

import com.leslie.framework.ieasyexcel.example.entity.ExcelRow;
import com.leslie.framework.ieasyexcel.example.entity.constant.EXCEL_ROW_STATUS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author leslie
 * @date 2021/6/07
 */
@Repository
public interface ExcelRowRepository extends JpaRepository<ExcelRow, Long> {

    List<ExcelRow> findByExcelRecordIdAndStatus(Long excelRecordId, EXCEL_ROW_STATUS rowStatus);

    Page<ExcelRow> findByExcelRecordIdAndStatus(Long excelRecordId, EXCEL_ROW_STATUS rowStatus, Pageable pageable);
}
