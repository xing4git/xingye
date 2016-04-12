package cn.xing.xingye.service;

import cn.xing.xingye.model.Zhishu;
import cn.xing.xingye.model.ZhishuData;
import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangxing on 15/12/21.
 */
@Service
public class ZhishuService extends BaseService {
    private static final Logger log = LoggerFactory.getLogger(ZhishuService.class);

    public void addZhishu(String name) {
        jdbcTemplate.update("INSERT INTO zhishu(name) VALUES (?)", name);
    }

    public List<Zhishu> queryZhishus() {
        JSONArray rows = query("select * from zhishu where deleted=0");
        return toJavaList(rows, Zhishu.class);
    }

    public Zhishu queryZhishu(long zhishuId) {
        return toJavaObj(queryOne("select * from zhishu where id=?", zhishuId), Zhishu.class);
    }

    public void addData(ZhishuData data) {
        jdbcTemplate.update("INSERT INTO zhishu_data(zhishuId, pe, pb, shoupan, dataDate) VALUES(?,?,?,?,?)",
                data.getZhishuId(), data.getPe(), data.getPb(), data.getShoupan(), data.getDataDate());
    }

    public void deleteData(List<Long> dataIds) {
        if (dataIds == null || dataIds.isEmpty()) return;
        String ins = Joiner.on(",").join(dataIds);
        jdbcTemplate.update("delete from zhishu_data where id in (" + ins + ")");
    }

    public List<ZhishuData> queryDatas(long zhishuId) {
        List<ZhishuData> datas = toJavaList(query("select * from zhishu_data where deleted=0 and zhishuId=? " +
                "order by dataDate desc", zhishuId), ZhishuData.class);

        return datas;
    }
}
