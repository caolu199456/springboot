package com.example.util.factory.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 本类主要实现异步工作
 * @description: 描述
 * @author:      CL
 * @date:        2018/9/26 14:41
 * @version:     1.0
*/
public class Task {

    private final ReentrantLock lock = new ReentrantLock();

    private final Condition doneCondition = lock.newCondition();

    private volatile boolean isDone = false;

    private Object result;

    /**
     * 执行任务的线程池
     */
    private static final ExecutorService THREAD_POOL =
            new ThreadPoolExecutor(
                    5,
                    200,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(102400),
                    new ThreadFactoryBuilder().setNameFormat("task-pool-%d").build(),
                    new ThreadPoolExecutor.AbortPolicy()
                    )
            ;
    private Task() {

    }

    /**
     * 创建任务
     * @return
     */
    public static Task createTask() {
        return new Task();
    }

    /**
     * 异步执行如果需要返回值请调用get方法
     */
    public Task exec(TaskCallback callback) {
        THREAD_POOL.submit(() -> {
            try {
                lock.lock();
                result = callback.doSomething();
                isDone = true;
                doneCondition.signal();
            }finally {
                lock.unlock();
            }

        });
        return this;
    }

    public Object get() throws InterruptedException {
        return get(0,TimeUnit.MILLISECONDS);
    }

    public Object get(long timeout,TimeUnit timeUnit) throws InterruptedException {
        try {
            lock.lock();
            if (!isDone) {

                    if (timeout > 0) {
                        doneCondition.await(timeout, timeUnit);
                    } else {
                        doneCondition.await();
                    }
            }
        }catch (InterruptedException e){
            throw new InterruptedException("得到结果出错");
        }finally {
            lock.unlock();
        }
        return result;
    }


}
