package com.myspringboot.myspringbootfirstapplication.controller;

import com.myspringboot.myspringbootfirstapplication.service.impl.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;

@RestController
@RequestMapping("/mycontroller")
public class MyController {

    @Autowired
    Environment environment;

    @Resource
    MyService myService;

    @GetMapping("/service1")
    public String controller1() {
        System.out.println("System.getProperties.getport : " + System.getProperties().get("server.port"));
        String port = environment.getProperty("server.port");
        return myService.service1(port);
    }

    @GetMapping("/restTemplateTest")
    public String restTemplateTest() {
        return myService.restTemplateTest();
    }

    @GetMapping("/referencetest")
    public String referencetest() {
        return myService.referenceTest();
    }

    @GetMapping("/download")
    public void download(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "ids", required = false) String ids) throws IOException {
        myService.downLoadExcel(request, response);
    }

    @GetMapping("/baiduIndex")
    public String baiduIndex(@RequestParam("url") String url) {
        return myService.callBaidu(url);
    }

    @GetMapping("/automicIntegerTest/{count}")
    public String automicIntegerTest(@PathVariable("count") int count) {
        return myService.automicIntegerTest(count);
    }

    @GetMapping("/integerTest/{count}")
    public String integerTest(@PathVariable("count") int count) {
        return myService.integerTest(count);
    }

    @GetMapping("/threadLocalTest/{value}")
    public String threadLocalTest(@PathVariable("value") String value) {
        return myService.threadLocalTest(value);
    }
}
