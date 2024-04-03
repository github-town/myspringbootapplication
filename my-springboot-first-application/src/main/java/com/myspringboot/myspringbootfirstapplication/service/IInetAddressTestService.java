package com.myspringboot.myspringbootfirstapplication.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.UnknownHostException;

public interface IInetAddressTestService {

    String getInetAddress() throws UnknownHostException;

    String testUDP();

    String testHTTP(String message) throws IOException;
}
