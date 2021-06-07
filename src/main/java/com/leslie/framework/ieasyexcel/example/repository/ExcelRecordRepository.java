package com.leslie.framework.ieasyexcel.example.repository;

import com.leslie.framework.ieasyexcel.example.entity.ExcelRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author leslie
 * @date 2021/6/07
 */
@Repository
public interface ExcelRecordRepository extends JpaRepository<ExcelRecord, Long> {
}
