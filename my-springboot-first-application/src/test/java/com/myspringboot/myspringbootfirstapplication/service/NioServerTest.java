package com.myspringboot.myspringbootfirstapplication.service;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.*;

@Slf4j
public class NioServerTest {

    private static final ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 100,
            10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) {
        try {
            // 创建 Selector
            Selector selector = Selector.open();

            // 创建 ServerSocketChannel
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false); // 设置为非阻塞模式

            // 绑定到指定端口
            serverSocketChannel.bind(new InetSocketAddress(8080));

            // 注册选择器，关注 OP_ACCEPT 事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("服务器启动，监听端口 8080...");

            while (true) {
                // 等待事件
                selector.select();

                // 获取选择器中所有已选择的键
                Iterator<SelectionKey> selectIterator = selector.selectedKeys().iterator();
                while (selectIterator.hasNext()){
                    SelectionKey key = selectIterator.next();
                    if (key.isValid() && key.isAcceptable()) {
                        // 接受连接
                        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
                        socketChannel.configureBlocking(false); // 设置为非阻塞模式
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        System.out.println("新连接: " + socketChannel.getRemoteAddress());
                    } else if (key.isValid() && key.isReadable()) {
                        // 有数据可读
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        poolExecutor.submit(() -> processRequest(socketChannel));
                        key.cancel(); // 解除注册
                    }

                    // 处理完之后从 selectedKeys 中移除当前键，以免重复处理
                    selectIterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processRequest(SocketChannel socketChannel) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(256);

            int bytesRead = socketChannel.read(buffer);
            if (bytesRead == -1) {
                // 客户端关闭连接
                System.out.println("连接关闭: " + socketChannel.getRemoteAddress());
                socketChannel.shutdownOutput();
            } else {
                String receivedData = new String(buffer.array()).trim();
                System.out.println("收到数据: " + receivedData);

                // 发送响应
                StringBuilder stringBuilder = new StringBuilder("HTTP/1.1 200 OK\r\n");
                stringBuilder.append("Content-Type:application/json\r\n");
                stringBuilder.append("\r\n");
                stringBuilder.append("success");
                stringBuilder.append("你好，客户端！你发送的数据是: " + receivedData);
                ByteBuffer responseBuffer = ByteBuffer.wrap(stringBuilder.toString().getBytes());
                socketChannel.write(responseBuffer);
                socketChannel.shutdownOutput();
            }
        } catch (IOException ex) {
            log.error("processRequest error!", ex);
        } finally {
            if (socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (IOException ex) {
                    log.error("socketChannel close error!", ex);
                }
            }
        }
    }
}
