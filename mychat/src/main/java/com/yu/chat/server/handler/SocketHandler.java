package com.yu.chat.server.handler;

import com.yu.chat.processor.IMProcessor;
import com.yu.chat.protocol.IMMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 拦截自定义协议处理过程
 * @author yusifan
 */
public class SocketHandler extends SimpleChannelInboundHandler<IMMessage> {

    private IMProcessor processor = new IMProcessor();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws Exception {
        processor.sendMsg(ctx.channel(), msg);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

}
