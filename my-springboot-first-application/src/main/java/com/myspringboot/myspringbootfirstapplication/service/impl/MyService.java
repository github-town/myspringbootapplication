package com.myspringboot.myspringbootfirstapplication.service.impl;

import com.alibaba.excel.EasyExcel;
import com.myspringboot.myspringbootfirstapplication.domain.ExcelPojo;
import com.myspringboot.myspringbootfirstapplication.service.IMyService;
import com.myspringboot.myspringbootfirstapplication.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.*;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class MyService implements IMyService {

    private MyServiceB myServiceB;

    private static final ReferenceQueue<Object> REFERENCE_QUEUE = new ReferenceQueue<>();

    @Autowired
    ApplicationContext context;

    @Resource
    RestTemplate restTemplateMy;

    @Value("${appname}")
    String myappName;

    @Value("${server.port}")
    String port;

    @Value("${restTemplate.url.firstApplication}")
    String firstApplicationUrl;

    @Autowired
    public void setMyService(MyServiceB myServiceB) {
        this.myServiceB = myServiceB;
    }

    @Override
    public String service1(String port){
        System.out.println("myappname : " + context.getEnvironment().getProperty("appname"));
        System.out.println("myappname @value: " + myappName);
        System.out.println("CPU count : " + Runtime.getRuntime().availableProcessors());
        System.out.println("java.library.path : " + System.getProperty("java.library.path"));
        myServiceB.pringB();
        return "service1 方法执行。。。 -- 当前端口为 ： " + port;
    }

    @Override
    public String restTemplateTest() {
        String url = new StringBuffer(firstApplicationUrl).append("/mycontroller/service1").toString();
        System.out.println("restTemplate call url: " + url);
        try {
            ResponseEntity<String> response = restTemplateMy.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, String.class, new HashMap<>());
            if (response != null) {
                if (response.getStatusCodeValue() == 200) {
                    return response.getBody();
                } else {
                    return "restTemplateTest failed, statusCode : " + response.getStatusCodeValue();
                }
            } else {
                return "restTemplateTest failed, statusCode : " + response.getStatusCodeValue();
            }
        } catch (RestClientException ex) {
            log.error("restTemplateTest failed", ex);
            return ex.getMessage();
        }
    }

    @Override
    public String referenceTest() {
        SoftReference softReference = new SoftReference(new ExcelPojo());
        System.out.println("softReference get : " + softReference.get());

        WeakReference weakReference = new WeakReference(new ExcelPojo());
        System.out.println("weakReference get : " + weakReference.get());

        System.gc();
        try {
            Thread.currentThread().sleep(500);
        } catch (InterruptedException ex) {
            log.error("sleep error", ex);
        }
        System.out.println("after gc, softReference get : " + softReference.get());
        System.out.println("after gc, weakReference get : " + weakReference.get());

        // phantomreference
        PhantomReference<ExcelPojo> phantomReference = new PhantomReference<>(new ExcelPojo(), REFERENCE_QUEUE);
        System.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            log.error("sleep error", ex);
        }
        Reference<?> poll1 = REFERENCE_QUEUE.poll();
        System.out.println("referenceQueue.poll().get() one: "+ poll1);
        Reference<?> poll2 = REFERENCE_QUEUE.poll();
        System.out.println("referenceQueue.poll().get() two: "+ poll2);
        System.out.println("phantomReference get : " + phantomReference.get());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            log.error("sleep error", ex);
        }
        return "<h1>referenceTest success</h1>";
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
