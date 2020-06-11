package cn.xing.xingye.touzi.hanler;

import cn.xing.xingye.touzi.model.WeixinMessage;

/**
 * Created by indexing on 16/4/19.
 */
public interface MessageHanlder {
    boolean match(WeixinMessage receiveMessage);
    String handle(WeixinMessage receiveMessage) throws Exception;
}
