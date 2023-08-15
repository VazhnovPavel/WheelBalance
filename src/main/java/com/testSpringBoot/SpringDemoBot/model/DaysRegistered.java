package com.testSpringBoot.SpringDemoBot.model;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Duration;

import java.time.LocalDateTime;

@Slf4j
@Component
public class DaysRegistered {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    public long daysUserRegistered(Long chatId) {
        String checkRegisteredAt = "SELECT registered_at FROM all_user_data WHERE chat_id = ?";
        Timestamp registeredAt = jdbcTemplate.queryForObject(checkRegisteredAt, Timestamp.class, chatId);

        if (registeredAt != null) {
            LocalDateTime registrationDateTime = registeredAt.toLocalDateTime();
            LocalDateTime currentDateTime = LocalDateTime.now();
            Duration duration = Duration.between(registrationDateTime, currentDateTime);
            return duration.toDays();
        } else {
            log.info("Пользователь с данным chat_id не зарегистрирован", chatId);
            return -1; // возвращаем значение -1, если регистрация не была пройдена
        }
    }
}
