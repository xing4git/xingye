package cn.xing.xingye.touzi.hanler;

import cn.xing.xingye.touzi.model.WeixinMessage;
import cn.xing.xingye.touzi.utils.XingConst;

/**
 * Created by indexing on 16/4/19.
 */
public class LastMessageHandler implements MessageHanlder {
    @Override
    public boolean match(WeixinMessage receiveMessage) {
        return true;
    }

    @Override
    public String handle(WeixinMessage receiveMessage) throws Exception {
        return "哎呀, 虽然不知道你给我发了什么, 但看起来很厉害的样子" + XingConst.WEIXIN_GUIDE_CONTENT;
    }
}
