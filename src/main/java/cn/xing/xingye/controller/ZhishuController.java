package cn.xing.xingye.controller;

import cn.xing.xingye.model.Zhishu;
import cn.xing.xingye.utils.CommonUtils;
import cn.xing.xingye.model.ZhishuData;
import cn.xing.xingye.service.ZhishuService;
import cn.xing.xingye.utils.SwsDownloadUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhangxing on 15/12/9.
 */
@Controller
@RequestMapping("/zhishu")
public class ZhishuController {
    private static final Logger log = LoggerFactory.getLogger(ZhishuController.class);
    private static BigDecimal hundred = BigDecimal.valueOf(100);
    @Autowired
    private ZhishuService zhishuService;

    /*@InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Timestamp.class, new TimestampEditor(new SimpleDateFormat("yyyyMMdd")));
    }*/

    @RequestMapping(value = {"", "list"})
    public ModelAndView zhishu() {
        ModelMap model = new ModelMap();
        String viewName = "zhishu/list";
        model.put("zhishus", zhishuService.queryZhishus());
        return new ModelAndView(viewName, model);
    }

    @RequestMapping(value = {"add_zhishu"})
    public String addZhishu(@RequestParam("name") String zhishuName,
                            @RequestParam(value = "swsCode", required = false) String swsCode,
                            RedirectAttributes attr) {
        log.info("zhishu name: {}", zhishuName);
        if (StringUtils.isEmpty(zhishuName)) {
            attr.addAttribute("error_message", "指数名称不能为空");
            return "redirect:/zhishu/list";
        }
        if (swsCode == null) swsCode = "";
        zhishuService.addZhishu(zhishuName, swsCode);
        attr.addAttribute("success_message", "添加指数: " + zhishuName + "成功!");
        return "redirect:/zhishu/list";
    }

    @RequestMapping("add_data")
    public String addData(ZhishuData data, RedirectAttributes attr) {
        if (!CommonUtils.isValidZhishuDate(data.getDataDate())) {
            attr.addAttribute("error_message", "invalid date: " + data.getDataDate());
            return "redirect:/zhishu/list";
        }
        zhishuService.addData(data);
        attr.addAttribute("success_message", "success!");
        return "redirect:/zhishu/list";
    }

    @RequestMapping("add_data_batch")
    public String addDataBatch(@RequestParam("zhishuId") long zhishuId,
                               @RequestParam("batchContent") String batchContent,
                               RedirectAttributes attr) {
        if (StringUtils.isEmpty(batchContent)) {
            attr.addAttribute("error_message", "数据不能为空");
            return "redirect:/zhishu/list";
        }
        batchContent = batchContent.trim();
        String[] arrs = batchContent.split("\n");

        int succLine = 0;
        int errorLine = 0;
        for (String line : arrs) {
            try {
                String[] arr = line.split("\\s+");
                if (arr.length != 4) {
                    log.error("error line: {}, len is: {}", line, arr.length);
                    errorLine++;
                    continue;
                }
                // 2015-12-11 4631.28	45.3500	3.8900
                ZhishuData data = new ZhishuData();
                data.setZhishuId(zhishuId);
                data.setPe(Double.valueOf(arr[2]));
                data.setPb(Double.valueOf(arr[3]));
                data.setShoupan(Double.valueOf(arr[1]));
                data.setDataDate(arr[0]);

                if (!isValidData(data)) {
                    errorLine++;
                    continue;
                }


                zhishuService.addData(data);
                succLine++;
            } catch (Exception e) {
                errorLine++;
                log.error("error line: {}", line, e);
            }
        }

        attr.addAttribute("success_message", "插入成功行数: " + succLine + ", 插入失败行数: " + errorLine);
        return "redirect:/zhishu/list";
    }

    @RequestMapping("sync_data_from_sws")
    public String syncDataFromSws(@RequestParam("zhishuId") long zhishuId,
                                  RedirectAttributes attr) {
        Zhishu zhishu = zhishuService.queryZhishu(zhishuId);
        if (zhishu == null) {
            attr.addAttribute("error_message", "该指数不存在");
            return "redirect:/zhishu/list";
        }

        String swsCode = zhishu.getSwsCode();
        if (StringUtils.isEmpty(swsCode)) {
            attr.addAttribute("error_message", "该指数不存在对应的申万code");
            return "redirect:/zhishu/list";
        }
        String lastDate = zhishuService.queryLastData(zhishuId);
        log.info("zhishu {} last date is {}", zhishu.getName(), lastDate);

        List<ZhishuData> datas = SwsDownloadUtils.parse(swsCode, zhishuId);
        log.info("sync from sws size: {}", datas.size());

        int succLine = 0;
        int errorLine = 0;
        int expireLine = 0;
        for (ZhishuData data : datas) {
            if (lastDate != null && lastDate.compareTo(data.getDataDate()) > 0) {
                expireLine++;
                continue;
            }
            if (!isValidData(data)) {
                errorLine++;
                continue;
            }
            zhishuService.addData(data);
            succLine++;
        }


        attr.addAttribute("success_message", "插入成功行数: " + succLine + ", 插入失败行数: "
                + errorLine + ", 过期行数: " + expireLine);
        return "redirect:/zhishu/list";
    }

    private boolean isValidData(ZhishuData data) {
        long time = CommonUtils.zhishuDateToTimestamp(data.getDataDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        long now = System.currentTimeMillis();
        if (now - time > 10L * 365 * 24 * 3600 * 1000) {
            log.info("too old data: {}", data.getDataDate());
            return false;
        }

        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week != Calendar.MONDAY) {
            log.info("not monday: {}", data.getDataDate());
            return false;
        }

        return true;
    }

    @RequestMapping("data")
    public ModelAndView data(@RequestParam("zhishuId") long zhishuId,
                             @RequestParam(value = "years", required = false) Long years,
                             @RequestParam(value = "from", required = false) String from,
                             @RequestParam(value = "to", required = false) String to) {
        ModelMap model = new ModelMap();
        String viewName = "zhishu/data";
        model.put("zhishu", zhishuService.queryZhishu(zhishuId));
        List<ZhishuData> datas = zhishuService.queryDatas(zhishuId);
        if (years != null) {
            long now = System.currentTimeMillis();
            for (Iterator<ZhishuData> it = datas.iterator(); it.hasNext(); ) {
                long time = CommonUtils.zhishuDateToTimestamp(it.next().getDataDate());
                if (now - time > years * 365 * 24 * 3600 * 1000L) it.remove();
            }
        }
        if (from != null && to != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            try {
                long fromTime = format.parse(from).getTime();
                long toTime = format.parse(to).getTime();
                for (Iterator<ZhishuData> it = datas.iterator(); it.hasNext(); ) {
                    long time = CommonUtils.zhishuDateToTimestamp(it.next().getDataDate());
                    if (time < fromTime || time > toTime) it.remove();
                }
            } catch (ParseException e) {
                log.error("parse time error", e);
            }
        }
        fillRank(datas);

        model.put("datas", datas);
        return new ModelAndView(viewName, model);
    }

    private void fillRank(List<ZhishuData> datas) {
        List<ZhishuData> copyDatas = Lists.newArrayList(datas);
        Collections.sort(copyDatas, new Comparator<ZhishuData>() {
            @Override
            public int compare(ZhishuData o1, ZhishuData o2) {
                double comp = o1.getPe() - o2.getPe();
                if (comp > 0) return 1;
                if (comp < 0) return -1;
                return 0;
            }
        });
        Map<Long, Integer> peRankMap = Maps.newHashMap();
        int i = 1;
        for (ZhishuData data : copyDatas) {
            peRankMap.put(data.getId(), i++);
        }

        Collections.sort(copyDatas, new Comparator<ZhishuData>() {
            @Override
            public int compare(ZhishuData o1, ZhishuData o2) {
                double comp = o1.getPb() - o2.getPb();
                if (comp > 0) return 1;
                if (comp < 0) return -1;
                return 0;
            }
        });
        Map<Long, Integer> pbRankMap = Maps.newHashMap();
        i = 1;
        for (ZhishuData data : copyDatas) {
            pbRankMap.put(data.getId(), i++);
        }

        for (ZhishuData data : datas) {
            data.setPeRank(peRankMap.get(data.getId()));
            data.setPbRank(pbRankMap.get(data.getId()));
        }
    }


}
