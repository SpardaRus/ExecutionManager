package com.spardarus.context;

import com.spardarus.config.TestContextConfig;
import com.spardarus.manager.ExecutionManager;
import com.spardarus.statistic.ExecutionStatistics;
import com.spardarus.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestContextConfig.class)
public class ContextThreadImplTest {

    private static final int TASK_COUNT = 6000;

    @Autowired
    private ExecutionManager executionManager;

    @Test(timeout = 100000L)
    public void getCompletedTaskCount() {
        int threadErrorCount = 2;
        int completedTaskCount = TASK_COUNT - threadErrorCount;

        Task[] tasks = getTasks(TASK_COUNT);
        Context context = executionManager.execute(tasks);
        context.awaitTermination();
        assertTrue(context.getCompletedTaskCount() == completedTaskCount);
    }

    @Test(timeout = 100000L)
    public void getFailedTaskCount() {
        int threadErrorCount = 2;

        Task[] tasks = getTasks(TASK_COUNT);
        Context context = executionManager.execute(tasks);
        context.awaitTermination();
        assertTrue(context.getFailedTaskCount() == threadErrorCount);
    }

    @Test(timeout = 100000L)
    public void interrupt() {
        Task[] tasks = getTasks(TASK_COUNT);
        Context context = executionManager.execute(tasks);
        context.interrupt();
        context.awaitTermination();
        assertTrue(context.getCompletedTaskCount() + context.getFailedTaskCount() + context.getInterruptedTaskCount() == TASK_COUNT);
    }

    @Test(timeout = 100000L)
    public void isFinished() {
        Task[] tasks = getTasks(TASK_COUNT);
        Context context = executionManager.execute(tasks);
        context.awaitTermination();
        assertTrue(context.isFinished());
    }

    @Test(timeout = 100000L)
    public void onFinish() {
        Task[] tasks = getTasks(TASK_COUNT);
        Context context = executionManager.execute(tasks);
        AtomicInteger count = new AtomicInteger(0);
        context.onFinish(() -> {
            count.set(2);
        });
        while (context.getCompletedTaskCount() + context.getFailedTaskCount() + context.getInterruptedTaskCount() != TASK_COUNT
                && count.get() != 2) {
        }
        try {
            TimeUnit.SECONDS.sleep(1L);
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(2, count.get());
    }

    @Test(timeout = 100000L)
    public void awaitTermination() {
        Task[] tasks = getTasks(TASK_COUNT);
        Context context = executionManager.execute(tasks);
        System.out.println("Start Thread: " + Thread.currentThread());
        context.awaitTermination();
        System.out.println("End Thread: " + Thread.currentThread());
        assertTrue(context.isFinished());
    }

    @Test(timeout = 100000L)
    public void getStatistics() {
        Task[] tasks = getTasks(TASK_COUNT);
        Context context = executionManager.execute(tasks);
        ExecutionStatistics statistics = context.getStatistics();
        context.awaitTermination();
        int minExecutionTimeInMs = statistics.getMinExecutionTimeInMs();
        int maxExecutionTimeInMs = statistics.getMaxExecutionTimeInMs();
        int averageExecutionTimeInMs = statistics.getAverageExecutionTimeInMs();
        assertTrue(minExecutionTimeInMs <= maxExecutionTimeInMs);
        assertTrue(minExecutionTimeInMs <= averageExecutionTimeInMs
                && maxExecutionTimeInMs >= averageExecutionTimeInMs);
    }

    private Task[] getTasks(int count) {
        Task[] tasks = new Task[count];
        for (int i = 0; i < count; i++) {
            tasks[i] = new Task(i);
        }
        return tasks;
    }
}
