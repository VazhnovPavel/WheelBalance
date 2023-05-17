package com.testSpringBoot.SpringDemoBot.visual;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
                        inputFile = new InputFile("CAACAgUAAxkBAAEHXrNjyw-ZJOTr0YnWHUKGltGxpxRFaQACSwIAAiHE3BFsPf7OlvgO2C0E");
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
                        inputFile = new InputFile("CAACAgIAAxkBAAEIguNkMcKp2JBkCkcHea3tdMSGoSr3ngACOAsAAk7kmUsysUfS2U-M0C8E");
                        break;
                }
                break;
            case "Материальный мир":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEHXrdjyxEsYnOeZhzv4x3IgCzBA-OjNwACNAADvbJxDgrMJeM54-p8LQQ");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgulkMcO4u5OSEa_a6t-SA0sy2TXAUQACpgAD2qQjMBltVXkF4ZaiLwQ");
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
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgu9kMcUgI3STxlFY0NkGo3eQ5-LvTwAC1xIAAvbvCUgZf_V7xQvNwC8E");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIhiNkMx1ZaDxROcl_Ek0ijQn96OiqxAACBhgAAgIluUrhGRsjwVAxLi8E");
                        break;
                }                break;
            case "Здоровье":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgvNkMcWJRYyCCS09SeJzSIfnBcH2cwACzBQAAgrXyUsa7GgoHs1N_y8E");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJBHpkZTyyKy9pJdB9SaXYrJlv-UQ0fQACbysAArPGsEioRp6ogvcJSy8E");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgvtkMcYy3f1V86kemUPW5nkwT31HQwACigADfI5YFYYrEzj9KKvHLwQ");
                        break;
                }
                break;
            case "Семья":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgv1kMcZnhh1ezEcyMSRyQ14UhVT0YwACM8oAAmOLRgxBEQpv2eeE0i8E");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgv9kMcap4qHxRB8VSkLefJ5xlrzuNwACMBQAAuvuKEjQJW_nQuqF-S8E");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIhhVkMxqGSS3gC0kKUsQUybbDgHwt3AAC96gBAAFji0YMy7uns-C2NFMvBA");
                        break;
                }
                break;
            case "Развлечения":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgwVkMchBU4qIW-i85w61i9hjUjM72wACNAAD8J60JPC9Kn5AbuITLwQ");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgwdkMcjZRC5avqqdY6ZlClFip2zmEAACoxIAAvDTKEgWBWUL8yPbiy8E");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgwlkMckFDkaL9Jj7tp4N65zfrFXj4AACKcoAAmOLRgwBEvlzxJ8e2i8E");
                        break;
                }
                break;
            case "Друзья":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgwtkMclJc__vBbxa1TMTfmsEdIQtyQACERQAAg59yEugoW5wtcva3i8E");
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
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgxdkMcsquz1942L3e6scQ4zWdf_FsQACpgADaIrFI5jyRf_xAcJuLwQ");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgylkMc6IoCJjX11vQIzvm4Ol6R-kfwACIQADy0m0CVxHNOBMQ6aMLwQ");
                        break;
                }
                break;
            case "Внешность":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgx1kMcy06xcsoX4fzDu4N8z3VmA--wACDQADvbJxDj1_1nNQ2kXfLwQ");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIhjVkMx6D9U8TC65imkqm6BR6xW2amgACMgUAAiMFDQABe6yTTsfJ4MYvBA");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgyVkMc13uGHtoG66WrJrNq2n-E6mAgACQhIAAv2U0EtckDqh5XLhFy8E");
                        break;
                }
                break;
        }
        sticker.setSticker(inputFile);

        return sticker;
    }
}
