package com.myspringboot.myspringbootfirstapplication.service;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        log.info("Received message: " + msg);
        StringBuilder stringBuilder = new StringBuilder("HTTP/1.1 200 OK\r\n");
        stringBuilder.append("Content-Type:application/json\r\n");
        stringBuilder.append("\r\n");
        stringBuilder.append("success");
        stringBuilder.append("你好，客户端！你发送的数据是: " + msg);
        ctx.writeAndFlush(stringBuilder.toString());
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}