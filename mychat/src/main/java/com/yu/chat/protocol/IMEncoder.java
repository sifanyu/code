package com.yu.chat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * 自定义IM协议的编码器
 * @author yusifan
 */
public class IMEncoder extends MessageToByteEncoder<IMMessage> {

	/**
	 * IMMessage对象序列化成MsgPack
	 * @param ctx
	 * @param msg
	 * @param out
	 */
	@Override
	protected void encode(ChannelHandlerContext ctx, IMMessage msg, ByteBuf out) throws Exception {
		out.writeBytes(new MessagePack().write(msg));
	}

	/**
	 * 编码处理
	 * @param msg
	 * @return
	 */
	public String encode(IMMessage msg){
		if(null == msg){ return ""; }
		String prex = "[" + msg.getCmd() + "]" + "[" + msg.getTime() + "]";
		if(IMP.LOGIN.getName().equals(msg.getCmd()) ||
		   IMP.CHAT.getName().equals(msg.getCmd()) ||
		   IMP.FLOWER.getName().equals(msg.getCmd())){
			prex += ("[" + msg.getSender() + "]");
		}else if(IMP.SYSTEM.getName().equals(msg.getCmd())){
			prex += ("[" + msg.getOnline() + "]");
		}
		if(!(null == msg.getContent() || "".equals(msg.getContent()))){
			prex += (" - " + msg.getContent());
		}
		return prex;
	}

}
