package com.spardarus.context;

import com.spardarus.statistic.ExecutionStatistics;
import com.spardarus.statistic.ExecutionStatisticsThread;
import org.springframework.stereotype.Service;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ContextThreadImpl implements ContextThread {

    private CopyOnWriteArrayList<Thread> threads;
    private CopyOnWriteArrayList<Long> threadsTimes = new CopyOnWriteArrayList<>();
    private AtomicInteger errorsCount = new AtomicInteger(0);
    private AtomicInteger interruptedCount = new AtomicInteger(0);
    private AtomicInteger finishTaskCount = new AtomicInteger(0);
    private int allThreadsCount = 0;

    private final ExecutionStatisticsThread executionStatistics;

    public ContextThreadImpl(ExecutionStatisticsThread executionStatistics) {
        this.executionStatistics = executionStatistics;
    }

    @Override
    public void setTreads(CopyOnWriteArrayList<Thread> threads) {
        initStart(threads);
        for (int i = 0; i < threads.size(); i++) {
            Thread thread = threads.get(i);
            Thread timerThread = new Thread(decorateTimerThread(thread));
            timerThread.setUncaughtExceptionHandler((Thread t, Throwable e) -> errorsCount.incrementAndGet());
            threads.set(i, timerThread);
        }
        this.threads = threads;
    }

    private void initStart(CopyOnWriteArrayList<Thread> threads) {
        allThreadsCount = threads.size();
        errorsCount = new AtomicInteger(0);
        interruptedCount = new AtomicInteger(0);
        finishTaskCount = new AtomicInteger(0);
        threadsTimes = new CopyOnWriteArrayList<>();
    }

    private Runnable decorateTimerThread(Thread thread) {
        return () -> {
            long start = System.currentTimeMillis();
            thread.run();
            threadsTimes.add(System.currentTimeMillis() - start);
        };
    }

    @Override
    public int getCompletedTaskCount() {
        synchronized (threads) {
            finishTaskCount = new AtomicInteger(0);
            for (int i = 0; i < threads.size(); i++) {
                Thread thread = threads.get(i);
                if (Thread.State.TERMINATED.equals(thread.getState())) {
                    finishTaskCount.incrementAndGet();
                }
            }
        }
        return finishTaskCount.get() - getFailedTaskCount();
    }

    @Override
    public int getFailedTaskCount() {
        return errorsCount.get();
    }

    @Override
    public int getInterruptedTaskCount() {
        return interruptedCount.get();
    }

    @Override
    public void interrupt() {
        synchronized (threads) {
            interruptedCount = new AtomicInteger(0);
            for (int i = 0; i < threads.size(); i++) {
                Thread thread = threads.get(i);
                if (Thread.State.NEW.equals(thread.getState())) {
                    threads.remove(i);
                    interruptedCount.incrementAndGet();
                }
            }
        }
    }

    @Override
    public boolean isFinished() {
        int finishedThreadsCount = getCompletedTaskCount() + getFailedTaskCount();
        return finishedThreadsCount == allThreadsCount
                || getInterruptedTaskCount() == allThreadsCount;
    }

    @Override
    public void onFinish(Runnable callback) {
        while (getCompletedTaskCount() + getFailedTaskCount() + getInterruptedTaskCount() != allThreadsCount) {
        }
        new Thread(callback).start();
    }

    @Override
    public ExecutionStatistics getStatistics() {
        executionStatistics.setThreadsTimes(threadsTimes);
        return executionStatistics;
    }

    @Override
    public void awaitTermination() {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        while (!isFinished()
                && (getInterruptedTaskCount() + finishTaskCount.get() != allThreadsCount)) {
        }
        lock.unlock();
    }
}
