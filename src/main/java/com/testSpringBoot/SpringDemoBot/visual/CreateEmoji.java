package com.testSpringBoot.SpringDemoBot.visual;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;

@Component
public class CreateEmoji {


    public String createFunnyEmoji(int answer) {
        String rating;
        String num1 = EmojiParser.parseToUnicode("\u0031\u20E3");
        String num2 = EmojiParser.parseToUnicode("\u0032\u20E3");
        String num3 = EmojiParser.parseToUnicode("\u0033\u20E3");
        String num4 = EmojiParser.parseToUnicode("\u0034\u20E3");
        String num5 = EmojiParser.parseToUnicode("\u0035\u20E3");
        String num6 = EmojiParser.parseToUnicode("\u0036\u20E3");
        String num7 = EmojiParser.parseToUnicode("\u0037\u20E3");
        String num8 = EmojiParser.parseToUnicode("\u0038\u20E3");
        String num9 = EmojiParser.parseToUnicode("\u0039\u20E3");
        String num10 = EmojiParser.parseToUnicode("\u0031\u0030\u20E3");

        switch (answer) {
            case 1:
                rating = num1;
                break;
            case 2:
                rating = num2;
                break;
            case 3:
                rating = num3;
                break;
            case 4:
                rating = num4;
                break;
            case 5:
                rating = num5;
                break;
            case 6:
                rating = num6;
                break;
            case 7:
                rating = num7;
                break;
            case 8:
                rating = num8;
                break;
            case 9:
                rating = num9;
                break;
            case 10:
                rating = num10;
                break;
            default:
                rating = "Invalid answer";
        }
        return rating;
    }
}
