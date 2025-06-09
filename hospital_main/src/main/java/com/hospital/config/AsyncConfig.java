package com.hospital.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {
	 @Bean(name = "proDocExecutor")
	    public Executor taskExecutor() {
	        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	        executor.setCorePoolSize(20);
	        executor.setMaxPoolSize(50);
	        executor.setQueueCapacity(2000);
	        executor.setThreadNamePrefix("ProDoc-Thread-");

	        // ✨ 큐가 가득 찼을 때 현재 스레드가 직접 실행하게 처리
	        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

	        executor.initialize();
	        return executor;
	    }

}
