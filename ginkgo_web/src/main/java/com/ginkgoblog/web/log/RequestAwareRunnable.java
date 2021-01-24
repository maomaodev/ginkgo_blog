package com.ginkgoblog.web.log;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 异步记录日志父类
 *
 * @author maomao
 * @date 2021-01-24
 */
public abstract class RequestAwareRunnable implements Runnable {
    private final RequestAttributes requestAttributes;
    private Thread thread;

    public RequestAwareRunnable() {
        this.requestAttributes = RequestContextHolder.getRequestAttributes();
        this.thread = Thread.currentThread();
    }

    @Override
    public void run() {
        try {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            onRun();
        } finally {
            if (Thread.currentThread() != thread) {
                RequestContextHolder.resetRequestAttributes();
            }
            thread = null;
        }
    }

    protected abstract void onRun();
}
