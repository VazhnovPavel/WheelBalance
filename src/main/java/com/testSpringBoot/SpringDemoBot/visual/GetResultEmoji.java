package com.testSpringBoot.SpringDemoBot.visual;

import org.springframework.stereotype.Component;

@Component
public class GetResultEmoji {

    public String getEmoji( double value,  boolean colorGreen) {
        final int emojiCount = (int)Math.round(value);
        final StringBuilder emoji = new StringBuilder();
        for (int i = 0; i < emojiCount; ++i) {
            emoji.append(colorGreen ? "🟢" : "⚪️");
        }
        return emoji.toString();

    }
}
