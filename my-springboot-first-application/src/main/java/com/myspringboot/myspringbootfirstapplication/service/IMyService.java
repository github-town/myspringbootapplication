package com.myspringboot.myspringbootfirstapplication.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IMyService {
    String service1(String port);

    void downLoadExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;

    void myServicePring();

    String callBaidu(String url);
}
