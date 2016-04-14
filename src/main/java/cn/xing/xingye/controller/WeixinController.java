package cn.xing.xingye.controller;

import cn.xing.xingye.exception.WeixinException;
import cn.xing.xingye.model.WeixinButtonGroup;
import cn.xing.xingye.model.WeixinMenu;
import cn.xing.xingye.model.WeixinViewButton;
import cn.xing.xingye.service.WeixinService;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by indexing on 16/4/13.
 */
@Controller
@RequestMapping("/weixin")
public class WeixinController {
    final Logger LOG = LoggerFactory.getLogger(getClass());
    @Autowired
    private WeixinService weixinService;

    @RequestMapping("")
    public void index(@RequestParam("signature") String signature,
                      @RequestParam("timestamp") String timestamp,
                      @RequestParam("nonce") String nonce,
                      @RequestParam(value = "echostr", required = false) String echostr,
                      HttpServletRequest request,
                      HttpServletResponse response)
            throws IOException {
        LOG.info("http method: {}, content: {}", request.getMethod(), request.getParameterMap());
        if (!weixinService.checkSignature(signature, timestamp, nonce)) {
            LOG.error("check weixin signature failed!");
            return;
        }

        LOG.info("check weixin signature succ! echo: {}", echostr);
        if (!StringUtils.isEmpty(echostr)) {
            PrintWriter writer = response.getWriter();
            writer.write(echostr);
            writer.flush();
            return;
        }

    }

    @ResponseBody
    @RequestMapping("access_token")
    public String accessToken() throws WeixinException {
        return weixinService.readAccessToken().accessToken;
    }

    @ResponseBody
    @RequestMapping("create_menu")
    public String createMenu() throws WeixinException {
        WeixinMenu menu = new WeixinMenu();

        WeixinButtonGroup bookGroup = new WeixinButtonGroup("书籍推荐");
        bookGroup.addButton(new WeixinViewButton("指数基金投资", "http://www.duokan.com/book/101329"));
        bookGroup.addButton(new WeixinViewButton("投资最重要的事", "http://www.duokan.com/book/24392"));
        bookGroup.addButton(new WeixinViewButton("资产配置", "http://www.duokan.com/book/98831"));

        menu.addButton(bookGroup);
        weixinService.createMenu(menu);
        return "succ";
    }
}
