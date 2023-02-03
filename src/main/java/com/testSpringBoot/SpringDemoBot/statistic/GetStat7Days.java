package com.testSpringBoot.SpringDemoBot.statistic;

import com.testSpringBoot.SpringDemoBot.visual.GetResultEmoji;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
/**
 * Выводим статистику за последние 7 дней
 * Среднее арифметическое всех значений в столбцах дат
 */
@Slf4j
@Component
public class GetStat7Days {
    @Autowired
    private LastWeekValues lastWeekValues;
    @Autowired
    private WeekValues weekValues;
    @Autowired
    private GetResultEmoji getResultEmoji;

    public String getStatFrom7days(Long chatId) {
        try {
            Map<String, Double> weekMap = weekValues.getMeanQuest(chatId);
            StringBuilder mean = new StringBuilder();
            for (Map.Entry<String, Double> entry : weekMap.entrySet()) {
                if (entry.getValue() != 0.0) {
                    mean.append(entry.getKey()).append(" ").append(entry.getValue());
                    String emoji = getResultEmoji.getEmoji(entry.getValue(), true);
                    mean.append("\n").append(emoji).append("\n");
                }
            }
            return "Среднее значение за последние 7 дней: \n\n " + mean +
                    "\n Сравнить с предыдущей неделей - /compareWeek";
        } catch (Exception e) {
            log.info("Ошибка" + e);
        }
        return "У нас какая-то проблемс";
    }
}
