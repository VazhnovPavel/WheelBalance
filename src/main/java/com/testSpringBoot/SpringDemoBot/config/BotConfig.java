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
    @Value("Wheel_Balance_bot")
    String botName;
    @Value("5929802437:AAFO4WkYk46YdbAV8hnq51dH1nI-khNESc4")
    String token;
    @Value("350511326")
    Long ownerId;
}


