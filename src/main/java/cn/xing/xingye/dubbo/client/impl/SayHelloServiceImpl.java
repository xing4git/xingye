package cn.xing.xingye.dubbo.client.impl;

import cn.xing.xingye.dubbo.client.SayHelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by indexing on 2017/12/28.
 */
@Service("sayHelloService")
public class SayHelloServiceImpl implements SayHelloService {
    static Logger LOG = LoggerFactory.getLogger(SayHelloServiceImpl.class);

    @Override
    public void sayHi() {
        LOG.info("hello world");
    }
}
