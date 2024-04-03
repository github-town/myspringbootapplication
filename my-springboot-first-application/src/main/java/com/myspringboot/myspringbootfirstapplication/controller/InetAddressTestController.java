package com.myspringboot.myspringbootfirstapplication.controller;

import com.myspringboot.myspringbootfirstapplication.service.IInetAddressTestService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/inetAddressTestController")
public class InetAddressTestController {

    @Resource
    IInetAddressTestService iInetAddressTestService;

    @GetMapping("/getInetAddress")
    public String getInetAddress() throws UnknownHostException {
        return iInetAddressTestService.getInetAddress();
    }

    @GetMapping("/testUDP")
    public String testUDP() {
        return iInetAddressTestService.testUDP();
    }

    @GetMapping("/testHTTP/{message}")
    public String testHTTP(@PathVariable("message") String message) throws IOException {
        return iInetAddressTestService.testHTTP(message);
    }
}
