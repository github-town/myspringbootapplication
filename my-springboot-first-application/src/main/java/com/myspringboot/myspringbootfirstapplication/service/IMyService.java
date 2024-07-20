package com.myspringboot.myspringbootfirstapplication.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IMyService {
    String service1(String port);

    String referenceTest();

    void downLoadExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;

    void downLoadExcelWithOutSaveFile(HttpServletRequest request, HttpServletResponse response) throws IOException;

    void myServicePring();

    String callBaidu(String url);

    String restTemplateTest();

    String automicIntegerTest(int count) throws InterruptedException;

    String integerTest(int count) throws InterruptedException;

    String threadLocalTest(String value);
}
