package cn.xing.xingye.weixin.message.hanler;

import cn.xing.xingye.model.WeixinLinkMessage;
import cn.xing.xingye.model.WeixinLocationMessage;
import cn.xing.xingye.model.WeixinMessage;
import org.springframework.stereotype.Component;

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
