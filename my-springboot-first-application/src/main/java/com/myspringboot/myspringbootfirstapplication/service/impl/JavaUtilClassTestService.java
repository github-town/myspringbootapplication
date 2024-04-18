package com.myspringboot.myspringbootfirstapplication.service.impl;

import com.myspringboot.myspringbootfirstapplication.service.IJavaUtilClassTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.io.*;
import java.net.URL;

@Service
public class JavaUtilClassTestService implements IJavaUtilClassTestService {
    private static final Logger log = LoggerFactory.getLogger(JavaUtilClassTestService.class);

    @Override
    public String classPathTest() throws IOException {
        try {
            System.out.println("this getClass() getResource(\"JavaUtilClassTestService.java\").getPath"
                    + this.getClass().getResource("/com/myspringboot/myspringbootfirstapplication/service/impl/JavaUtilClassTestService.class").getPath());
        } catch (Exception e) {
            log.error("this getClass() getResource(\"JavaUtilClassTestService.java\").getPath error", e);
        }
        System.out.println("this getClass() getName() : " + this.getClass().getName());
        System.out.println("this getClass() getSimpleName() : " + this.getClass().getSimpleName());
        System.out.println("this getClass() getCanonicalName() : " + this.getClass().getCanonicalName());
        System.out.println("this getClass() getTypeName() : " + this.getClass().getTypeName());
        Class<?> aClass = null;
        try {
            aClass = ClassUtils.forName("com.myspringboot.myspringbootfirstapplication.domain.ExcelPojo", this.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            log.error("ClassUtils forName error", e);
        }
        System.out.println(aClass == null ? null : aClass.getTypeName());

        URL resource = this.getClass().getResource("/static/index.html");
        System.out.println("resource getPath : " + resource.getPath());
        File file = new File(resource.getPath());
        StringBuilder result = new StringBuilder();
        if (file.exists()) {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    result.append(line);
                }
            } catch (IOException e) {
                log.error("file read error", e);
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        }
        return result.toString();
    }
}
