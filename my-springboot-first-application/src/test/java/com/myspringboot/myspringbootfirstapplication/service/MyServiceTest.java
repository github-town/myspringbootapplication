package com.myspringboot.myspringbootfirstapplication.service;

import org.junit.Test;

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
}
