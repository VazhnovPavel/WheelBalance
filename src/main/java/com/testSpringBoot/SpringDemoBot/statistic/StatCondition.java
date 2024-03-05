package com.testSpringBoot.SpringDemoBot.statistic;

import org.springframework.stereotype.Component;


@Component
public class StatCondition {

    public enum CategoryState {
        MONTH_CATEGORY,
        SPECIFIC_CATEGORY,
        ALL
    }
}
