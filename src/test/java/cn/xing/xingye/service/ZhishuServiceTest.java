package cn.xing.xingye.service;

import cn.xing.xingye.utils.CommonUtils;
import cn.xing.xingye.model.ZhishuData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by zhangxing on 15/12/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/spring/main.xml")
public class ZhishuServiceTest {
    private static final Logger log = LoggerFactory.getLogger(ZhishuServiceTest.class);

    @Autowired
    private ZhishuService zhishuService;
    private Map<Long, String> filenameMap = Maps.newHashMap();

    @org.junit.Before
    public void init() {
        filenameMap.put(1L, "zhongpan");
        filenameMap.put(2L, "zhongxiaoban");
        filenameMap.put(3L, "xiaofei");
        filenameMap.put(4L, "300");
        filenameMap.put(5L, "yiyao");
        filenameMap.put(6L, "chuangyeban");
        filenameMap.put(7L, "50");
    }

    @Test
    public void testAdd() {
        zhishuService.addZhishu("申万中盘指数");
    }

    @Test
    public void testQuery() {
        log.info(zhishuService.queryZhishus() + "");
    }

    /**
     * 删除统计周期之外的数据
     */
    @Test
    public void deleteNonMondayData() {
        long zhishuId = 4;
        List<ZhishuData> datas = zhishuService.queryDatas(zhishuId);
        List<Long> delIds = Lists.newArrayList();
        long now = System.currentTimeMillis();
        for (ZhishuData data : datas) {
            boolean ok = true;
            long time = CommonUtils.zhishuDateToTimestamp(data.getDataDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            if (week != Calendar.MONDAY || now - time > 10L * 365 * 24 * 3600 * 1000L) ok = false;
            if (!ok) delIds.add(data.getId());
        }
        zhishuService.deleteData(delIds);
    }

    @Test
    public void testAddData() throws Exception {
        String dir = "/Users/indexing/source/java/xingye/shenwan/";
        long zhishuId = 3;
        String filename = filenameMap.get(zhishuId) + ".data";
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dir + filename)));

        String line;
        while ((line = reader.readLine()) != null) {
            try {
                line = line.trim();
                String[] arr = line.split("\\s+");
                if (arr.length != 4) {
                    log.error("error line: {}, len is: {}", line, arr.length);
                    continue;
                }
                boolean ok = true;
                for (String s : arr) {
                    if (!StringUtils.isEmpty(s)) continue;
                    log.error("error line: {}", line);
                    ok = false;
                    continue;
                }
                if (!ok) continue;
                // 2015-12-11 4631.28	45.3500	3.8900
                ZhishuData data = new ZhishuData();
                data.setZhishuId(zhishuId);
                data.setPe(Double.valueOf(arr[2]));
                data.setPb(Double.valueOf(arr[3]));
                data.setShoupan(Double.valueOf(arr[1]));
                data.setDataDate(arr[0]);

                long time = CommonUtils.zhishuDateToTimestamp(data.getDataDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);

                long now = System.currentTimeMillis();
                if (now - time > 10L * 365 * 24 * 3600 * 1000) {
                    log.info("two old data: {}", data.getDataDate());
                    continue;
                }

                int week = calendar.get(Calendar.DAY_OF_WEEK);
                if (week != Calendar.MONDAY) {
                    log.info("not monday: {}", data.getDataDate());
                    continue;
                }

                zhishuService.addData(data);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

    }
}
