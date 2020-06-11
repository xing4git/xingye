package cn.xing.xingye.dubbo;

import cn.xing.xingye.dubbo.client.SayHelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by indexing on 2017/12/28.
 */
public class ConsumerMain {
    static Logger LOG = LoggerFactory.getLogger(ConsumerMain.class);

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[]{"classpath:spring/consumer.xml"});
        context.start();
        SayHelloService sayHelloService = context.getBean(SayHelloService.class);
        sayHelloService.sayHi();
    }
}
