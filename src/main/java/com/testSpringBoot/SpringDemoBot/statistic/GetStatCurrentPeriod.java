package com.testSpringBoot.SpringDemoBot.statistic;

import com.testSpringBoot.SpringDemoBot.visual.GetResultEmoji;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Выводим статистику за последние N дней
 * Среднее арифметическое всех значений в столбцах дат
 */
@Slf4j
@Component
public class GetStatCurrentPeriod {
    @Autowired
    private CurrentStatValues currentStatValues;
    @Autowired
    private GetResultEmoji getResultEmoji;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getStatFromCurrentDays(Long chatId, int currentDays) {
        try {
            Map<String, Double> weekMap = currentStatValues.getMeanQuest(chatId, currentDays);
            StringBuilder mean = new StringBuilder();
            for (Map.Entry<String, Double> entry : weekMap.entrySet()) {
                if (entry.getValue() != 0.0) {
                    mean.append(entry.getKey()).append(" ").append(entry.getValue());
                    String emoji = getResultEmoji.getEmoji(entry.getValue(), true);
                    mean.append("\n").append(emoji).append("\n");
                }
            }
            return "Среднее значение за последние " + currentDays + " дней: \n\n " + mean +
                    "\n Список всех статистик - /statistic";
        } catch (Exception e) {
            log.info("Ошибка" + e);
        }
        return "У нас какая-то проблемс";
    }


    // Первый метод возвращает список месяцев, сколко прошло с даты регистрации в формате: Месяц Год
    public List<String> getMonthsSinceRegistration(int daysRegistered) {
        List<String> monthsList = new ArrayList<>();
        LocalDate registrationDate = LocalDate.now().minusDays(daysRegistered);
        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

        while (!registrationDate.isAfter(currentDate)) {
            String monthYear = formatter.format(registrationDate);
            monthsList.add(monthYear);
            registrationDate = registrationDate.plusMonths(1);
        }

        log.info("Generated list of months: {}", monthsList);

        return monthsList;
    }

        //Второй метод собирает все значения из 2 и 1  метода в мапу.
        //результирующая мапа должна быть такой:
        //Ключ: MMMM yyyy
        //Значение: #.#

        public String getMonthStatFromCurrentCategory (Long chatId,int daysRegistered, String category){
            StringBuilder stringBuilder = new StringBuilder();
            List<String> monthsList = getMonthsSinceRegistration(daysRegistered);

            for (String monthYear : monthsList) {
                Double average = currentStatValues.getAverageFromCurrentMonth(chatId, monthYear, category);
                double averageValue = (average != null) ? average : 0.0;
                stringBuilder.append(monthYear).append(" - ").append(String.format("%.1f", averageValue)).append("; ");
            }

            if (stringBuilder.length() > 0) {
                stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
            }

            return stringBuilder.toString();
        }

}
