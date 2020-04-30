package com.spardarus.statistic;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ExecutionStatisticsThreadImpl implements ExecutionStatisticsThread {

    private CopyOnWriteArrayList<Long> threadsTimes;

    @Override
    public int getMinExecutionTimeInMs() {
        return Collections.min(threadsTimes).intValue();
    }

    @Override
    public int getMaxExecutionTimeInMs() {
        return Collections.max(threadsTimes).intValue();
    }

    @Override
    public int getAverageExecutionTimeInMs() {
        return (int) threadsTimes.stream()
                .mapToLong(time -> time)
                .average()
                .getAsDouble();
    }

    @Override
    public void setThreadsTimes(CopyOnWriteArrayList<Long> threadsTimes) {
        this.threadsTimes = threadsTimes;
    }
}
