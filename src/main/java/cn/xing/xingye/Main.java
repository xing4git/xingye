package cn.xing.xingye;

import cn.xing.xingye.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangxing on 15/12/2.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        byte[] bytes = HttpClientUtils.post("https://www.jisilu.cn/data/sfnew/funda_list/", "");
        log.info(new String(bytes, "utf8"));
    }
}
