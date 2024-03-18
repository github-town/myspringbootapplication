package com.myspringboot.myspringbootfirstapplication.service.impl;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class MyServiceB {

    private MyService myService;

    @Autowired
    public MyServiceB(MyService myService) {
        this.myService = myService;
    }

    public void pringB(){
        myService.myServicePring();
    }
}
