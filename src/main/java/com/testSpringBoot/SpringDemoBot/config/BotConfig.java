package com.testSpringBoot.SpringDemoBot.config;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling                 //есть методы, подлежащие авто запуску
@Data
//@PropertySource("application.properties")
public class BotConfig {
    @Value("${bot.Name}")
    String botName;
    @Value("${bot.Token}")
    String token;
}
