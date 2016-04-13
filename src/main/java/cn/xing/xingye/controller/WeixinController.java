package cn.xing.xingye.controller;

import cn.xing.xingye.model.WeixinConfig;
import cn.xing.xingye.utils.DigestUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

/**
 * Created by indexing on 16/4/13.
 */
@Controller
@RequestMapping("/weixin")
public class WeixinController {
    final Logger LOG = LoggerFactory.getLogger(getClass());
    @Autowired
    private WeixinConfig config;

    @ResponseBody
    @RequestMapping("")
    public String index(@RequestParam("signature") String signature,
                        @RequestParam("timestamp") String timestamp,
                        @RequestParam("nonce") String nonce,
                        @RequestParam("echostr") String echostr
    ) {
        List<String> strs = Lists.newArrayList(config.token, timestamp, nonce);
        Collections.sort(strs);
        String s = StringUtils.collectionToDelimitedString(strs,"");
        s = DigestUtils.sha1(s);

        LOG.info("signature from weixin: {}, generate sign: {}", signature, s);
        if (s.equals(signature)) {
            return echostr;
        }
        return echostr;
    }
}
