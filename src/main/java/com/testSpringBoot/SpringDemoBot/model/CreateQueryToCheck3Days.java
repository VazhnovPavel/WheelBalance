package com.testSpringBoot.SpringDemoBot.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CreateQueryToCheck3Days {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CreateDateColumn createDateColumn;

    public String sql(Long chat_id) {
        log.info("Executing query to get quest");
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate dayBeforeYesterday = today.minusDays(2);
        String formattedTodayDate = today.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String formattedYesterdayDate = yesterday.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String formattedDayBeforeYesterdayDate = dayBeforeYesterday.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String sql = "SELECT quest, quest_string FROM data_base_quest WHERE chat_id = ?";
        String[] dateColumns = new String[]{formattedTodayDate, formattedYesterdayDate, formattedDayBeforeYesterdayDate};
        List<String> existingDateColumns = new ArrayList<>();
        for (String column : dateColumns) {
            String checkIfExistsSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'data_base_quest' AND COLUMN_NAME = 'date_" + column + "'";
            int count = jdbcTemplate.queryForObject(checkIfExistsSql, Integer.class);
            if (count > 0) {
                existingDateColumns.add("date_" + column);
            } else {
                // If column doesn't exist, add new column
                createDateColumn.addNewColumn("date_" + column);
                existingDateColumns.add("date_" + column);
            }
        }
        if (!existingDateColumns.isEmpty()) {
            sql += " AND (" + String.join(" IS NULL AND ", existingDateColumns) + " IS NULL)";
        }
        sql += " ORDER BY random() LIMIT 1";
        try {
            Object[] params = new Object[]{chat_id};
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params);
            // process the result set
        } catch (DataAccessException e) {
            // handle the exception and continue running
            log.warn("Failed to execute SQL query: {}", e.getMessage());
        }
        return sql;
    }

    public String sqlToday(Long chat_id) {
        log.info("Executing query to get empty cells count");
        LocalDate today = LocalDate.now();
        String formattedTodayDate = today.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String sqlToday = "SELECT quest FROM data_base_quest WHERE chat_id = ? AND (date_" + formattedTodayDate
                + " IS NULL ) ";
        return sqlToday;
    }
}
