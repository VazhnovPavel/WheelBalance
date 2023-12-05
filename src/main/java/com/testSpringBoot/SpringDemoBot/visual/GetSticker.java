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
                        inputFile = new InputFile("CAACAgIAAxkBAAEJyUNkvYllFGMWkltMkgmqJWCcGwHQNgACwxcAAvLosEkjOlvTGX-T-C8E");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJyUlkvYohfe262prewZFtOU6NBAEmZgACbwEAAhZ8aAOK9nH8d3JcRi8E");
                        break;
                }
                 break;
            case "Работа":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJyUtkvYpwywlynFvy2lsBex1w-XDCpwACzzkAAu_-iUl34iHPVXoxfi8E");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEK5bRlbg1-YqAGN2OHJnPWLQqMGgr8zAAC_jwAAr9xaEvRMmWcC1b8ajME");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEK5allbgzJksTubjWcofYbjW7h-EXdJAAC0BUAAgh7KEjZ-OZ0AAFT4q0zBA");
                        break;
                }
                break;
            case "Материальный мир":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJgFxkmx_IbS-wfooRg0O6pJjmr2xPrwACWgADfI5YFVERlXx2Itx6LwQ");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJyU9kvYuNxNL1xqAMvdL7gmPoM1WdFQACay4AAmTkoEhx4IDi1WOIoS8E");
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
                        inputFile = new InputFile("CAACAgIAAxkBAAEKBYZk2lU2DPNrYwUkeKkwsw9dtXnxFQAC8BMAAlbFKEi4DBhmqzzfkDAE");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJyfNkvYzCgzIfU9YOQchrfGVGUUBGMgACIxcAAvY9yEvoJYVTb5k-ay8E");
                        break;
                }                break;
            case "Здоровье":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJyfVkvY0k0SuLCb542Zq3DcLUFuDQYQACoywAAlB5qUjdbeX6NMeXrC8E");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEK5bxlbg9ARkXKEs3kRjii0oUGLjYyCAACXSoAAvgYuEqJS4Cu0uvglDME");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJyflkvY2wD_E7IFKpZXQnRMy1hojkngACLAEAAhZ8aAPtUedaQ6gYTS8E");
                        break;
                }
                break;
            case "Семья":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJyftkvY3gBBCOLn7eyxMCHCg4nUhMjAACOygAAgbxiUjA10xUvHLVgC8E");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJyf1kvY6ZuLwQCdQPEC6s39E7P4BcagACZB4AAkm0SUtMLQ2LStVLNi8E");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJyf9kvY8sdz1xJBMs_XCk44xna9o1OgACMhgAAp5OcUjnKqkcXV3wgC8E");
                        break;
                }
                break;
            case "Развлечения":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJgGpkmyM7LvF3DVeh68A_gnAcHMlZ7QAC5h0AApQCMEj0W8nLG5FF3i8E");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEK5bhlbg3xj87byd0YnX3f6Zw2We--TgACaSgAAj0hQUs8hOtUPGT_hDME");
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
                        inputFile = new InputFile("CAACAgIAAxkBAAEJygNkvZAfwXGaEYwbo5FJrAhJ6afb3wAC1S4AApLBqUh_IpVT68NnlC8E");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJygVkvZA-uDhwsCEp514xwd3R7wPXcgACwhQAArP70Eg4qTcl7eNzqi8E");
                        break;
                }
                break;
            case "Саморазвитие":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEIgxFkMcqCB7DgxC7_fLzZsZipxJpjrwACcQUAAiMFDQABqr8z5dgL2qovBA");
                        break;
                    case 2:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJygdkvZCAM7BxHEgMDMRXnnAJtjJDGwACdgEAAhZ8aAN92wnVCq6FvS8E");
                        break;
                    case 3:
                        inputFile = new InputFile("CAACAgIAAxkBAAEJyo5kvZIJQChKdLkndq0-hkDDZdOiVwACQiEAAj-aQUtC_0mvvpK8Mi8E");
                        break;
                }
                break;
            case "Внешность":
                switch (stickerNum) {
                    case 1:
                        inputFile = new InputFile("CAACAgIAAxkBAAEKBYRk2lR4I0kKhzY2Rg7fzrQyTPDZMwACMxQAAtWzKEhzcG8yt63ebjAE");
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
