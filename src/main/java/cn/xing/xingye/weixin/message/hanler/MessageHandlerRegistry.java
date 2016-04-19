package cn.xing.xingye.weixin.message.hanler;

import cn.xing.xingye.model.WeixinMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by indexing on 16/4/19.
 */
public class MessageHandlerRegistry {
    final Logger LOG = LoggerFactory.getLogger(MessageHandlerRegistry.class);

    private List<MessageHanlder> messageHanlders;

    public List<MessageHanlder> getMessageHanlders() {
        return messageHanlders;
    }

    public void setMessageHanlders(List<MessageHanlder> messageHanlders) {
        this.messageHanlders = messageHanlders;
    }

    public String handle(WeixinMessage receiveMessage) {
        if (messageHanlders == null) return null;
        for (MessageHanlder handler : messageHanlders) {
            try {
                if (handler.match(receiveMessage)) return handler.handle(receiveMessage);
            } catch (Exception e) {
                LOG.error("handle message error: {}", receiveMessage, e);
            }
        }
        return null;
    }
}
