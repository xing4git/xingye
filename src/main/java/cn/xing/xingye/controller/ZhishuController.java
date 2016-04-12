package cn.xing.xingye.controller;

import cn.xing.xingye.editor.TimestampEditor;
import cn.xing.xingye.model.ZhishuData;
import cn.xing.xingye.service.ZhishuService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
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

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Timestamp.class, new TimestampEditor(new SimpleDateFormat("yyyyMMdd")));
    }

    @RequestMapping(value = {"", "list"})
    public ModelAndView zhishu() {
        ModelMap model = new ModelMap();
        String viewName = "zhishu/list";
        model.put("zhishus", zhishuService.queryZhishus());
        return new ModelAndView(viewName, model);
    }

    @RequestMapping("add_data")
    public String addData(ZhishuData data, RedirectAttributes attr) {
        zhishuService.addData(data);
        attr.addAttribute("success_message", "success!");
        return "redirect:/zhishu/list";
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
                long time = it.next().getDataDate().getTime();
                if (now - time > years * 365 * 24 * 3600 * 1000L) it.remove();
            }
        }
        if (from != null && to != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            try {
                long fromTime = format.parse(from).getTime();
                long toTime = format.parse(to).getTime();
                for (Iterator<ZhishuData> it = datas.iterator(); it.hasNext(); ) {
                    long time = it.next().getDataDate().getTime();
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
