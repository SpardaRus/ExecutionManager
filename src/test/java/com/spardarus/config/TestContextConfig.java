package com.spardarus.config;

import com.spardarus.context.ContextThread;
import com.spardarus.context.ContextThreadImpl;
import com.spardarus.manager.ExecutionManagerImpl;
import com.spardarus.statistic.ExecutionStatisticsThread;
import com.spardarus.statistic.ExecutionStatisticsThreadImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContextConfig {

    @Bean
    public ExecutionManagerImpl getExecutionManagerImpl(ContextThread contextThread) {
        return new ExecutionManagerImpl(contextThread);
    }

    @Bean
    public ContextThread getContextThread(ExecutionStatisticsThread executionStatisticsThread) {
        return new ContextThreadImpl(executionStatisticsThread);
    }

    @Bean
    public ExecutionStatisticsThread getExecutionStatisticsThread() {
        return new ExecutionStatisticsThreadImpl();
    }
}
