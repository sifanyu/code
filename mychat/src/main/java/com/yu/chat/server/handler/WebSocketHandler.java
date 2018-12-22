package com.yu.chat.server.handler;

import com.yu.chat.processor.IMProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * 拦截WebSocket协议后台处理过程
 * @author yusifan
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private IMProcessor processor = new IMProcessor();

    /**
     * 消息处理
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        processor.process(ctx.channel(), msg.text());
    }

    /**
     * 可处理直接关网页窗口
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        processor.logout(ctx.channel());
    }
}
