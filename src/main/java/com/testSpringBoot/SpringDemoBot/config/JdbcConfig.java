package com.testSpringBoot.SpringDemoBot.config;

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
    public DataSource dataSource() {
        this.logger.info("Creating DataSource bean");
        System.out.println("Creating DataSource bean");
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/BalanceWheel");
        dataSource.setUsername("NikToRozeo");
        dataSource.setPassword("13241324");
        this.logger.info("Creating DataSource bean Success!");
        System.out.println("Creating DataSource bean Success!");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(final DataSource dataSource) {
        this.logger.info("Creating JdbcTemplate bean");
        System.out.println("Creating JdbcTemplate bean");
        return new JdbcTemplate(dataSource);
    }
}