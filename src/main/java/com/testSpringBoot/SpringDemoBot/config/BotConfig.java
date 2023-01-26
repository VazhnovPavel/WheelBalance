package com.testSpringBoot.SpringDemoBot.config;

import lombok.Data;
import lombok.Synchronized;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@Configuration
@EnableScheduling
@Data
@ComponentScan("com.testSpringBoot.SpringDemoBot.config")
public class BotConfig {
    @Value("vazhnov_test_1_bot")
    String botName;
    @Value("5454098510:AAHbjC1Z2O3HEab9O27xdngKdNn-fMvn4_E")
    String token;
    @Value("350511326")
    Long ownerId;
}


