package com.testSpringBoot.SpringDemoBot.statistic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Этот класс отвечает за формирование статистики на основе недельных или месячных данных
 */
@Slf4j
@Component
public class EndStatisticFromCurrentPeriod {
    @Autowired
    private PreviousStatValues previousStatValues;
    @Autowired
    private CurrentStatValues currentStatValues;

    public String messageEndStatisticFromCurrentPeriod (Long chatId,int currentDays){

        //получаем значения текущего и предыдущего периода
        Map<String, Double> currentResultMap = currentStatValues.getMeanQuest(chatId, currentDays);
        Map<String, Double> previousResultMap = previousStatValues.getMeanQuest(chatId,currentDays);

        //получаем общую сумму всех категорий в сравнении периодов
        double totalSum = calculateTotalSum(previousResultMap, currentResultMap);

        //получаем макс и мин значения в сравнении периодов
        Map<String, Object> maxAndMinValues = findMaxAndMinCategory(previousResultMap, currentResultMap);
        String maxCategoryName = (String) maxAndMinValues.get("Max Category Name");
        double maxCategoryValue = (double) maxAndMinValues.get("Max Category Value");
        String minCategoryName = (String) maxAndMinValues.get("Min Category Name");
        double minCategoryValue = (double) maxAndMinValues.get("Min Category Value");

        DecimalFormat df = new DecimalFormat("0.0");
        df.setDecimalSeparatorAlwaysShown(true); // Показывать десятичный разделитель даже для целых чисел
        String formattedMaxCategoryValue = df.format(maxCategoryValue);
        String formattedMinCategoryValue = df.format(minCategoryValue);
        String formattedTotalSum = df.format(totalSum);

        //формируем сообщения пользователю на основе его значений
        StringBuilder mean = new StringBuilder();

        if (totalSum > 2 ) {
            mean.append("Продолжай в том же духе! Твой общий счет увеличился на " + formattedTotalSum +
                    " баллов за эту неделю, что свидетельствует о твоем прогрессе. \uD83D\uDE0A \n\n");
            for (int i = 0; i < (int) totalSum; i++) {
                mean.append("🟢");
            }
        }
        else if ((totalSum < 2) && (totalSum > 0) ) {
            mean.append("Хорошая работа! Твой общий счет увеличился на " + formattedTotalSum +
                    " баллов за неделю \uD83D\uDC4C \n\n");
            for (int i = 0; i < (int) totalSum; i++) {
                mean.append("🟢");
            }
        }
        else if (totalSum == 0) {
            mean.append("Четко! На этой неделе сумма твоих баллов не изменилась и равна " + formattedTotalSum +
                    " , у тебя все стабильно \uD83D\uDC4C \n\n");
                mean.append("⚪️");

        }
        else if (totalSum < 0) {
            mean.append("На этой неделе твой общий счет упал на " + formattedTotalSum +
                    " ,но это не повод для беспокойства. " +
                    "\nВероятно, ты переживаешь сложные времена или находишься в стадии переоценки" +
                    " своих ценностей. \uD83D\uDC99 \n\n");
            for (int i = 0; i < (int) Math.abs(totalSum); i++) {
                mean.append("🔴");
            }
        }
        mean.append("\n\nТы достиг наибольшего прогресса в категории \""+maxCategoryName+ "\",где твой рейтинг вырос на "
                + formattedMaxCategoryValue + " по сравнению с прошлым периодом \uD83D\uDC4D \n");
        for (int i = 0; i < (int) Math.abs(maxCategoryValue); i++) {
            mean.append("🟢");
        }
        mean.append("\n\nНаибольшие трудности возникли в категории \"" + minCategoryName + "\",где результат " +
                "снизился на " + formattedMinCategoryValue + " \uD83E\uDD37 \n\n");
        for (int i = 0; i < Math.abs(minCategoryValue); i++) {
            mean.append("🔴");
        }

        return mean.toString();
    }

    //Суммируем значения по категориям за текущий и предыдущий период.
    private double calculateTotalSum(Map<String, Double> previousResultMap, Map<String, Double> currentResultMap) {
        double totalSum = 0.0;

        for (Map.Entry<String, Double> entry : currentResultMap.entrySet()) {
            String key = entry.getKey();
            double currentValue = entry.getValue();

            if (previousResultMap.containsKey(key)) {
                double previousValue = previousResultMap.get(key);
                double sum = -(previousValue) + currentValue;
                totalSum += sum;
            }
        }

        return totalSum;
    }

    //Ищем категорию, в которой показатели сильнее всего изменились в плюс и в минус
   private Map<String, Object> findMaxAndMinCategory(Map<String, Double> previousResultMap, Map<String, Double> currentResultMap) {
       List<String> maxCategories = new ArrayList<>();
       List<String> minCategories = new ArrayList<>();
       double maxValueCategory = Double.NEGATIVE_INFINITY;
       double minValueCategory = Double.POSITIVE_INFINITY;

       for (Map.Entry<String, Double> entry : currentResultMap.entrySet()) {
           String key = entry.getKey();
           double currentValue = entry.getValue();

           if (previousResultMap.containsKey(key)) {
               double previousValue = previousResultMap.get(key);
               double sum = -(previousValue) + currentValue;

               if (sum > maxValueCategory) {
                   maxCategories.clear();
                   maxCategories.add(key);
                   maxValueCategory = sum;
               } else if (sum == maxValueCategory) {
                   maxCategories.add(key);
               }

               if (sum < minValueCategory) {
                   minCategories.clear();
                   minCategories.add(key);
                   minValueCategory = sum;
               } else if (sum == minValueCategory) {
                   minCategories.add(key);
               }
           }
       }

       String maxNameCategory = String.join(", ", maxCategories);
       String minNameCategory = String.join(", ", minCategories);

       Map<String, Object> maxAndMinValues = new HashMap<>();
       maxAndMinValues.put("Max Category Name", maxNameCategory);
       maxAndMinValues.put("Max Category Value", maxValueCategory);
       maxAndMinValues.put("Min Category Name", minNameCategory);
       maxAndMinValues.put("Min Category Value", minValueCategory);

       return maxAndMinValues;
   }


}
