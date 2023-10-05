package com.testSpringBoot.SpringDemoBot.statistic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class PeriodHasData {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<Integer> getRegistrationYears(long chatID) {
        List<Integer> registrationYears = new ArrayList<>();
        try {
            // Выполняем SQL-запрос для получения года регистрации пользователя по chat_id
            String sql = "SELECT EXTRACT(YEAR FROM registered_at) FROM all_user_data WHERE chat_id = ?";
            int registrationYear = jdbcTemplate.queryForObject(sql, new Object[]{chatID}, Integer.class);

            // Получаем текущий год
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);

            // Добавляем все годы с года регистрации до текущего года в список
            for (int year = registrationYear; year <= currentYear; year++) {
                registrationYears.add(year);
            }

        } catch (EmptyResultDataAccessException e) {
            // Обработка случая, если пользователя с указанным chat_id не найдено
            // Можно выбрать подходящую стратегию обработки ошибки, например, бросить исключение или вернуть пустой список
            // В данном примере вернем пустой список
        }

        return registrationYears;
    }

    public List<String> getRegistrationMonths(long chatID, int year) {
        List<String> registrationMonths = new ArrayList<>();
        try {
            // Ваш SQL-запрос для получения месяца регистрации пользователя по chat_id и указанному году
            String sql = "SELECT EXTRACT(MONTH FROM registered_at) FROM all_user_data WHERE chat_id = ? AND EXTRACT(YEAR FROM registered_at) = ?";
            List<Integer> monthList = jdbcTemplate.queryForList(sql, new Object[]{chatID, year}, Integer.class);

            LocalDate currentDate = LocalDate.now();
            int currentYear = currentDate.getYear();
            Month currentMonth = currentDate.getMonth();

            // Добавляем названия всех месяцев от января до текущего месяца минус один (текущий месяц исключается)
            for (int monthInt = 1; monthInt < currentMonth.getValue(); monthInt++) {
                Month month = Month.of(monthInt);
                String monthName = month.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));
                registrationMonths.add(monthName);
            }
        } catch (EmptyResultDataAccessException e) {
            // Обработка случая, если пользователя с указанным chat_id не найдено или нет записей для указанного года
            // Можно выбрать подходящую стратегию обработки ошибки, например, бросить исключение или вернуть пустой список
            // В данном примере вернем пустой список
        }

        return registrationMonths;
    }

}