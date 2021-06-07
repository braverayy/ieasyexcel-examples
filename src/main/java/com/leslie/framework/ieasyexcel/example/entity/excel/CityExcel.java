package com.leslie.framework.ieasyexcel.example.entity.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.leslie.framework.ieasyexcel.read.BasedReadBean;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author leslie
 * @date 2021/6/7
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ExcelIgnoreUnannotated
public class CityExcel extends BasedReadBean {

    @NotBlank(message = "城市编码不能为空")
    @ExcelProperty("城市编码")
    private String code;

    @NotBlank(message = "城市名称不能为空")
    @ExcelProperty("城市名称")
    private String name;

    @NotBlank(message = "省份编码不能为空")
    @ExcelProperty("省份编码")
    private String provinceCode;
}
