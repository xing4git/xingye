package cn.xing.xingye.touzi.hanler;

import cn.xing.xingye.touzi.model.WeixinMessage;
import cn.xing.xingye.touzi.model.WeixinVideoMessage;

/**
 * Created by indexing on 16/4/19.
 */
public class VideoMessageHandler implements MessageHanlder {
    @Override
    public boolean match(WeixinMessage receiveMessage) {
        return receiveMessage instanceof WeixinVideoMessage;
    }

    @Override
    public String handle(WeixinMessage receiveMessage) throws Exception {
        return "哎呦, 给我发视频呐, 是不是少儿不宜的?";
    }
}
