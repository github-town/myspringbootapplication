package com.myspringboot.myspringbootfirstapplication.service.impl;

import com.myspringboot.myspringbootfirstapplication.service.IInetAddressTestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class InetAddressTestService implements IInetAddressTestService {

    private static final String LINE_SEPERATE = System.lineSeparator();

    private static final AtomicBoolean HTTP_SERVER_STARTED = new AtomicBoolean(false);

    private static final int UDP_BIND_PORT = 9001;

    private static final int HTTP_BIND_PORT = 9002;

    @Override
    public String getInetAddress() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        InetAddress byName = InetAddress.getByName("www.baidu.com");
        StringBuilder append = new StringBuilder("本机的IP地址：").append(localHost.getHostAddress()).append(LINE_SEPERATE)
                .append("百度的IP地址：").append(byName.getHostAddress()).append(System.lineSeparator())
                .append("百度的主机名为：").append(byName.getHostName());
        System.out.println(LINE_SEPERATE);
        return append.toString();
    }

    @Override
    public String testUDP() {
        aSyncSend();
        threadSleep(1000);
        aSyncAccept();
        return "success";
    }

    private void aSyncAccept() {
        Runnable runnableAccept = new Runnable() {
            @Override
            public void run() {
                try {
                    acceptPort();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        CompletableFuture.runAsync(runnableAccept);
    }

    private void aSyncSend() {
        Runnable runnableSend = new Runnable() {
            @Override
            public void run() {
                try {
                    sendMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnableSend, "send").start();
    }

    private void sendMessage() throws IOException {
        log.info("sendMessage start...");
        DatagramSocket sendSocket = new DatagramSocket();
        String data = new String("hello,UDP!");
        DatagramPacket sendPacket = new DatagramPacket(data.getBytes(), data.length(), InetAddress.getLocalHost(), UDP_BIND_PORT);

        sendSocket.send(sendPacket);
        sendSocket.close();
    }

    private String acceptPort() throws IOException {
        log.info("acceptPort start...");
        DatagramSocket acceptSocket = new DatagramSocket(UDP_BIND_PORT);
        byte[] bytes = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(bytes, 1024);
        acceptSocket.receive(datagramPacket);

        log.info("received data...");
        InetAddress remote = datagramPacket.getAddress();
        String ip = remote.getHostAddress();
        String hostName = remote.getHostName();
        byte[] data = datagramPacket.getData();
        int length = datagramPacket.getLength();
        String dataStr = new String(data, 0, length);

        acceptSocket.close();
        String result = ip + "-" + hostName + " : " + dataStr;
        System.out.println(result);
        return result;
    }

    @Override
    public String testHTTP(String message) throws IOException {
        startHttpServer();
        return startClientSendMessage(message);
    }

    private String startClientSendMessage(String message) throws IOException {
        // 建立连接
        Socket clientSocket = new Socket("localhost", HTTP_BIND_PORT);
        threadSleep(2000);
        OutputStream cliOutputStream = clientSocket.getOutputStream();
        cliOutputStream.write((message + LINE_SEPERATE).getBytes());
        log.info("cliOutputStream write done...");
        // 关闭输出流,通知服务端,写出数据完毕
        clientSocket.shutdownOutput();

        InputStream cliInputStream = clientSocket.getInputStream();
        log.info("clientSocket getInputStream done...");
        byte[] bytes = new byte[1024];
        int read;
        StringBuilder receiveBuilder = new StringBuilder();
        while ((read = cliInputStream.read(bytes)) != -1) {
            receiveBuilder.append(new String(bytes, 0, read));
        }
        log.info("cliInputStream read done...");
        cliInputStream.close();
        cliOutputStream.close();
        clientSocket.close();
        return receiveBuilder.toString();
    }

    private void startHttpServer() throws IOException {
        // 同步，只监听一次
        if (!HTTP_SERVER_STARTED.get()) {
            synchronized (HTTP_SERVER_STARTED) {
                if (!HTTP_SERVER_STARTED.get()) {
                    log.info("start server port : " + HTTP_BIND_PORT);
                    ServerSocket serverSocket = new ServerSocket(HTTP_BIND_PORT);
                    HTTP_SERVER_STARTED.set(true);
                    CompletableFuture.runAsync(() -> {
                        try {
                            circleAcceptClient(serverSocket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }

    private void circleAcceptClient(ServerSocket serverSocket) throws IOException {
        while (true) {
            Socket accept = serverSocket.accept();
            log.info("serverSocket accept done...");
            CompletableFuture.runAsync(() -> processRequest(accept));
        }
    }

    private void processRequest(Socket accept) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = accept.getInputStream();
            log.info("accept getInputStream done...");
            byte[] chars = new byte[1024 * 2];
            int read;
            String mes = Strings.EMPTY;
            while ((read = inputStream.read(chars)) != -1) {
                log.info("inputStream read chars..." + read);
                mes += new String(chars, 0, read);
                // 直接用浏览器请求9002端口时，因为浏览器没有主动shutdownOutputstream，这里就会一直阻塞read，这里做个特殊处理
                if (read > 500) {
                    break;
                }
            }
            log.info("bufferedReader readLine done...");
            log.info("received message : " + mes);
            threadSleep(2000);
            outputStream = accept.getOutputStream();
            outputStream.write("HTTP/1.1 200 OK\r\n".getBytes());
            outputStream.write("Content-Type:application/json\r\n".getBytes());
            outputStream.write("\r\n".getBytes());
            outputStream.write(("hello , i received your message : " + LINE_SEPERATE + mes).getBytes());
            accept.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (accept != null) {
                try {
                    accept.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void threadSleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
