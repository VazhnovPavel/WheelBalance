package com.testSpringBoot.SpringDemoBot.statistic;

import com.testSpringBoot.SpringDemoBot.model.CreateDateColumn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/*
@Slf4j
@Component
public class WeekValues {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CreateDateColumn createDateColumn;

    public Map<String, Double> getMeanQuest(Long chatId) {
        Map<String, Double> resultMap = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7); // to get the date of last 7 days
        SimpleDateFormat format1 = new SimpleDateFormat("dd_MM_yyyy");
        String startDate = format1.format(cal.getTime());
        String endDate = format1.format(Calendar.getInstance().getTime());
        String sql = "SELECT quest, ";
        List<String> columnNames = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            cal.add(Calendar.DATE, 1);
            String columnName = "date_" + format1.format(cal.getTime());
            try {
                jdbcTemplate.queryForObject("SELECT " + columnName + " FROM data_base_quest LIMIT 1", Object.class);
                // If the column exists, add it to the SELECT statement and columnNames list
                sql += columnName + ", ";
                columnNames.add(columnName);
            } catch (DataAccessException e) {
                // If the column does not exist, skip it
                log.warn("Column {} does not exist in table data_base_quest.", columnName);
            }
        }
        sql = sql.substring(0, sql.length() - 2); // to remove last comma
        sql += " FROM data_base_quest WHERE chat_id = ?";
        jdbcTemplate.query(sql, new Object[]{chatId}, new RowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                String quest = rs.getString("quest");
                double total = 0;
                int count = 0;
                for (int i = 0; i < columnNames.size(); i++) {
                    if (rs.getObject(columnNames.get(i)) != null) { // checking if the value is not null
                        total += rs.getDouble(columnNames.get(i));
                        count++;
                    }
                }
                double mean = 0;
                if (count != 0) {
                    mean = total / count;
                    DecimalFormat df = new DecimalFormat("#.#");
                    resultMap.put(quest, Double.parseDouble(df.format(mean)));
                } else {
                    resultMap.put(quest, 0.0);
                }
                return quest + " " + mean;
            }
        });
        System.out.println(resultMap);
        return resultMap;
    }

}*/

@Slf4j
@Component
public class WeekValues {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CreateDateColumn createDateColumn;

    public Map<String, Double> getMeanQuest(Long chatId) {
        Map<String, Double> resultMap = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7); // to get the date of last 7 days
        SimpleDateFormat format1 = new SimpleDateFormat("dd_MM_yyyy");
        String startDate = format1.format(cal.getTime());
        String endDate = format1.format(Calendar.getInstance().getTime());
        String sql = "SELECT quest, ";
        List<String> columnNames = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            cal.add(Calendar.DATE, 1);
            String columnName = "date_" + format1.format(cal.getTime());
            try {
                jdbcTemplate.queryForObject("SELECT " + columnName + " FROM data_base_quest LIMIT 1", Object.class);
                // If the column exists, add it to the SELECT statement and columnNames list
                sql += columnName + ", ";
                columnNames.add(columnName);
            } catch (DataAccessException e) {
                // If the column does not exist, create it and then add it to the SELECT statement and columnNames list
                log.warn("Column {} does not exist in table data_base_quest.", columnName);
                createDateColumn.addNewColumn(columnName);
                sql += columnName + ", ";
                columnNames.add(columnName);
            }
        }
        sql = sql.substring(0, sql.length() - 2); // to remove last comma
        sql += " FROM data_base_quest WHERE chat_id = ?";
        jdbcTemplate.query(sql, new Object[]{chatId}, new RowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                String quest = rs.getString("quest");
                double total = 0;
                int count = 0;
                for (int i = 0; i < columnNames.size(); i++) {
                    if (rs.getObject(columnNames.get(i)) != null) { // checking if the value is not null
                        total += rs.getDouble(columnNames.get(i));
                        count++;
                    }
                }
                double mean = 0;
                if (count != 0) {
                    mean = total / count;
                    DecimalFormat df = new DecimalFormat("#.#");
                    resultMap.put(quest, Double.parseDouble(df.format(mean)));
                } else {
                    resultMap.put(quest, 0.0);
                }
                return quest + " " + mean;
            }
        });
        System.out.println(resultMap);
        return resultMap;
    }
}

