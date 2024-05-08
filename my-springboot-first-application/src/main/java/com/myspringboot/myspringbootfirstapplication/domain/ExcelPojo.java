package com.myspringboot.myspringbootfirstapplication.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@AllArgsConstructor
@Data
@Builder
@Slf4j
public class ExcelPojo implements Cloneable {

    @ExcelProperty("序号")
    private Long id;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("性别")
    private String sex;

    private String abc;

    private ArrayList<String> arr = new ArrayList();

    public ExcelPojo() {
        this.name = "name";
    }

    public ExcelPojo(String name, String sex, String abc) {
        this.name = name;
        this.sex = sex;
        this.abc = abc;
    }

    @Override
    protected void finalize() throws Throwable {
        log.info("ExcelPojo 对象被回收了。。。");
    }

    public ExcelPojo clone(){
        Object clone = null;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return (ExcelPojo)clone;
    }
}
