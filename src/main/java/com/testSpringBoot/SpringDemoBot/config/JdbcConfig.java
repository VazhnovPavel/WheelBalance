package com.testSpringBoot.SpringDemoBot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Bean;
import org.apache.commons.dbcp.BasicDataSource;
import javax.sql.DataSource;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdbcConfig
{
    private final Logger logger;

    public JdbcConfig() {
        this.logger = LoggerFactory.getLogger(JdbcConfig.class);
    }

    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.driver-class-name}") String driverClassName,
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) {
        logger.info("Creating DataSource bean");
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        logger.info("DataSource bean is created");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(final DataSource dataSource) {
        this.logger.info("Creating JdbcTemplate bean");
        return new JdbcTemplate(dataSource);
    }
}