package cn.xing.xingye.touzi.hanler;

import cn.xing.xingye.touzi.model.WeixinLocationMessage;
import cn.xing.xingye.touzi.model.WeixinMessage;

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
