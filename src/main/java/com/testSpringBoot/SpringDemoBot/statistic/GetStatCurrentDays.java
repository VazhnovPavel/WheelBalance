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
public class GetStatCurrentDays {
    @Autowired
    private WeekValues weekValues;
    @Autowired
    private GetResultEmoji getResultEmoji;

    public String getStatFromCurrentDays(Long chatId,int currentDays) {
        try {
            Map<String, Double> weekMap = weekValues.getMeanQuest(chatId,currentDays);
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
}
