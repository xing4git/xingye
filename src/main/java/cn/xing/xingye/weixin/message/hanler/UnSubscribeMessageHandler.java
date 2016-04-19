package cn.xing.xingye.weixin.message.hanler;

import cn.xing.xingye.model.WeixinEventMessage;
import cn.xing.xingye.model.WeixinMessage;
import cn.xing.xingye.model.WeixinUser;
import cn.xing.xingye.service.WeixinUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by indexing on 16/4/19.
 */
public class UnsubscribeMessageHandler implements MessageHanlder {
    @Autowired
    private WeixinUserService weixinUserService;

    @Override
    public boolean match(WeixinMessage receiveMessage) {
        if (!(receiveMessage instanceof WeixinEventMessage)) return false;
        String event = ((WeixinEventMessage) receiveMessage).getEvent();
        return "unsubscribe".equals(event);
    }

    @Override
    public String handle(WeixinMessage receiveMessage) throws Exception {
        WeixinUser wxUser = new WeixinUser(receiveMessage.getFromUserName());
        weixinUserService.deleteWxUser(wxUser);
        return null;
    }
}
