package cn.xing.xingye.weixin.message.hanler;

import cn.xing.xingye.model.WeixinMessage;
import cn.xing.xingye.model.WeixinTextMessage;
import cn.xing.xingye.model.Zhishu;
import cn.xing.xingye.model.ZhishuData;
import cn.xing.xingye.service.ZhishuService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by indexing on 16/4/19.
 */
public class ZhishuMessageHandler implements MessageHanlder {
    @Autowired
    private ZhishuService zhishuService;
    private Map<Long, String> cache = Maps.newConcurrentMap();

    @Override
    public boolean match(WeixinMessage receiveMessage) {
        if (!(receiveMessage instanceof WeixinTextMessage)) return false;
        String content = ((WeixinTextMessage) receiveMessage).getContent();
        return content.contains("指数") || content.toLowerCase().contains("zhishu");
    }

    @Override
    public String handle(WeixinMessage receiveMessage) throws Exception {
        String content = null;
        long now = System.currentTimeMillis();
        for (Long key : cache.keySet()) {
            if (now - key < 1000L * 300) {
                content = cache.get(key);
                continue;
            }
            cache.remove(key);
        }
        if (content != null) return content;

        List<Zhishu> zhishus = zhishuService.queryZhishus();
        StringBuilder sb = new StringBuilder();
        for (Zhishu zhishu : zhishus) {
            List<ZhishuData> datas = zhishuService.queryDatas(zhishu.getId());
            if (datas.isEmpty()) continue;
            zhishuService.fillRank(datas);
            ZhishuData lastData = datas.get(0);
            sb.append(dataToString(lastData, zhishu.getName(), datas.size())).append("\n");
        }
        content = sb.toString();

        cache.put(System.currentTimeMillis(), content);
        return content;
    }

    private String dataToString(ZhishuData data, String name, int size) {
        StringBuilder sb = new StringBuilder();
        sb.append("指数名: ").append(name)
                .append(", pe: ").append(data.getPe())
                .append(", pe排名: ").append(data.getPeRank())
                .append(", pe情况: ").append(toRankString(data.getPeRank(), size))
                .append(", pb: ").append(data.getPb())
                .append(", pb排名: ").append(data.getPbRank())
                .append(", pb情况: ").append(toRankString(data.getPbRank(), size))
                .append(", 数据总数: ").append(size)
                .append(", 日期: ").append(data.getDataDate());
        return sb.toString();
    }

    private String toRankString(int rank, int size) {
        double rate = rank * 1.0 / size;
        if (rate < 0.1) return "极度低估";
        if (rate < 0.2) return "中低估";
        if (rate < 0.3) return "微低估";
        if (rate < 0.7) return "合适";
        if (rate < 0.8) return "微高估";
        if (rate < 0.9) return "中高估";
        return "极度高估";
    }

}
