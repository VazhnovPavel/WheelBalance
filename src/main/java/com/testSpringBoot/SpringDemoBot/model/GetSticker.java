package com.testSpringBoot.SpringDemoBot.model;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
@Component
public class GetSticker {

    public SendSticker addStiker (String quest, Long chatId){
        SendSticker sticker = new SendSticker();
        sticker.setChatId(chatId);
        InputFile inputFile = null;
        switch (quest){
            case "Деньги, капитал":
                 inputFile = new InputFile("CAACAgUAAxkBAAEHXrNjyw-ZJOTr0YnWHUKGltGxpxRFaQACSwIAAiHE3BFsPf7OlvgO2C0E");
                 break;
            case "Работа":
                inputFile = new InputFile("CAACAgIAAxkBAAEHXrljyxFRaqNjzMQ1Hgd2PLtzLE-jBQACmAUAAiMFDQABoc8KAXKxsyItBA");
                break;
            case "Материальный мир":
                inputFile = new InputFile("CAACAgIAAxkBAAEHXrdjyxEsYnOeZhzv4x3IgCzBA-OjNwACNAADvbJxDgrMJeM54-p8LQQ");
                break;
            case "Отношения":
                inputFile = new InputFile("CAACAgIAAxkBAAEHXrFjyw8MpLYUk0SpNnx-StxZlqivbQACaR4AAh3tYUkpdZKPeRAC_i0E");
                break;
            case "Здоровье":
                inputFile = new InputFile("CAACAgIAAxkBAAEHXr1jyxL1p01ql55mgXbVqgepZ0ZryAACzBQAAgrXyUsa7GgoHs1N_y0E");
                break;
            case "Семья":
                inputFile = new InputFile("CAACAgIAAxkBAAEHXqtjyw6TMZoLQKYbMQSh67AqI_lRQgACM8oAAmOLRgxBEQpv2eeE0i0E");
                break;
            case "Развлечения":
                inputFile = new InputFile("CAACAgIAAxkBAAEHXqljyw5ruytX1tyOnpAdoN14LXYDnQACNAAD8J60JPC9Kn5AbuITLQQ");
                break;
            case "Друзья":
                inputFile = new InputFile("CAACAgIAAxkBAAEHXqdjyw49-raG63P8gY6TNoGpTRlgfgACEAADvbJxDi3qQYYlbuFeLQQ");
                break;
            case "Саморазвитие":
                inputFile = new InputFile("CAACAgIAAxkBAAEHXoxjywNcMyfPF7WHS7NuHLkkC20nMwACcQUAAiMFDQABqr8z5dgL2qotBA");
                break;
            case "Внешность":
                inputFile = new InputFile("CAACAgIAAxkBAAEHXqVjyw36EJO4tZ0DxDEaus2cUBT-yQACTgUAAiMFDQABpYn5wcj-AAEMLQQ");

        }
        sticker.setSticker(inputFile);

        return sticker;
    }
}
