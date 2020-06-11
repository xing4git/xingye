package cn.xing.xingye.touzi.hanler;

import cn.xing.xingye.touzi.model.WeixinMessage;
import cn.xing.xingye.touzi.model.WeixinVoiceMessage;
import org.apache.commons.lang.StringUtils;

/**
 * Created by indexing on 16/4/19.
 */
public class VoiceMessageHandler implements MessageHanlder {
    @Override
    public boolean match(WeixinMessage receiveMessage) {
        return receiveMessage instanceof WeixinVoiceMessage;
    }

    @Override
    public String handle(WeixinMessage receiveMessage) throws Exception {
        String recognition = ((WeixinVoiceMessage) receiveMessage).getRecognition();
        if (StringUtils.isEmpty(recognition)) {
            return "木有听懂你说的...";
        }
        return "让我猜猜你说的是不是: " + recognition;
    }
}
