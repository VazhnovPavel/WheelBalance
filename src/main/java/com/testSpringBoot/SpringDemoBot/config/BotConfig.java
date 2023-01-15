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
    @Value("${bot.Name}")
    String botName;
    @Value("${bot.Token}")
    String token;
    @Value("${bot.Owner}")
    Long ownerId;
}


