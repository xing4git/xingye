package cn.xing.xingye.touzi.controller;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Created by zhangxing on 15/12/2.
 */
@Controller
public class IndexController {
    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("*")
    public ModelAndView index() {
        Map<String, Object> model = Maps.newHashMap();
        model.put("time", System.currentTimeMillis());
        return new ModelAndView("index", model);
    }

    @RequestMapping("status")
    @ResponseBody
    public String status() {
        return "success";
    }
}
