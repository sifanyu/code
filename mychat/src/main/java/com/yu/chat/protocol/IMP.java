package com.yu.chat.protocol;

/**
 * 自定义IM协议，Instant Messaging Protocol即时通信协议
 * 协议：约定/有规则的字符串，客户端按规则拼接，服务端按规则解析
 * @author yusifan
 */
public enum IMP {

    /** 系统消息 */
    SYSTEM("SYSTEM"),
    /** 登录指令 */
    LOGIN("LOGIN"),
    /** 登出指令 */
    LOGOUT("LOGOUT"),
    /** 聊天消息 */
    CHAT("CHAT"),
    /** 送鲜花 */
    FLOWER("FLOWER");

    private String name;

    public static boolean isIMP(String content){
        return content.matches("^\\[(SYSTEM|LOGIN|LOGOUT|CHAT|FLOWER)\\]");
    }

    IMP(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public String toString(){
        return this.name;
    }
}
