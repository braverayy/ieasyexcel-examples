package com.leslie.framework.ieasyexcel.example.entity;

import com.leslie.framework.ieasyexcel.example.entity.constant.EXCEL_BIZ_TYPE;
import com.leslie.framework.ieasyexcel.example.entity.constant.EXCEL_STATUS;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * @author leslie
 * @date 2021/6/7
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "excel_records")
public class ExcelRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "biz_type", nullable = false, length = 45)
    private EXCEL_BIZ_TYPE excelBizType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 45)
    private EXCEL_STATUS status;

    @Column(name = "op_uname", nullable = false, length = 45)
    private String opUname;
}
