package cn.xing.xingye.touzi.utils;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by indexing on 16/9/19.
 */
public class Trace {
    private static ThreadLocal<Tracer> stack = new ThreadLocal<>();

    public static void start(String traceName) {
        Tracer tracer = new Tracer(traceName, getCurrentTracer());
        tracer.start();
        setCurrentTracer(tracer);
    }

    public static void end() {
        Tracer tracer = getCurrentTracer();
        if (tracer == null) {
            return;
        }
        tracer.end();

        System.out.println(tracer.dump());
        setCurrentTracer(tracer.getParent());
    }

    private static Tracer getCurrentTracer() {
        return stack.get();
    }

    private static void setCurrentTracer(Tracer tracer) {
        stack.set(tracer);
    }

    private static class Tracer {
        private String traceId;
        private String rpcId;
        private String traceName;
        private long startTime;
        private long endTime;
        private boolean ended;
        private Tracer parent;
        private AtomicInteger subRpcId = new AtomicInteger(1);

        public Tracer(String traceName, Tracer parent) {
            this.traceName = traceName;
            this.parent = parent;
            if (parent != null) {
                this.traceId = parent.traceId;
                this.rpcId = parent.rpcId + "." + parent.subRpcId.getAndIncrement();
            } else {
                this.traceId = genTraceId();
                this.rpcId = "1";
            }
        }

        private String genTraceId() {
            return UUID.randomUUID().toString();
        }

        public void start() {
            this.startTime = System.currentTimeMillis();
        }

        public void end() {
            ended = true;
            endTime = System.currentTimeMillis();
        }

        public boolean isEnded() {
            return ended;
        }

        public Tracer getParent() {
            return parent;
        }

        public String dump() {
            if (!ended) return "not end";
            StringBuilder sb = new StringBuilder();
            sb.append("Tracer: ").append(traceId).append("|")
                    .append(rpcId).append("|")
                    .append(traceName).append("|")
                    .append(startTime).append("|")
                    .append(endTime).append("|")
                    .append(endTime - startTime);
            return sb.toString();
        }
    }
}
