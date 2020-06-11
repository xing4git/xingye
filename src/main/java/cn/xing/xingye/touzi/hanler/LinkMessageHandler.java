package cn.xing.xingye.touzi.hanler;

import cn.xing.xingye.touzi.model.WeixinLinkMessage;
import cn.xing.xingye.touzi.model.WeixinMessage;

/**
 * Created by indexing on 16/4/19.
 */
public class LinkMessageHandler implements MessageHanlder {
    @Override
    public boolean match(WeixinMessage receiveMessage) {
        return receiveMessage instanceof WeixinLinkMessage;
    }

    @Override
    public String handle(WeixinMessage receiveMessage) throws Exception {
        return "是少儿不宜的文章吗? 不是的话滚粗!";
    }
}
