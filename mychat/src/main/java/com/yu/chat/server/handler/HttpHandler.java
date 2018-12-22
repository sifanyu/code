package com.yu.chat.server.handler;


import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 拦截HTTP请求用的Handler
 * @author yusifan
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static Logger LOG = Logger.getLogger(HttpHandler.class);

    //获取classPath
    private URL baseUrl = HttpHandler.class.getProtectionDomain().getCodeSource().getLocation();

    private final String WEB_ROOT = "webroot";

    public File getResource(String fileName) throws Exception {
        String path = baseUrl.toURI() + WEB_ROOT + "/" + fileName;
        path = !path.contains("file") ? path : path.substring(5);
        path = path.replaceAll("//", "/");
        return new File(path);
    }

    /**
     * 类似read0的方法都是实现类的方法，不是接口
     * 只做逻辑处理，SimpleChannelInboundHandler已经返回HTTP处理结果
     * @param ctx
     * @param request
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        //获取客户端请求的URI
        String uri = request.getUri();
        //文件查找和处理
        RandomAccessFile file = null;
        try {
            String page = uri.equals("/") ? "chat.html" : uri;
            file = new RandomAccessFile(getResource(page), "r");
        } catch (Exception e){
            //抛异常时关闭通道?
            ctx.fireChannelRead(request.retain());
            return;
        }

        HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
        String contentType = "text/html;";
        if (uri.endsWith(".css")) {
            contentType = "text/css";
        } else if(uri.endsWith(".js")) {
            contentType = "text/javascript;";
        } else if(uri.toLowerCase().matches("(jpg|png|gif)$")) {
            String ext = uri.substring(uri.lastIndexOf("."));
            contentType = "image/" + ext;
        }

        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType + "charset=UTF-8;");

        //长连接
        boolean keepAlive = HttpHeaders.isKeepAlive(request);
        if (keepAlive) {
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        //先写协议内容，再写文件出去
        ctx.write(response);
        ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
        //清空缓冲区
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }

        file.close();

    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel client = ctx.channel();
        LOG.info("Client" + client.remoteAddress() + "异常");
        ctx.close();
    }
}
