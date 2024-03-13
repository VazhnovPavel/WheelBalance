package com.testSpringBoot.SpringDemoBot.visual;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.Random;

@Component
public class GetSticker {

    public SendSticker addStiker (String quest, Long chatId){
        SendSticker sticker = new SendSticker();
        sticker.setChatId(chatId);
        InputFile inputFile = null;
        Random random = new Random();
        int stickerNum = random.nextInt(3) + 1; // генерируем случайное число от 1 до 3
        switch (quest){
            case "Деньги, капитал":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgQAAxkBAAELoLVl6SaN3gIfeU7htbe0H8MyynrtLwACPwEAAj9vzQtTP7k3bK9lcjQE");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAELoLdl6SbjIiN_TmxHa6ShJ5vuc9hLiAAC6RUAApiqmEgcx3kHpFw_QTQE");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAELrcdl8UQLPR7VjvqV7cA_X7npNN7-VAACtAkAAvFCvwVdgSdK67S70DQE");
                        break;
                }
                 break;
            case "Работа":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAELn4Vl6E3a0RFAbEDfuC7i1jl0ttGfxgACzRsAAu2-oUolEkrNyYe6ejQE");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAELn6ll6FtQtldVNXLw_gABMm6grJs8uEoAAngJAALxQr8FzJbgGdE27Ao0BA");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAELn7Nl6F0Q-NRjewa_H4hCZm1q4FrFtgAC3TAAAmbVsEtM8jwMxHlh3jQE");
                        break;
                }
                break;
            case "Бытовой комфорт":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAELn4dl6E6jwNtH0IPLCXZzp6wlhUyCjwACW7EBAAFji0YM7yIrhkzlMOg0BA");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAELn5hl6FpXeQM8Zdg3x1AHceaqwzYh9gACZzkAAnmKwUv7fhaXVQPk9zQE");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJyU9kvYuNxNL1xqAMvdL7gmPoM1WdFQACay4AAmTkoEhx4IDi1WOIoS8E");
                        break;
                }                break;
            case "Отношения":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgUAAxkBAAELn5Rl6FlSooxoyQ7uTVIn1GuJu0L2UwACgQsAAl42qVT-eyoBAAGaHYo0BA");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAELn5Zl6FoMxcOBhDXM823jFp8yoEa8uQACtzYAAjBFwUsj026wX_NhHzQE");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgQAAxkBAAELoMFl6SiH_oByaW_7Y3pblrdGwR9bfgAC9gwAAicccVAbAWD7RlrDTTQE");
                        break;
                }                break;
            case "Здоровье":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAELn5pl6FqRCe0TlGY3toKpnYTdFkG3wgACxRIAAl7-0EkojaLQ7rSlAzQE");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAELoMdl6SmZkA26XKoMAi7RQ3RBOU5YoQACjTsAArlskUjF8qK1hI5WwDQE");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgQAAxkBAAELrcFl8UJbtY3Var2GwmEHvnRYirix9gACPA4AAldwAVKz82h80sRPcjQE");
                        break;
                }
                break;
            case "Семья":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgQAAxkBAAELn7Fl6FyqT3nQBqv2GNMTgOQZcSsF3AACsg4AAoCleFBCMz8BsIpH8DQE");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAELoMll6SnCBbZvm7nrqeTf6dfxyeG0fwACXLEBAAFji0YM0UcZPo3MOfc0BA");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgQAAxkBAAELrcll8URED_71ssjn8E32d5vhX5EqqQACIxAAAtuS-FHW6Ccse3q3ZzQE");
                        break;
                }
                break;
            case "Развлечения":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAELn45l6FBQsf9PT3Ng-wj51jTN8fIRDgACbicAAhKDuEsCL3ZftJR79DQE");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAELn5Jl6FjhERS_IzCehqqEfgwwAAGjivEAAp0lAALnRUFLeebqp_hvYjI0BA");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgQAAxkBAAELn6dl6FsPgFklTQqBBx7V_fVcugbKQwACIwsAAnmgEVKkenBwp8tQ6jQE");
                        break;
                }
                break;
            case "Друзья":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAELn4ll6E_L8AcFh3VyacyWCgiv46oo7wACQQADg6PsFXn2EDtr1w0rNAQ");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAELn5Bl6FjMvpM3ngyiy6Ix04ZHTfcGMQACOyUAApstQUt0K34-BrT25zQE");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgQAAxkBAAELrb9l8UJZ4ED1Hs3NQxXA-N5ca3TkoAACFwwAAsESaVHl3tZHz0RcEDQE");
                        break;
                }
                break;
            case "Саморазвитие":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAELn69l6FwB2FwN-c16sREvr882nkdhXwAC7BwAAmePoEo04gyPb4FY0zQE");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAELoMNl6Sj6LBLqd_QH1qckglDIO7RPPgACBwUAAvFCvwVGiPEpEStPeDQE");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgQAAxkBAAELoMVl6SkpOwMqeUdAzx6_IQUM4TKfagACKQsAAiNi6VAVFuou85HJijQE");
                        break;
                }
                break;
            case "Внешность":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAELn61l6FvIrm-F5ESMD92OSWlT80TcwQACOxoAAhLYoUp0O14wgS66NTQE");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgUAAxkBAAELoL1l6SgLQgZOxuw7u06lgXTBNfjUVQACrg8AAqQuqVTk4h6dYUxWIDQE");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAELrcNl8UMAAd0y-9EAARA6vw8Drof-4ZwBAAJKJAACcBlBSyydWdwAARn_EzQE");
                        break;
                }
                break;
        }
        sticker.setSticker(inputFile);

        return sticker;
    }
}
