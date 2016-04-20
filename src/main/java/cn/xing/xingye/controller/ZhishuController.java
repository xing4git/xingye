package cn.xing.xingye.controller;

import cn.xing.xingye.model.Zhishu;
import cn.xing.xingye.utils.CommonUtils;
import cn.xing.xingye.model.ZhishuData;
import cn.xing.xingye.service.ZhishuService;
import cn.xing.xingye.utils.SwsDownloadUtils;
import cn.xing.xingye.utils.XingConst;
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
            attr.addAttribute(XingConst.KEY_ERROR_MSG, "指数名称不能为空");
            return "redirect:/zhishu/list";
        }
        if (swsCode == null) swsCode = "";
        zhishuService.addZhishu(zhishuName, swsCode);
        attr.addAttribute(XingConst.KEY_SUCCESS_MSG, "添加指数: " + zhishuName + "成功!");
        return "redirect:/zhishu/list";
    }

    @RequestMapping("add_data")
    public String addData(ZhishuData data, RedirectAttributes attr) {
        if (!CommonUtils.isValidZhishuDate(data.getDataDate())) {
            attr.addAttribute(XingConst.KEY_ERROR_MSG, "invalid date: " + data.getDataDate());
            return "redirect:/zhishu/list";
        }
        zhishuService.addData(data);
        attr.addAttribute(XingConst.KEY_SUCCESS_MSG, "success!");
        return "redirect:/zhishu/list";
    }

    @RequestMapping("add_data_batch")
    public String addDataBatch(@RequestParam("zhishuId") long zhishuId,
                               @RequestParam("batchContent") String batchContent,
                               RedirectAttributes attr) {
        if (StringUtils.isEmpty(batchContent)) {
            attr.addAttribute(XingConst.KEY_ERROR_MSG, "数据不能为空");
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

                if (!zhishuService.isValidData(data)) {
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

        attr.addAttribute(XingConst.KEY_SUCCESS_MSG, "插入成功行数: " + succLine + ", 插入失败行数: " + errorLine);
        return "redirect:/zhishu/list";
    }

    @RequestMapping("sync_data_from_sws")
    public String syncDataFromSws(@RequestParam("zhishuId") long zhishuId,
                                  RedirectAttributes attr) {
        try {
            zhishuService.syncDataFromSws(zhishuId);
        } catch (Exception e) {
            attr.addAttribute(XingConst.KEY_ERROR_MSG, e.getMessage());
        }

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
        zhishuService.fillRank(datas);

        model.put("datas", datas);
        return new ModelAndView(viewName, model);
    }
}
