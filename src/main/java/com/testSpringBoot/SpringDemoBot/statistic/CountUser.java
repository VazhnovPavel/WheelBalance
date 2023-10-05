package com.testSpringBoot.SpringDemoBot.statistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CountUser {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int countDeadUserToday() {
        // SQL-запрос для подсчета пользователей, зарегистрированных сегодня
        String registeredTodayQuery = "SELECT COUNT(*) FROM all_user_data WHERE DATE(registered_at) = " +
                "CURRENT_DATE";

        try {
            // Получаем количество пользователей, зарегистрированных сегодня
            int registeredTodayCount = jdbcTemplate.queryForObject(registeredTodayQuery, Integer.class);

            // Выводим результат
            log.info("Количество пользователей, зарегистрированных сегодня и с непустым time_to_questions: {}", registeredTodayCount);

            return registeredTodayCount;
        } catch (Exception e) {
            log.error("Ошибка при выполнении SQL-запроса: {}", e.getMessage());
            return -1; // В случае ошибки вернуть -1 или другое значение по умолчанию
        }
    }


    public int countUserToday() {
        // SQL-запрос для подсчета пользователей, у которых time_to_questions не равно null
        String questionsNotNullQuery = "SELECT COUNT(*) FROM all_user_data WHERE DATE(registered_at) = " +
                "CURRENT_DATE AND time_to_questions IS NOT NULL";

        try {
            // Получаем количество пользователей, у которых time_to_questions не равно null
            int questionsNotNullCount = jdbcTemplate.queryForObject(questionsNotNullQuery, Integer.class);

            return questionsNotNullCount;
        } catch (Exception e) {
            log.error("Ошибка при выполнении SQL-запроса: {}", e.getMessage());
            return -1; // В случае ошибки вернуть -1 или другое значение по умолчанию
        }
    }
}
