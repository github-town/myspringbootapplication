package com.myspringboot.myspringbootfirstapplication.controller;

import com.myspringboot.myspringbootfirstapplication.service.impl.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        System.out.println("System.getenv : " + System.getenv());
        String port = environment.getProperty("server.port");
        return myService.service1(port);
    }

    @GetMapping("/download")
    public void download(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "ids", required = false) String ids) throws IOException {
        myService.downLoadExcel(request, response);
    }

    @GetMapping("/baiduIndex")
    public String baiduIndex(@RequestParam("url") String url) {
        return myService.callBaidu(url);
    }
}
