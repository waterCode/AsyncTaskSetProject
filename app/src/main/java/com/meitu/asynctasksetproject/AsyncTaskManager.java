package com.meitu.asynctasksetproject;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zmc on 2017/6/7.
 */

public class AsyncTaskManager {
    private static final String TAG = "AsncTaskManger";
    private BlockingDeque<AsyncTaskSet> taskSetQueue = new LinkedBlockingDeque<>();
    private AsyncTaskSet mActive;//当前执行任务
    public static final MyExecutor executor;

    static {
        executor = new MyExecutor();
    }


    public void execute(AsyncTaskSet taskSet) {
        taskSetQueue.addFirst(taskSet);
        if (mActive == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    scheduleNext();
                }
            }).start();
        }
    }


    /*
    有毒，不同步后果很严重。。。。。。。。。。。。。。。。。。。。。。。。。。
     */
    private synchronized void scheduleNext() {
        Log.d(TAG, "excute");
        try {
            //拿到一个任务
            mActive = taskSetQueue.takeLast();
            //初始化障碍器
            CountDownLatch latch = new CountDownLatch(mActive.getSize());
            executor.setLatch(latch);
            //遍历任务，开始执行
            mActive.executeTasks();

            //阻塞当前线程
            latch.await();
            scheduleNext();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    public static class MyExecutor implements Executor {

        private final String TAG = "Myexecutor";
        CountDownLatch mLatch;

        @Override
        public void execute(@NonNull final Runnable command) {
            Log.d(TAG, "MyExecutor executor");
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try {
                        command.run();
                    } finally {
                        mLatch.countDown();
                    }
                }
            };
            //线程池去执行
            AsyncTask.THREAD_POOL_EXECUTOR.execute(task);
        }

        private void setLatch(CountDownLatch mLatch) {
            this.mLatch = mLatch;
        }
    }
}
