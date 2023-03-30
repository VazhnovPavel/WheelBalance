package com.testSpringBoot.SpringDemoBot.statistic;

import com.testSpringBoot.SpringDemoBot.visual.GetResultEmoji;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class CompareWeekLastWeek  {
    @Autowired
    private LastWeekValues lastWeekValues;
    @Autowired
    private WeekValues weekValues;


    public String compareWeekAndLastWeek(Long chatId) {
    Map<String, Double> weekMap = weekValues.getMeanQuest(chatId);
    Map<String, Double> lastResultMap = lastWeekValues.getMeanQuest(chatId);
    StringBuilder mean = new StringBuilder("Сравниваем эту и предыдущую неделю:\n\n" +
            "🟢 – текущая неделя\n" +
            "⚪️ – предыдущая неделя\n");

    for (final String key : weekMap.keySet()) {
        mean.append("\n\n").append(key).append(" ").append(weekMap.get(key));
        mean.append(" (было ").append(lastResultMap.get(key)).append(")\n");
        int weekCircles = (int) Math.round(weekMap.get(key));
        for (int i = 0; i < weekCircles; i++) {
            mean.append("🟢");
        }
        if (lastResultMap.containsKey(key)) {
            mean.append("\n");
            int lastCircles = (int) Math.round(lastResultMap.get(key));
            for (int i = 0; i < lastCircles; i++) {
                mean.append("⚪️");
            }
        }
        mean.append("\n");
    }
    mean.append("\n Все команды - /help");
    return mean.toString();
}


}
