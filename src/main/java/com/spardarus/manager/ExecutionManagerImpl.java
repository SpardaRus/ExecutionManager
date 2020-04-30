package com.spardarus.manager;

import com.spardarus.context.Context;
import com.spardarus.context.ContextThread;
import org.springframework.stereotype.Service;

import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ExecutionManagerImpl implements ExecutionManager {

    private final ContextThread contextThread;

    public ExecutionManagerImpl(ContextThread contextThread) {
        this.contextThread = contextThread;
    }

    public Context execute(Runnable... tasks) {
        CopyOnWriteArrayList<Thread> threads = new CopyOnWriteArrayList<>();
        for (Runnable task : tasks) {
            threads.add(new Thread(task));
        }
        contextThread.setTreads(threads);
        new Thread(getStartThreadsRunnable(threads)).start();

        return contextThread;
    }

    private Runnable getStartThreadsRunnable(CopyOnWriteArrayList<Thread> threads) {
        return () -> {
            for (int i = 0; i < threads.size(); i++) {
                synchronized (threads) {
                    Thread thread = threads.get(i);
                    thread.start();
                }
            }
        };
    }

}
