package com.testSpringBoot.SpringDemoBot.statistic;

import com.testSpringBoot.SpringDemoBot.model.CreateDateColumn;

import com.testSpringBoot.SpringDemoBot.model.MonthConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;


import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;


@Slf4j
@Component
public class CurrentStatValues {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CreateDateColumn createDateColumn;


    /**
     * Делаем подсчет среднего арифметического по категориям за последние N дней
     */

    public Map<String, Double> getMeanQuest(Long chatId,int currentDays) {
        Map<String, Double> resultMap = new HashMap<>();
        List<String> columnNames = getColumnNames(currentDays);
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
     * Собираем в лист все столбцы за последние N дней
     * Если столбца не существует, отправляем в класс CreateDateColumn и создаем столбец
     */

    private List<String> getColumnNames(int currentDays) {
        List<String> columnNames = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -currentDays); // to get the date of last N days
        SimpleDateFormat format1 = new SimpleDateFormat("dd_MM_yyyy");
        for (int i = 0; i < currentDays; i++) {
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



    public Map<String, Double> getMeanQuest(Long chatId, int year, String month) {
        Map<String, Double> resultMap = new HashMap<>();
        List<String> columnNames = getColumnNamesForYearAndMonth(year, month);
        String sql = buildSqlQuery(columnNames);
        jdbcTemplate.query(sql, new Object[]{chatId}, new RowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                String quest = rs.getString("quest");
                double total = 0;
                int count = 0;
                for (int i = 0; i < columnNames.size(); i++) {
                    if (rs.getObject(columnNames.get(i)) != null) { // Проверяем, не является ли значение null
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

    public static List<String> getColumnNamesForYearAndMonth(int year, String monthString) {
        List<String> columnNames = new ArrayList<>();

        // Преобразование строки месяца в объект Month
        Month month = MonthConverter.getMonth(monthString);


        // Определение количества дней в месяце
        int daysInMonth = java.time.YearMonth.of(year, month).lengthOfMonth();

        // Генерация имен столбцов для всех дней в месяце
        for (int day = 1; day <= daysInMonth; day++) {
            String dayStr = String.format("%02d", day); // Форматируем день в виде "DD"
            String monthStr = String.format("%02d", month.getValue()); // Форматируем месяц в виде "MM"
            String yearStr = String.valueOf(year); // Год в виде "YYYY"
            String columnName = "date_" + dayStr + "_" + monthStr + "_" + yearStr;
            columnNames.add(columnName);
        }

        return columnNames;
    }
}


