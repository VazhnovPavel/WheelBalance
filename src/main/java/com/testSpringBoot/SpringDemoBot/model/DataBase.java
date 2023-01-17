package com.testSpringBoot.SpringDemoBot.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
@Slf4j
@Component
public class DataBase {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<String> getMeanQuest(Long chatId) {
        List<String> result = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7); // to get the date of last 7 days
        SimpleDateFormat format1 = new SimpleDateFormat("dd_MM_yyyy");
        String startDate = format1.format(cal.getTime());
        String endDate = format1.format(Calendar.getInstance().getTime());
        String sql = "SELECT quest, ";
        for (int i = 0; i < 7; i++) {
            cal.add(Calendar.DATE, 1);
            String columnName = "date_" + format1.format(cal.getTime());
            sql += columnName + ", ";
        }
        sql = sql.substring(0, sql.length() - 2); // to remove last comma
        sql += " FROM data_base_quest WHERE chat_id = ?";

        jdbcTemplate.query(sql, new Object[]{chatId}, new RowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                String quest = rs.getString("quest");
                double total = 0;
                int count = 0;
                for (int i = 0; i < 7; i++) {
                    if (rs.getObject(i + 2) != null) { // checking if the value is not null
                        total += rs.getDouble(i + 2);
                        count++;
                    }
                }
                double mean = 0;
                if (count != 0) {
                    mean = total / count;
                    DecimalFormat df = new DecimalFormat("#.#");
                    result.add(quest + " " + df.format(mean));
                } else {
                    result.add(quest + " Пока нет данных");
                }
                return quest + " " + mean;
            }
        });
        System.out.println(result);
        return result;
    }
   /* public List<String> getMeanQuest(Long chatId) {
        List<String> result = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7); // to get the date of last 7 days
        SimpleDateFormat format1 = new SimpleDateFormat("dd_MM_yyyy");
        String startDate = format1.format(cal.getTime());
        String endDate = format1.format(Calendar.getInstance().getTime());
        String sql = "SELECT quest, ";
        for (int i = 0; i < 7; i++) {
            cal.add(Calendar.DATE, 1);
            String columnName = "date_" + format1.format(cal.getTime());
            sql += columnName + ", ";
        }
        sql = sql.substring(0, sql.length() - 2); // to remove last comma
        sql += " FROM data_base_quest WHERE chat_id = ?";

        jdbcTemplate.query(sql, new Object[]{chatId}, new RowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                String quest = rs.getString("quest");
                double total = 0;
                int count = 0;
                for (int i = 0; i < 7; i++) {
                    if (rs.getObject(i + 2) != null) { // checking if the value is not null
                        total += rs.getDouble(i + 2);
                        count++;
                    }
                }
                double mean = 0;
                if (count != 0) {
                     mean = total / count;
                    result.add(quest + " " + mean);
                } else {
                    result.add(quest + " No data available");
                }
                return quest + " " + mean;
            }
        });
        System.out.println(result);
        return result;
    }*/
}
