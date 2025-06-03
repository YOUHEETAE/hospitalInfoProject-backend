package com.hospital.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;



@Configuration
@PropertySource("classpath:mainApi.properties")
@PropertySource("classpath:db.properties")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.hospital.repository")
public class AppConfig {
	@Bean
	public RestTemplate restTemplate() {
		
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(15000);
		factory.setReadTimeout(60000);
		return new RestTemplate(factory);		
	}
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}
	 @Bean
	    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	        return new PropertySourcesPlaceholderConfigurer();
	    }
	 // DataSource 빈 (JDBC Template과 JPA 모두 사용합니다. 이미 설정되어 있다면 이 부분은 건너뛰세요.)
	    // 만약 db.properties 또는 application.properties에 DB 연결 정보가 있다면 @Value로 주입받아 사용합니다.
	    @Bean
	    public DataSource dataSource(@Value("${jdbc.driverClassName}") String driverClassName,
	                                 @Value("${jdbc.url}") String url,
	                                 @Value("${jdbc.username}") String username,
	                                 @Value("${jdbc.password}") String password) {
	        DriverManagerDataSource dataSource = new DriverManagerDataSource();
	        dataSource.setDriverClassName(driverClassName);
	        dataSource.setUrl(url);
	        dataSource.setUsername(username);
	        dataSource.setPassword(password);
	        return dataSource;
	    }


	    // ★ JPA 설정 시작 ★

	    // 1. LocalContainerEntityManagerFactoryBean: JPA의 EntityManagerFactory를 생성하는 핵심 빈
	    @Bean
	    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
	        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
	        em.setDataSource(dataSource); // 위에서 정의한 DataSource 빈 주입
	        em.setPackagesToScan("com.hospital.entity"); // ★ JPA 엔티티가 위치한 패키지 지정 (Hospital, HospitalDetail 등)

	        // JPA 벤더 어댑터 설정 (Hibernate 사용)
	        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
	        em.setJpaVendorAdapter(vendorAdapter);

	        // 하이버네이트 속성 설정 (사용 DB, SQL 로깅 등)
	        java.util.Properties jpaProperties = new java.util.Properties();
	        // ★ 개발 환경에서만 'update'를 사용하고, 운영 환경에서는 'none' 또는 'validate'로 변경해야 합니다.
	        jpaProperties.setProperty("hibernate.hbm2ddl.auto", "update"); // 엔티티 기반으로 DB 스키마 자동 생성/업데이트
	        jpaProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect"); // 사용 DB에 맞는 Dialect
	                                                                                              // MariaDB를 사용하시니 MariaDBDialect를 사용합니다.
	                                                                                              // MySQL 8.x는 MySQL8Dialect
	        jpaProperties.setProperty("hibernate.show_sql", "true"); // 콘솔에 실행되는 SQL 쿼리 출력
	        jpaProperties.setProperty("hibernate.format_sql", "true"); // SQL 쿼리 포맷팅
	        // jpaProperties.setProperty("hibernate.use_sql_comments", "true"); // SQL 주석 추가 (디버깅에 유용)

	        em.setJpaProperties(jpaProperties);

	        return em;
	    }

	    // 2. JpaTransactionManager: JPA를 위한 트랜잭션 매니저
	    @Bean
	    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
	        JpaTransactionManager transactionManager = new JpaTransactionManager();
	        // .getObject()를 사용하여 실제 EntityManagerFactory 인스턴스를 주입합니다.
	        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
	        return transactionManager;
	    }
}
