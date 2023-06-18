package com.testSpringBoot.SpringDemoBot.statistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class CompareCurrentAndPreviousPeriod {
    @Autowired
    private PreviousStatValues previousStatValues;
    @Autowired
    private CurrentStatValues currentStatValues;


    public String compareWeekAndLastWeek(Long chatId,int currentDays, String currentText) {
    Map<String, Double> currentResultMap = currentStatValues.getMeanQuest(chatId,currentDays);
    Map<String, Double> previousResultMap = previousStatValues.getMeanQuest(chatId,currentDays);
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

    for (final String key : currentResultMap.keySet()) {
        mean.append("\n").append(key).append(" ").append(currentResultMap.get(key));
        if (previousResultMap.get(key) == null){
            mean.append("\n");
        }
        else {
            mean.append(" (было ").append(previousResultMap.get(key)).append(")\n");
        }
        int weekCircles = (int) Math.round(currentResultMap.get(key));
        for (int i = 0; i < weekCircles; i++) {
            mean.append("🟢");
        }
        if (previousResultMap.containsKey(key)) {
            mean.append("\n");
            int lastCircles = (int) Math.round(previousResultMap.get(key));
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
