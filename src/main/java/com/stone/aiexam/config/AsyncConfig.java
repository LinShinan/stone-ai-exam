package com.stone.aiexam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean("aiGradingExecutor")
    public Executor aiGradingExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);//4个常驻
        executor.setMaxPoolSize(8);//8个最大
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("ai-grade-");
        executor.initialize();
        return executor;
    }

}
