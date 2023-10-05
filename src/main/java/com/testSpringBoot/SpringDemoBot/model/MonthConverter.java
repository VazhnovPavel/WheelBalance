package com.testSpringBoot.SpringDemoBot.model;

import java.time.Month;
import java.util.HashMap;
import java.util.Map;

public class MonthConverter {
    private static final Map<String, Month> monthMap = new HashMap<>();

    static {
        monthMap.put("ЯНВАРЬ", Month.JANUARY);
        monthMap.put("ФЕВРАЛЬ", Month.FEBRUARY);
        monthMap.put("МАРТ", Month.MARCH);
        monthMap.put("АПРЕЛЬ", Month.APRIL);
        monthMap.put("МАЙ", Month.MAY);
        monthMap.put("ИЮНЬ", Month.JUNE);
        monthMap.put("ИЮЛЬ", Month.JULY);
        monthMap.put("АВГУСТ", Month.AUGUST);
        monthMap.put("СЕНТЯБРЬ", Month.SEPTEMBER);
        monthMap.put("ОКТЯБРЬ", Month.OCTOBER);
        monthMap.put("НОЯБРЬ", Month.NOVEMBER);
        monthMap.put("ДЕКАБРЬ", Month.DECEMBER);
    }

    public static Month getMonth(String monthString) {
        return monthMap.get(monthString.toUpperCase());
    }
}