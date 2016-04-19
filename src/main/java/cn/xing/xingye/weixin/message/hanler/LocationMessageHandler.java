package cn.xing.xingye.weixin.message.hanler;

import cn.xing.xingye.model.WeixinLocationMessage;
import cn.xing.xingye.model.WeixinMessage;
import org.springframework.stereotype.Component;

/**
 * Created by indexing on 16/4/19.
 */
public class LocationMessageHandler implements MessageHanlder {
    @Override
    public boolean match(WeixinMessage receiveMessage) {
        return receiveMessage instanceof WeixinLocationMessage;
    }

    @Override
    public String handle(WeixinMessage receiveMessage) throws Exception {
        return ((WeixinLocationMessage) receiveMessage).getLabel() + ": 这是我们的约会地址吗?";
    }
}
