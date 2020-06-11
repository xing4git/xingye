package cn.xing.xingye.dubbo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by indexing on 2017/12/28.
 */
public class ProviderMain {
    static Logger LOG = LoggerFactory.getLogger(ProviderMain.class);

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[]{"classpath:spring/provider.xml"});
        context.start();
        LOG.info("provider started");
        System.in.read();
    }
}
