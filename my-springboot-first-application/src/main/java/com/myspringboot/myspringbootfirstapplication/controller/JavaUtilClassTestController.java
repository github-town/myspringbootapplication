package com.myspringboot.myspringbootfirstapplication.controller;

import com.myspringboot.myspringbootfirstapplication.service.IJavaUtilClassTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/javaUtilClassTestController")
public class JavaUtilClassTestController {

    @Resource
    IJavaUtilClassTestService javaUtilClassTestService;

    @GetMapping("/classPathTest")
    public String classPathTest() throws IOException {
        return javaUtilClassTestService.classPathTest();
    }
}
