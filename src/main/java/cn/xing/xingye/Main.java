package cn.xing.xingye;

import cn.xing.xingye.touzi.utils.Trace;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangxing on 15/12/2.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        //trace();

        String s = "hello Zhejiang University";
        List<String> stack = Lists.newArrayList();
        StringBuilder sb = new StringBuilder();
        boolean inword = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ' ' && inword) {
                inword = false;
                transBuff(stack, sb);
            } else if (c != ' ' && !inword) {
                inword = true;
                transBuff(stack, sb);
            }
            sb.append(c);
        }

        transBuff(stack, sb);

        for (int i=stack.size() - 1; i>=0; i--) {
            sb.append(stack.get(i));
        }

        System.out.println(sb.toString());



    }

    private static void transBuff(List<String> stack, StringBuilder sb) {
        if (sb.length() > 0) {
            stack.add(sb.toString());
            sb.delete(0, sb.length());
        }
    }

    private static void trace() throws InterruptedException {
        Trace.start("main");
        try {
            Thread.sleep(100);

            Trace.start("f1");
            try {
                Thread.sleep(200);

                Trace.start("f11");
                try {
                    Thread.sleep(100);

                    Trace.start("f111");
                    try {
                        Thread.sleep(100);
                    } finally {
                        Trace.end();
                    }

                } finally {
                    Trace.end();
                }

            } finally {
                Trace.end();
            }

            Trace.start("f2");
            try {
                Thread.sleep(200);

                Trace.start("f22");
                try {
                    Thread.sleep(100);

                    Trace.start("f222");
                    try {
                        Thread.sleep(100);
                    } finally {
                        Trace.end();
                    }

                } finally {
                    Trace.end();
                }

            } finally {
                Trace.end();
            }
        } finally {
            Trace.end();
        }
    }
}
