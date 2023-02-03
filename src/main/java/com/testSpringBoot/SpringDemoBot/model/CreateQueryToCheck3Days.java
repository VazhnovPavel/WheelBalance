package com.testSpringBoot.SpringDemoBot.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Slf4j
@Component
public class CreateQueryToCheck3Days {


    public String sql(Long chat_id) {
        log.info("Выполнение запроса для получения квеста");
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate dayBeforeYesterday = today.minusDays(2);
        String formattedTodayDate = today.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String formattedYesterdayDate = yesterday.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String formattedDayBeforeYesterdayDate = dayBeforeYesterday.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String sql = "SELECT quest,quest_string FROM data_base_quest WHERE chat_id = ? AND (date_" + formattedTodayDate
                + " IS NULL AND date_" + formattedYesterdayDate
                + " IS NULL AND date_" + formattedDayBeforeYesterdayDate
                + " IS NULL) ORDER BY random() LIMIT 1";
        return sql;
    }

    public String sqlToday(Long chat_id) {
        log.info("Выполнение запроса для получения квеста");
        LocalDate today = LocalDate.now();
        String formattedTodayDate = today.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String sqlToday = "SELECT quest FROM data_base_quest WHERE chat_id = ? AND (date_" + formattedTodayDate
                + " IS NULL ) ";
        return sqlToday;
    }



}