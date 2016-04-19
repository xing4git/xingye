package cn.xing.xingye.weixin.message.hanler;

import cn.xing.xingye.model.WeixinMessage;

/**
 * Created by indexing on 16/4/19.
 */
public interface MessageHanlder {
    boolean match(WeixinMessage receiveMessage);
    String handle(WeixinMessage receiveMessage) throws Exception;
}
