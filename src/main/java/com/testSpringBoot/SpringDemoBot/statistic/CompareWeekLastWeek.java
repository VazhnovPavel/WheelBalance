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


    public String compareWeekAndLastWeek(Long chatId,int currentDays, String currentText) {
    Map<String, Double> weekMap = weekValues.getMeanQuest(chatId,currentDays);
    Map<String, Double> lastResultMap = lastWeekValues.getMeanQuest(chatId);
        String firstTime = null;
        String lastTime = null;
    if (currentDays == 7){
         firstTime = "– текущая неделя\n";
         lastTime = "– предыдущая неделя\n";
    }
    else if (currentDays == 30){
        firstTime = "– текущий месяц\n";
        lastTime = "– предыдущий месяц\n";
    }

    StringBuilder mean = new StringBuilder(currentText +
            "🟢 " + firstTime +
            "⚪️ " + lastTime);

    for (final String key : weekMap.keySet()) {
        mean.append("\n").append(key).append(" ").append(weekMap.get(key));
        if (lastResultMap.get(key) == null){
            //mean.append(" (пока нет данных)\n");
            mean.append("\n");
        }
        else {
            mean.append(" (было ").append(lastResultMap.get(key)).append(")\n");
        }
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
    mean.append("\nСписок всех статистик - /statistic");
    return mean.toString();
}
}
