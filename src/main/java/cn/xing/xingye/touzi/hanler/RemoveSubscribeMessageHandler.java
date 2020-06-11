package cn.xing.xingye.touzi.hanler;

import cn.xing.xingye.touzi.model.WeixinEventMessage;
import cn.xing.xingye.touzi.model.WeixinMessage;
import cn.xing.xingye.touzi.model.WeixinUser;
import cn.xing.xingye.touzi.service.WeixinUserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by indexing on 16/4/19.
 */
public class RemoveSubscribeMessageHandler implements MessageHanlder {
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
