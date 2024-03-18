package com.myspringboot.myspringbootfirstapplication.service.impl;

import com.alibaba.excel.EasyExcel;
import com.myspringboot.myspringbootfirstapplication.domain.ExcelPojo;
import com.myspringboot.myspringbootfirstapplication.service.IMyService;
import com.myspringboot.myspringbootfirstapplication.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class MyService implements IMyService {

    private MyServiceB myServiceB;

    @Autowired
    ApplicationContext context;

    @Value("${appname}")
    String myappName;

    @Autowired
    public void setMyService(MyServiceB myServiceB) {
        this.myServiceB = myServiceB;
    }

    @Override
    public String service1(String port){
        System.out.println("myappname : " + context.getEnvironment().getProperty("appname"));
        System.out.println("myappname @value: " + myappName);
        myServiceB.pringB();
//        MyServiceB myServiceB1 = new MyServiceB();
        return "service1 方法执行。。。 -- 当前端口为 ： " + port;
    }

    @Override
    public void downLoadExcel(HttpServletRequest request,HttpServletResponse response) throws IOException {
        // 写数据
        List<ExcelPojo> pojos = new ArrayList<>();
        for (long i = 1; i <= 1000000; i++) {
            ExcelPojo excelPojo = new ExcelPojo();
            excelPojo.setId(i);
            excelPojo.setName("张同学" + i);
            excelPojo.setSex(i % 2 == 0 ? "女" : "男");
            pojos.add(excelPojo);
        }

        String servletPath = request.getServletPath();
        log.info("request.getServletPath:{}",servletPath);
        String contextPath = request.getServletContext().getRealPath("");
        log.info("request.getServletContext().getRealPath():{}",contextPath);
        String indexPath = request.getServletContext().getRealPath("/static/index.html");
        log.info("request.getServletContext().getRealPath(index.html):{}",indexPath);
        String property = System.getProperty("user.home");
        log.info("System.getProperty(user.home):{}",property);

        String abFileName = property + "\\temp\\" + System.currentTimeMillis() + ".xlsx";
        String fileName = abFileName.substring(abFileName.lastIndexOf("\\")+1);
        String fileNameEncode = URLEncoder.encode(fileName, "utf-8");

        // 创建目录
        createDir(abFileName);

        EasyExcel.write(abFileName,ExcelPojo.class).sheet("学生").doWrite(pojos);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");// 设置contentType为excel格式
        response.setHeader("Content-Disposition", "Attachment;Filename="+ fileNameEncode);

        try (FileInputStream fileInputStream = new FileInputStream(abFileName);
             ServletOutputStream outputStream = response.getOutputStream()) {
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = fileInputStream.read(buf, 0, 1024)) != -1) {
                outputStream.write(buf, 0, len);
            }
        }
    }

    @Override
    public void myServicePring() {
        log.info("MyService myservicePring donging");
    }

    @Override
    public String callBaidu(String url) {
//        String url = "http://www.12306.cn/index";
//        String url = "https://www.baidu.com";
        try {
            return HttpClientUtil.httpGet(url,new HashMap<>(),new HashMap<>(),60,false);
        } catch (URISyntaxException e) {
            log.error("error : {}",e);
            return "";
        }
    }

    private void createDir(String abFileName) {
        File file = new File(abFileName);
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
    }



}
