package com.testSpringBoot.SpringDemoBot.statistic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

/**
 * Этот класс отличается от CurrentStatValues, так как берет значения не из последних currentDays, а по формуле
 * ((currentDays * 2) - currentDays)
 * То есть, это данные запредыдущий период
 */

@Slf4j
@Component
public class PreviousStatValues {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CurrentStatValues currentStatValues;


    public Map<String, Double> getMeanQuest(Long chatId, int currentDays) {
        Map<String, Double> result = new HashMap<>();

        // Получаем список существующих столбцов
        List<String> columnNames = currentStatValues.getColumnNames(currentDays);

        // Формируем SQL запрос, используя только существующие столбцы
        String sql = "SELECT quest, " + String.join(", ", columnNames) + " FROM data_base_quest WHERE chat_id = ?";

        jdbcTemplate.query(sql, new Object[]{chatId}, new RowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                String quest = rs.getString("quest");
                double total = 0;
                int count = 0;

                for (int i = 0; i < columnNames.size(); i++) {
                    double value = rs.getDouble(columnNames.get(i));
                    if (!rs.wasNull()) {
                        total += value;
                        count++;
                    }
                }

                if (count != 0) {
                    double mean = total / count;
                    DecimalFormat df = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.US));
                    df.setRoundingMode(RoundingMode.HALF_UP);
                    result.put(quest, Double.parseDouble(df.format(mean)));
                }

                return quest;
            }
        });

        return result;
    }

}
