package com.myspringboot.myspringbootfirstapplication.service;

import com.myspringboot.myspringbootfirstapplication.domain.ExcelPojo;
import org.junit.Test;
import org.junit.experimental.theories.internal.ParameterizedAssertionError;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class MyServiceTest {

    @Test
    public void test01() {
        String str1 = "aaa";
        String str2 = "aaa";
        System.out.println(str1 == str2);
        System.out.println(str1.equals(str2));
        System.out.println(str1.hashCode());
        System.out.println(str2.hashCode());

        String str3 = new String("aaa");
        String str4 = new String("aaa");
        System.out.println(str3 == str4);
        System.out.println(str3.equals(str4));
        System.out.println(str3.hashCode());
        System.out.println(str4.hashCode());
    }

    public static void main(String[] args) throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.bind(new InetSocketAddress(9000));
        System.out.println("启动9000端口");
        for (;;){
            SocketChannel accept = channel.accept();
            System.out.println(accept.getRemoteAddress());
            String result = "success";
            accept.write(ByteBuffer.wrap(result.getBytes()));
        }
    }

    @Test
    public void test02() {
        Integer i = 100;
        Integer i2 = 100;
        System.out.println(i==i2);

        try {
           int a =  1/0;
        }catch (ParameterizedAssertionError error){
            System.out.println("catched");
        }finally {
            System.out.println("fally runed");
        }
    }

    @Test
    public void test03() {
        ExcelPojo excelPojo = new ExcelPojo();
        excelPojo.setName("pojo");
        excelPojo.getArr().add("pojo");

        ExcelPojo clone = excelPojo.clone();
        System.out.println("name: " + excelPojo.getName() + "," + excelPojo);
        System.out.println("name: " + clone.getName() + "," + clone);

        System.out.println(excelPojo== clone);
        System.out.println(excelPojo.equals(clone) );
        System.out.println(excelPojo.hashCode() + "," + clone.hashCode());

        excelPojo.setName("pojo123");
        excelPojo.getArr().add("pojo123");
        System.out.println("name: " + excelPojo.getName() + "," + excelPojo);
        System.out.println("name: " + clone.getName() + "," + clone);
    }
}
