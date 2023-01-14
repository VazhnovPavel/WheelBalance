package com.testSpringBoot.SpringDemoBot.config;

import lombok.Data;
import lombok.Synchronized;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@Configuration
@EnableScheduling                 //есть методы, подлежащие авто запуску
@Data

//@PropertySource("application.properties")
public class BotConfig {
    @Value("${bot.Name}")
    String botName;
    @Value("${bot.Token}")
    String token;
    @Value("${bot.Owner}")
    Long ownerId;


   /* @Bean
    public SchedulerLock schedulerLock() {
        return  new SchedulerLock();
    }*/

}


