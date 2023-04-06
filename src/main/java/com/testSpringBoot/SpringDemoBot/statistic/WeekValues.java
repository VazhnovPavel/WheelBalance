package com.testSpringBoot.SpringDemoBot.statistic;

import com.testSpringBoot.SpringDemoBot.model.CreateDateColumn;
import io.quickchart.QuickChart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WeekValues  {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CreateDateColumn createDateColumn;




    /**
     * Делаем подсчет среднего арифметического по категориям за последние 7 дней
     */

    public Map<String, Double> getMeanQuest(Long chatId) {
        Map<String, Double> resultMap = new HashMap<>();
        List<String> columnNames = getColumnNames();
        String sql = buildSqlQuery(columnNames);
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
                    System.out.println(mean);
                    DecimalFormat df = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.US));
                    df.setRoundingMode(RoundingMode.HALF_UP);
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




    /**
     * Собираем в лист все столбцы за последние 7 дней
     * Если столбца не существует, отправляем в класс CreateDateColumn и создаем столбец
     */

    private List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7); // to get the date of last 7 days
        SimpleDateFormat format1 = new SimpleDateFormat("dd_MM_yyyy");
        for (int i = 0; i < 7; i++) {
            cal.add(Calendar.DATE, 1);
            String columnName = "date_" + format1.format(cal.getTime());
            try {
                jdbcTemplate.queryForObject("SELECT " + columnName + " FROM data_base_quest LIMIT 1", Object.class);
                // If the column exists, add it to the columnNames list
                columnNames.add(columnName);
            } catch (DataAccessException e) {
                // If the column does not exist, create it and then add it to the columnNames list
                log.warn("Column {} does not exist in table data_base_quest.", columnName);
                createDateColumn.addNewColumn(columnName);
                columnNames.add(columnName);
            }
        }
        return columnNames;
    }


    private String buildSqlQuery(List<String> columnNames) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT quest, ");
        for (String columnName : columnNames) {
            sb.append(columnName).append(", ");
        }
        sb.setLength(sb.length() - 2); // to remove last comma
        sb.append(" FROM data_base_quest WHERE chat_id = ?");
        return sb.toString();
    }
}


