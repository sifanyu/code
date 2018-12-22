package com.yu.chat.server;

import com.yu.chat.protocol.IMDecoder;
import com.yu.chat.protocol.IMEncoder;
import com.yu.chat.server.handler.HttpHandler;
import com.yu.chat.server.handler.SocketHandler;
import com.yu.chat.server.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.log4j.Logger;

/**
 * 服务端处理过程
 * @author yusifan
 */
public class ChatServer {

    private static Logger LOG = Logger.getLogger(ChatServer.class);
    private int port = 80;

    public void start(){
        //boss线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //worker线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //启动引擎
            ServerBootstrap b = new ServerBootstrap();
            //主从模型
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //所有自定义业务从这里开始
                            ChannelPipeline pl = ch.pipeline();

                            /** 解析自定义协议 */
                            pl.addLast(new IMDecoder());
                            pl.addLast(new IMEncoder());
                            pl.addLast(new SocketHandler());

                            /** 解析HTTP请求 */
                            //解码和编码HTTP请求
                            pl.addLast(new HttpServerCodec());
                            //将同一个HTTP请求或响应的多个消息对象变成一个完整的FullHttpRequest消息对象
                            pl.addLast(new HttpObjectAggregator(64*1024));
                            //用于处理大数据流，防止传大文件时撑爆JVM内存，是一个写文件出去的解释器
                            pl.addLast(new ChunkedWriteHandler());
                            //拦截HTTP请求用的Handler
                            pl.addLast(new HttpHandler());

                            /** 解析WebSocket协议 */
                            pl.addLast(new WebSocketServerProtocolHandler("/im"));
                            pl.addLast(new WebSocketHandler());
                        }
                    });
            //绑定服务端口，等待客户端连接
            ChannelFuture cf = b.bind(this.port).sync();
            LOG.info("服务已启动，监听端口" + this.port);

            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ChatServer().start();
    }
}
