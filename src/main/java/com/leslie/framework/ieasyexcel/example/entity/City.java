package com.leslie.framework.ieasyexcel.example.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author leslie
 * @date 2021/6/7
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "cities")
public class City {

    @Id
    @Column(name = "code", length = 45)
    private Long code;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "province_code", nullable = false, length = 45)
    private Long provinceCode;
}
