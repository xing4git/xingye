package cn.xing.xingye.touzi.hanler;

import cn.xing.xingye.touzi.model.WeixinEventMessage;
import cn.xing.xingye.touzi.model.WeixinMessage;
import cn.xing.xingye.touzi.model.WeixinUser;
import cn.xing.xingye.touzi.service.WeixinUserService;
import cn.xing.xingye.touzi.utils.XingConst;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by indexing on 16/4/19.
 */
public class SubscribeMessageHandler implements MessageHanlder {
    @Autowired
    private WeixinUserService weixinUserService;

    @Override
    public boolean match(WeixinMessage receiveMessage) {
        if (!(receiveMessage instanceof WeixinEventMessage)) return false;
        String event = ((WeixinEventMessage) receiveMessage).getEvent();
        return "subscribe".equals(event);
    }

    @Override
    public String handle(WeixinMessage receiveMessage) throws Exception {
        WeixinUser wxUser = new WeixinUser(receiveMessage.getFromUserName());
        weixinUserService.addWxUser(wxUser);
        int userCount = weixinUserService.queryWxUserCount();
        return "你才来啊, 在你之前已经有" + userCount + "位同道关注我啦!" + XingConst.WEIXIN_GUIDE_CONTENT;
    }
}
