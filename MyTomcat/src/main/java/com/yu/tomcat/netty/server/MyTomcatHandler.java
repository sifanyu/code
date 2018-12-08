package com.yu.tomcat.netty.server;

import com.yu.tomcat.http.MyRequest;
import com.yu.tomcat.http.MyResponse;
import com.yu.tomcat.servlet.ClientServlet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;

public class MyTomcatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest r = (HttpRequest)msg;

            MyRequest request = new MyRequest(ctx, r);
            MyResponse response = new MyResponse(ctx, r);

            new ClientServlet().doGet(request, response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
