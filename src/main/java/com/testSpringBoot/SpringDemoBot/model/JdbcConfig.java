package com.testSpringBoot.SpringDemoBot.model;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcConfig {
    private final Logger logger = LoggerFactory.getLogger(JdbcConfig.class);

    @Bean
    public DataSource dataSource() {
        logger.info("Creating DataSource bean");
        System.out.println("Creating DataSource bean");
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/BalanceWheel");
        dataSource.setUsername("NikToRozeo");
        dataSource.setPassword("13241324");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        logger.info("Creating JdbcTemplate bean");
        System.out.println("Creating JdbcTemplate bean");
        return new JdbcTemplate(dataSource);
    }
}



