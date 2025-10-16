package com.hospital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * 웹 MVC 설정
 * - JSP ViewResolver 설정
 * - CORS 설정 (필요시)
 * - 정적 리소스 설정 (필요시)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	/**
	 * JSP ViewResolver 설정
	 */
	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".jsp");
		resolver.setContentType("text/html; charset=UTF-8");
		System.out.println("✅ JSP ViewResolver 설정 완료");
		return resolver;
	}
}