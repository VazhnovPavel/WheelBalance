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
                        inputFile = new InputFile("CAACAgIAAxkBAAEJgFRkmxzMXLejmtSS39YT3ta8UJEZVgACXAEAAhZ8aAMx8cbqRyVdmy8E");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgttkMcE5eumc3of5sXlIFjLQ9Sk0hwACaAADSMbXDdRsUi1yMNGbLwQ");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgt9kMcGJ6D7bMlQcoj7uBUg2nQFp9gACwwADAvupH1YYLl5ZH93oLwQ");
                        break;
                }
                 break;
            case "Работа":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEHgkdj1sMoldigxV0tU5oYxjycaNAKiQACw1gBAAFji0YM2xFsYmsNXkktBA");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJBHFkZTo6gVlaCKfci4yqWKL09OD_7AACQQADg6PsFXn2EDtr1w0rLwQ");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJgFZkmx43u6de0qX08mY8MZrMXzuE1AACcgADfI5YFeY0_6_miPtrLwQ");
                        break;
                }
                break;
            case "Материальный мир":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJgFxkmx_IbS-wfooRg0O6pJjmr2xPrwACWgADfI5YFVERlXx2Itx6LwQ");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJgF5kmyEXrhvnYf1U7hnWL458u2tEZAACbQEAAhZ8aAO7FHHGC0EvrC8E");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgutkMcRfas4-qivZaHzQYXFzxkYbIgACgxcAApDu8EmjEXVCZqJfny8E");
                        break;
                }                break;
            case "Отношения":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJBHNkZTrNsV2jeF32jWxZyswimxMl3QACgQADg6PsFTCGvXMAAb-x1y8E");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJgGBkmyFXzpMyDehkGLFOsB7-SfpGhgACMwEAAhZ8aANN39rzSRsa6S8E");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJgGJkmyGwfZvSfIlV-CIpIjj94uIrXQACmQUAAiMFDQABsahsRc-HWwsvBA");
                        break;
                }                break;
            case "Здоровье":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgvNkMcWJRYyCCS09SeJzSIfnBcH2cwACzBQAAgrXyUsa7GgoHs1N_y8E");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJgGRkmyJ3dX4YyO-MneWba5yhOtbe6gACaQADfI5YFdcudqr_fOFRLwQ");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgvtkMcYy3f1V86kemUPW5nkwT31HQwACigADfI5YFYYrEzj9KKvHLwQ");
                        break;
                }
                break;
            case "Семья":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJgGZkmyLBQmZQTIIcMVwnuy0KlGRXGgAChwADfI5YFXK3Uw8uFywPLwQ");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgv9kMcap4qHxRB8VSkLefJ5xlrzuNwACMBQAAuvuKEjQJW_nQuqF-S8E");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJgGhkmyLdZywGc40HFQABf3z4430-gKsAAgYBAAIWfGgDX8Vs46Q-6CMvBA");
                        break;
                }
                break;
            case "Развлечения":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJgGpkmyM7LvF3DVeh68A_gnAcHMlZ7QAC5h0AApQCMEj0W8nLG5FF3i8E");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgwdkMcjZRC5avqqdY6ZlClFip2zmEAACoxIAAvDTKEgWBWUL8yPbiy8E");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJgGxkmyN0BylAtlOtk7QUFKC3zfwvsgACoxwAAoAuOEvqHWrKJag9Ai8E");
                        break;
                }
                break;
            case "Друзья":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJgHRkmyR1D9Jwk2lMY-Pngr6Fe1uhdwACiQADAvupHzxc66-sxzcdLwQ");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgw1kMcn8_bH44OwlfP7goSRo0TdUMwACGBoAAmu2IUhEVEbUFq0OOy8E");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgw9kMcpXwU9BCFsExWj6c8X0hUcu8wACwgsAAi7vsUmJbcMsLm4KlS8E");
                        break;
                }
                break;
            case "Саморазвитие":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgxFkMcqCB7DgxC7_fLzZsZipxJpjrwACcQUAAiMFDQABqr8z5dgL2qovBA");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJBspkZmHrYrvuFv7LwjNSGAMe7oVG7wACSQsAAs5z4EpErIJnMh-jLS8E");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJBHpkZTyyKy9pJdB9SaXYrJlv-UQ0fQACbysAArPGsEioRp6ogvcJSy8E");
                        break;
                }
                break;
            case "Внешность":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJgH1kmyWMZoYC_H9KGv0mqdaOO8nLYgAC3BsAArg32UjaSz1SMjazzi8E");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIhjVkMx6D9U8TC65imkqm6BR6xW2amgACMgUAAiMFDQABe6yTTsfJ4MYvBA");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJgH9kmyXeFpQoZF2wta-T5he8gzjz2QACbQADfI5YFX0VqFGUXd1cLwQ");
                        break;
                }
                break;
        }
        sticker.setSticker(inputFile);

        return sticker;
    }
}
