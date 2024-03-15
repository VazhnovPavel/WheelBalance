package com.testSpringBoot.SpringDemoBot.statistic.Administrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    public int countAllUser() {

        String allUsersQuery = "SELECT COUNT(*) FROM all_user_data";

        try {
            int allUsersCount = jdbcTemplate.queryForObject(allUsersQuery, Integer.class);
            return allUsersCount;
        } catch (Exception e) {
            log.error("Ошибка при выполнении SQL-запроса: {}", e.getMessage());
            return -1; // В случае ошибки вернуть -1 или другое значение по умолчанию
        }
    }

    public int countAllActiveUser() {

        String activeUsersQuery = "SELECT COUNT(*) FROM all_user_data WHERE time_to_questions IS NOT NULL";

        try {
            int activeUsersCount = jdbcTemplate.queryForObject(activeUsersQuery, Integer.class);
            return activeUsersCount;
        } catch (Exception e) {
            log.error("Ошибка при выполнении SQL-запроса: {}", e.getMessage());
            return -1; // В случае ошибки вернуть -1 или другое значение по умолчанию
        }
    }

    public int countActiveUserToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
        String todayDate = "date_" + dateFormat.format(new Date());
        String query = "SELECT COUNT(*) FROM data_base_quest WHERE " + todayDate + " != 0";

        try {
            int count = jdbcTemplate.queryForObject(query, Integer.class);
            log.info("count = " + count);
            int result = Math.round(count / 3); // Используем Math.round для округления к ближайшему целому
            return result;
        } catch (Exception e) {
            log.error("Ошибка при выполнении SQL-запроса: {}", e.getMessage());
            return -1; // В случае ошибки вернуть -1
        }
    }

    public int countActiveUserToday(Date specificDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
        String date = "date_" + dateFormat.format(specificDate);
        String query = "SELECT COUNT(*) FROM data_base_quest WHERE " + date + " != 0";

        try {
            int count = jdbcTemplate.queryForObject(query, Integer.class);
            log.info("count = " + count);
            return Math.round(count / 3);
        } catch (Exception e) {
            log.error("Ошибка при выполнении SQL-запроса: {}", e.getMessage());
            return -1;
        }
    }

















}
