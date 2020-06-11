package cn.xing.xingye.touzi.utils;

import cn.xing.xingye.buy.model.DateRangeEnum;
import cn.xing.xingye.buy.model.WaiZi;
import cn.xing.xingye.buy.model.WaiZiQuery;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 爬取东方财富的陆股通数据
 */
public class WaiziUtils {
    static final Logger log = LoggerFactory.getLogger(WaiziUtils.class);

    private WaiZiQuery query;

    public WaiziUtils(WaiZiQuery query) {
        this.query = query;
    }

    /**
     * 获取外资数据
     */
    public List<WaiZi> queryWzList() throws Exception {
        String url = "http://dcfm.eastmoney.com/EM_MutiSvcExpandInterface/api/js/get";
        Map<String, String> params = Maps.newHashMap();
        params.put("type", "HSGT20_GGTJ_SUM");
        params.put("token", "894050c76af8597a853f5b408b759f5d");
        params.put("st", "ShareSZ");
        params.put("sr", "-1");
        params.put("p", "1"); // 第一页
        params.put("ps", query.dataNum + ""); // 每页行数
        // 指定数据范围和截止日期
        params.put("filter", "(DateType=%27" + query.incDateRange.getRange() + "%27%20and%20HdDate=%27" + query.date + "%27)");

        String param = Joiner.on("&").join(params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.toList()));
        String uri = url + "?" + param;
        System.out.println(uri);
        HttpGet get = new HttpGet(uri);
        get.addHeader("Referer", "http://data.eastmoney.com/hsgtcg/list.html?st=ShareSZ");
        get.addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
        byte[] bytes = HttpClientUtils.execute(get, -1);
        String content = new String(bytes);
        List<WaiZi> list = convert(content);

        // 过滤流通股比例低于指定值
        list = list.stream().filter(one -> {
            boolean flag = one.getLtsz() / one.getZsz() > query.filterLtRate;
            if (!flag) {
                log.warn("{}/{}，流通比例：{}<={}，不满足条件", one.getName(), one.getCode(), one.getLtsz() / one.getZsz(), query.filterLtRate);
            }
            return flag;
        }).collect(Collectors.toList());

        // 评分，并按评分结果降序排列
        list = score(list);

        // 补充增减持信息
        Date endTime = DateUtils.parseDate(query.date, new String[]{"yyyy-MM-dd"});
        String startDate = DateFormatUtils.format(DateUtils.addDays(endTime, -180), "yyyy-MM-dd");
        String endDate = query.date;
        log.info("queryZjcRate, start: {}, end: {}", startDate, endDate);
        int i = 0;
        for (WaiZi one : list) {
            try {
                if (i++ > query.zjcTopN) break; // 只补充得分前N股票的增减持信息
                one.setZcRate(queryZjcRate(one.getCode(), true, startDate, endDate));
                one.setJcRate(queryZjcRate(one.getCode(), false, startDate, endDate));
            } catch (Exception e) {
                log.error("queryZjcRate error: {}", e.getMessage());
            }
        }

        // 过滤减持比例大于指定值
        return list.stream().filter(one -> {
            boolean flag = one.getJcRate() == null || one.getJcRate() < query.filterJcRate;
            if (!flag) {
                log.warn("{}/{}，近半年减持比例：{}>={}，不满足条件", one.getName(), one.getCode(), one.getJcRate(), query.filterJcRate);
            }
            return flag;
        }).collect(Collectors.toList());

    }

    private List<WaiZi> convert(String content) {
        List<WaiZi> wzs = Lists.newArrayList();

        JSONArray arr = JSONArray.parseArray(content);
        for (int i = 0; i < arr.size(); i++) {
            JSONObject json = arr.getJSONObject(i);
            WaiZi wz = new WaiZi();
            wz.setName(json.getString("SName")); // 股票名
            wz.setCode(json.getString("SCode")); // 股票代码
            wz.setHyName(json.getString("HYName")); // 行业
            wz.setLtsz(json.getDouble("LTSZ")); // 流通市值
            wz.setZsz(json.getDouble("ZSZ")); // 总市值
            wz.setShareSZ(json.getDouble("ShareSZ")); // 持股市值
            wz.setLtRate(json.getDouble("LTZB") * 100); // 流通股比例
            wz.setIncSZ(json.getDouble("ShareSZ_Chg_One")); // 变动市值
            wz.setIncLtRate(json.getDouble("LTZB_One") * 100); // 变动占占流通股比例
            wzs.add(wz);
        }

        return wzs;
    }

    private double queryZjcRate(String code, boolean zc, String startDate, String endDate) throws Exception {
        String content = readZJC(code, zc);

        JSONObject res = JSONObject.parseObject(content);
        JSONObject json = res.getJSONArray("Data").getJSONObject(0);
        List<String> fields = Splitter.on(",").splitToList(json.getString("FieldName"));
        int ltRateIdx = fields.indexOf("BDSLZLTB"); // 变动占流通股比例索引
        int zgRateIdx = fields.indexOf("BDZGBBL"); // 变动占总股比例索引
        int startDateIdx = fields.indexOf("BDKS"); // 变动起始日期索引
        String splitter = json.getString("SplitSymbol");
        JSONArray arr = json.getJSONArray("Data");
        double d = 0d;
        for (int i = 0; i < arr.size(); i++) {
            List<String> list = Splitter.on(splitter).trimResults().splitToList(arr.getString(i));
            try {
                String bgDate = list.get(startDateIdx);
                if (!(bgDate.compareTo(startDate) >= 0 && bgDate.compareTo(endDate) <= 0)) continue;
                String s = list.get(zgRateIdx); // 优先使用占总股比例
                if (StringUtils.isEmpty(s)) {
                    s = list.get(ltRateIdx); // 占总股比例为空时，降级使用占流通股比例
                }
                d += Double.valueOf(s);
            } catch (Exception e) {
                log.error("error {} for line: {}", e.getMessage(), arr.getString(i));
            }
        }
        return d;
    }

    private String readZJC(String code, boolean zc) throws IOException {
        File cacheFile = new File("/Users/indexing/Dropbox/理财/.dgzjc/" + code + (zc ? "-zc" : "-jc") + ".json");
        if (query.zjcUseCacheFirst && cacheFile.exists()) return FileUtils.readFileToString(cacheFile);

        String url = "http://datainterface3.eastmoney.com/EM_DataCenter_V3/api/GDZC/GetGDZC";
        Map<String, String> params = Maps.newHashMap();
        params.put("secucode", code);
        params.put("sharehdname", "");
        params.put("tkn", "eastmoney");
        params.put("cfg", "gdzc");
        params.put("fx", zc ? "1" : "2"); // 增持：fx=1，减持：fx=2
        params.put("sortFields", "BDKS"); // 变动开始日期
        params.put("sortDirec", "1"); // 降序
        params.put("startDate", ""); // 起始变动公告日期
        params.put("endDate", "");// 截止变动公告日期
        params.put("pageNum", "1"); // 第一页
        params.put("pageSize", "50"); // 每页行数

        String param = Joiner.on("&").join(params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.toList()));
        String uri = url + "?" + param;
        // System.out.println(uri);
        HttpGet get = new HttpGet(uri);
        get.addHeader("Referer", "http://data.eastmoney.com/hsgtcg/list.html?st=ShareSZ");
        get.addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
        try {
            String content = new String(HttpClientUtils.execute(get, -1));
            FileUtils.write(cacheFile, content);
            log.info("write to cacheFile: {}", cacheFile);
            return content;
        } catch (Exception e) {
            log.error("request queryZjcRate error: {}", e.getMessage());
            if (cacheFile.exists()) {
                log.warn("read cache file: {}", cacheFile);
                return FileUtils.readFileToString(cacheFile);
            } else {
                throw new RuntimeException("no cache file: " + cacheFile, e);
            }
        }
    }

    /**
     * 给股票打分
     */
    private List<WaiZi> score(List<WaiZi> wzs) {
        List<Comp> comps = Lists.newArrayList();
        comps.add(new Comp("持有市值排序", Comparator.comparing(WaiZi::getShareSZ), query.factorShareSZ));
        comps.add(new Comp("流通股占比排序", Comparator.comparing(WaiZi::getLtRate), query.factorLtRate));
        comps.add(new Comp("变动市值排序", Comparator.comparing(WaiZi::getIncSZ), query.factorIncSZ));
        comps.add(new Comp("变动流通股占比排序", Comparator.comparing(WaiZi::getIncLtRate), query.factorIncLtRate));
        Map<String, Double> scoreMap = rankByCompare(wzs, comps);
        wzs = wzs.stream().map(one -> {
            one.setScore(scoreMap.get(one.getName()));
            return one;
        }).sorted((o1, o2) -> o2.getScore().compareTo(o1.getScore())).collect(Collectors.toList());
        return wzs;
    }

    private static class Comp {
        String name;
        Comparator<WaiZi> comparator;
        double factor;

        public Comp(String name, Comparator<WaiZi> comparator, double factor) {
            this.name = name;
            this.comparator = comparator;
            this.factor = factor;
        }
    }

    private Map<String, Double> rankByCompare(List<WaiZi> wzs, List<Comp> comps) {
        Map<String, Double> map = Maps.newTreeMap();
        for (Comp comp : comps) {
            wzs = wzs.stream().sorted(comp.comparator).collect(Collectors.toList());
            for (int i = 0; i < wzs.size(); i++) {
                double d = (i + 1) * 100 / wzs.size() * comp.factor;
                String name = wzs.get(i).getName();
                log.info("股票：{}，排序项：{}，排名：{}，得分：{}", name, comp.name, i, d);
                Double s = map.get(name);
                if (s == null) s = 0d;
                map.put(name, s + d);
            }
        }


        return map;
    }

    public static void main(String[] args) throws Exception {

        WaiZiQuery query = new WaiZiQuery();
        query.date = "2020-06-10";
        query.incDateRange = DateRangeEnum.MONTH;
        query.dataNum = 300;
        query.zjcTopN = 100;
        query.zjcUseCacheFirst = true;
        query.filterJcRate = 2.0;
        query.filterLtRate = 0.4;
        query.factorShareSZ = 0.4444;
        query.factorLtRate = 0.2222;
        query.factorIncSZ = 0.2222;
        query.factorIncLtRate = 0.1111;

        AtomicInteger rank = new AtomicInteger(1);
        List<String> list = Lists.newArrayList();
        new WaiziUtils(query).queryWzList().stream().forEach(one ->
                list.add(Joiner.on(",").join(Lists.newArrayList(
                        one.getName(), rank.getAndIncrement(), one.getHyName(),
                        (one.getCode().startsWith("6") ? "SH" : "SZ") + one.getCode(),
                        String.format("%.2f", one.getScore()), String.format("%.2f", one.getZsz() / 1e8),
                        String.format("%.2f", one.getLtsz() * 100 / one.getZsz()),
                        String.format("%.2f", one.getShareSZ() / 1e8), String.format("%.2f", one.getLtRate()),
                        String.format("%.2f", one.getIncSZ() / 1e8), String.format("%.2f", one.getIncLtRate()),
                        one.getZcRate() != null ? String.format("%.2f", one.getZcRate()) : "",
                        one.getJcRate() != null ? String.format("%.2f", one.getJcRate()) : ""
                ))));

        if (CollectionUtils.isEmpty(list)) {
            log.error("empty result");
            return;
        }

        list.add(0, Joiner.on(",").join(Lists.newArrayList("股票", "序号", "行业", "编码", "得分",
                "市值(亿)", "流通比例(%)", "持有市值(亿)", "流通股占比(%)", "变动市值(亿)", "变动流通股占比(%)",
                "近半年增持比例(%)", "近半年减持比例(%)")));
        String s = Joiner.on("\n").join(list);
        System.out.println("\n\n\n" + s);
        FileUtils.write(new File("/Users/indexing/Dropbox/理财/waizi-" + query.date + ".csv"), s);
    }
}
