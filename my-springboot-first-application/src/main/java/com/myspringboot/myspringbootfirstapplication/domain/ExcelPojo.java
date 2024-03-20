package com.myspringboot.myspringbootfirstapplication.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Slf4j
public class ExcelPojo {

    @ExcelProperty("序号")
    private Long id;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("性别")
    private String sex;

    private String abc;

    @Override
    protected void finalize() throws Throwable {
        log.info("ExcelPojo 对象被回收了。。。");
    }
}
