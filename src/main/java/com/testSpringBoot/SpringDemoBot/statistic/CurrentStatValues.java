package com.testSpringBoot.SpringDemoBot.statistic;

import com.testSpringBoot.SpringDemoBot.model.CreateDateColumn;

import com.testSpringBoot.SpringDemoBot.model.MonthConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;


import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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

    public List<String> getColumnNames(int currentDays) {
        List<String> columnNames = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -currentDays); // собирает календарь за последние currentDays дней
        SimpleDateFormat format1 = new SimpleDateFormat("dd_MM_yyyy");
        for (int i = 0; i < currentDays; i++) {
            cal.add(Calendar.DATE, 1);
            String columnName = "date_" + format1.format(cal.getTime());
            try {
                jdbcTemplate.queryForObject("SELECT " + columnName + " FROM data_base_quest LIMIT 1", Object.class);
                // Если столбец существует, добавляем его в список
                columnNames.add(columnName);
            } catch (DataAccessException e) {
                // Если столбца нет, создаём его и добавляем в лист
                log.warn("Column {} does not exist in table data_base_quest.", columnName);
                createDateColumn.addNewColumn(columnName);
                columnNames.add(columnName);
            }
        }
        return columnNames;
    }


    /**
      Метод получает чат айди и дату месяца одного из столбцов. Он должен получить средннее арифметическое
      значение за тот месяц по определенной категории
     */

    public Double getAverageFromCurrentMonth(Long chatId, String monthYearColumn, String category) {
        YearMonth yearMonth = YearMonth.parse(monthYearColumn, DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        LocalDate today = LocalDate.now();
        // Если месяц еще не закончился, используйте текущую дату
        LocalDate lastDate = yearMonth.equals(YearMonth.from(today)) ? today : endOfMonth;

        StringBuilder sqlBuilder = new StringBuilder("SELECT ");
        int daysCount = 0;
        for (int day = 1; day <= lastDate.getDayOfMonth(); day++) {
            String columnName = "date_" + startOfMonth.withDayOfMonth(day).format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
            sqlBuilder.append(columnName);
            daysCount++;
            if (day < lastDate.getDayOfMonth()) {
                sqlBuilder.append(", ");
            }
        }

        sqlBuilder.append(" FROM data_base_quest WHERE quest = ? AND chat_id = ?");
        String sql = sqlBuilder.toString();

        int finalDaysCount = daysCount;
        return jdbcTemplate.queryForObject(sql, new Object[]{category, chatId}, (ResultSet rs, int rowNum) -> {
            double total = 0;
            int count = 0;
            for (int i = 1; i <= finalDaysCount; i++) {
                double value = rs.getDouble(i); // Here 'i' indexes the SQL query result columns, starting from 1
                if (!rs.wasNull()) {
                    total += value;
                    count++;
                }
            }
            return (count > 0) ? (total / count) : 0.0; // Return the mean or 0.0 if no values
        });
    }

//    public Double getAverageFromCurrentMonth(Long chatId, String monthYearColumn, String category) {
//        YearMonth yearMonth = YearMonth.parse(monthYearColumn, DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));
//        LocalDate startOfMonth = yearMonth.atDay(1);
//        LocalDate endOfMonth = yearMonth.atEndOfMonth();
//
//        // Build SQL Query
//        StringBuilder sqlBuilder = new StringBuilder("SELECT ");
//        for (int day = 1; day <= endOfMonth.getDayOfMonth(); day++) {
//            String columnName = "date_" + startOfMonth.withDayOfMonth(day).format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
//            sqlBuilder.append(columnName).append(", ");
//        }
//        // Remove the last comma and space
//        sqlBuilder.setLength(sqlBuilder.length() - 2);
//
//        sqlBuilder.append(" FROM data_base_quest WHERE quest = ? AND chat_id = ?");
//
//        String sql = sqlBuilder.toString();
//
//        return jdbcTemplate.queryForObject(sql, new Object[]{category, chatId}, (ResultSet rs, int rowNum) -> {
//            double total = 0;
//            int count = 0;
//            for (int day = 1; day <= endOfMonth.getDayOfMonth(); day++) {
//                double value = rs.getDouble(day);
//                if (!rs.wasNull()) {
//                    total += value;
//                    count++;
//                }
//            }
//            return count > 0 ? total / count : 0.0;
//        });
//    }

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


