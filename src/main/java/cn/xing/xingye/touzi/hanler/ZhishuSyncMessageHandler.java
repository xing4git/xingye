package cn.xing.xingye.touzi.hanler;

import cn.xing.xingye.touzi.model.WeixinMessage;
import cn.xing.xingye.touzi.model.WeixinTextMessage;
import cn.xing.xingye.touzi.model.WeixinUser;
import cn.xing.xingye.touzi.model.Zhishu;
import cn.xing.xingye.touzi.service.WeixinUserService;
import cn.xing.xingye.touzi.service.ZhishuService;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by indexing on 16/4/19.
 */
public class ZhishuSyncMessageHandler implements MessageHanlder {
    static final Logger log = LoggerFactory.getLogger(ZhishuSyncMessageHandler.class);

    @Autowired
    private ZhishuService zhishuService;
    @Autowired
    private WeixinUserService weixinUserService;
    private Map<Long, String> cache = Maps.newConcurrentMap();

    @Override
    public boolean match(WeixinMessage receiveMessage) {
        if (!(receiveMessage instanceof WeixinTextMessage)) return false;
        String content = ((WeixinTextMessage) receiveMessage).getContent();
        return content.contains("sync");
    }

    @Override
    public String handle(WeixinMessage receiveMessage) throws Exception {
        String fromUserOpenId = receiveMessage.getFromUserName();
        WeixinUser user = weixinUserService.queryWxUser(fromUserOpenId);
        if (user == null) return "不存在该用户";
        if (user.getUserId() == null || user.getUserId() <= 0) {
            return "未绑定网站用户, 没有权限进行操作";
        }

        List<Zhishu> zhishus = zhishuService.queryZhishus();
        for (Zhishu zhishu : zhishus) {
            try {
                zhishuService.syncDataFromSws(zhishu.getId());
            } catch (Exception e) {
                log.error("sync zhishu data from sws error", e);
            }
        }

        return "指数同步完毕";
    }


}
