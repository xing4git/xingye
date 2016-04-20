package cn.xing.xingye.weixin.message.hanler;

import cn.xing.xingye.model.*;
import cn.xing.xingye.service.LoginService;
import cn.xing.xingye.service.WeixinUserService;
import cn.xing.xingye.service.ZhishuService;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by indexing on 16/4/19.
 */
public class BindUserMessageHandler implements MessageHanlder {
    static final Logger log = LoggerFactory.getLogger(BindUserMessageHandler.class);

    @Autowired
    private ZhishuService zhishuService;
    @Autowired
    private WeixinUserService weixinUserService;
    @Autowired
    private LoginService loginService;

    @Override
    public boolean match(WeixinMessage receiveMessage) {
        if (!(receiveMessage instanceof WeixinTextMessage)) return false;
        String content = ((WeixinTextMessage) receiveMessage).getContent();
        return content.startsWith("bind");
    }

    @Override
    public String handle(WeixinMessage receiveMessage) throws Exception {
        String content = ((WeixinTextMessage) receiveMessage).getContent();
        String arr[] = content.trim().split("\\s");
        if (arr.length != 3) return "绑定用户的消息格式为: bind username password";
        LoginInfo info = loginService.login(arr[1], arr[2]);
        Long userId = info.getUser().getId();
        weixinUserService.bind(receiveMessage.getFromUserName(), userId);
        return "绑定用户: " + info.getUser().getUsername();
    }


}
