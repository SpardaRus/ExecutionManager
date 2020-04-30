package com.spardarus.statistic;

import java.util.concurrent.CopyOnWriteArrayList;

public interface ExecutionStatisticsThread extends ExecutionStatistics{

    void setThreadsTimes(CopyOnWriteArrayList<Long> threadsTimes);

}
