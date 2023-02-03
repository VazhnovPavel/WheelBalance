package com.testSpringBoot.SpringDemoBot.statistic;

import com.testSpringBoot.SpringDemoBot.visual.GetResultEmoji;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class CompareWeekLastWeek {
    @Autowired
    private LastWeekValues lastWeekValues;
    @Autowired
    private WeekValues weekValues;
    @Autowired
    private GetResultEmoji getResultEmoji;

    public String compareWeekAndLastWeek( Long chatId) {
        boolean colorGreen = false;
        Map<String, Double> weekMap = weekValues.getMeanQuest(chatId);
        Map<String, Double> lastResultMap = lastWeekValues.getMeanQuest(chatId);
        StringBuilder mean = new StringBuilder("Сравниваем эту и предыдущую неделю:\n");
        for (final String key : weekMap.keySet()) {
            mean.append("\n\n_______________________________________________________\n");
            mean.append("\n").append(key).append(" ").append(weekMap.get(key));
            colorGreen = true;
            mean.append("\n").append(getResultEmoji.getEmoji(weekMap.get(key), colorGreen));
            if (lastResultMap.containsKey(key) && lastResultMap.get(key) != 0.0) {
                mean.append("\nНа прошлой неделе  ").append(lastResultMap.get(key));
                colorGreen = false;
                mean.append("\n").append(getResultEmoji.getEmoji(lastResultMap.get(key), colorGreen));
            }
        }
        mean.append("\n\n Все команды - /help");
        return mean.toString();
    }
}
