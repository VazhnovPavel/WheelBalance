package com.testSpringBoot.SpringDemoBot.keyboard;


import com.testSpringBoot.SpringDemoBot.statistic.PeriodHasData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class Keyboard {
    @Autowired
    private PeriodHasData periodHasData;


    private InlineKeyboardButton createInlineKeyboardButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    /**
     * Создаем клавиатуру для ответов
     * Все кнопки создаются по формату "BUTTON_" + "Номер вопроса" + "Сам вопрос"
     * Эта информация необходима потом для занесения данных в БД
     */


    public SendMessage getKeyboard(Long chatID, String quest, String condition) {
        ArrayList<String> categoryList = new ArrayList<String>();
        String startMessage = null;
        String category = null;
        int countButtons = 0;

        if (Objects.equals(condition, "Клавиатура оценок")){
            countButtons = 11;
            startMessage = "Оцени от 0 до 10";
            category = "BUTTON_";
        }
        else if (Objects.equals(condition, "Клавиатура категорий")){
            countButtons = 10;
            startMessage = "Выбери категорию стикера: ";
            category = "CATEGORY_";
            categoryList.add("Здоровье");
            categoryList.add("Работа");
            categoryList.add("Саморазвитие");
            categoryList.add("Деньги, капитал");
            categoryList.add("Друзья");
            categoryList.add("Отношения");
            categoryList.add("Развлечения");
            categoryList.add("Семья");
            categoryList.add("Внешность");
            categoryList.add("Материальный мир");
        }

        else if (Objects.equals(condition, "Клавиатура с выбором года")){

            startMessage = "Выбери нужный год: ";
            List<Integer> registrationYears = new ArrayList<>();
            registrationYears = periodHasData.getRegistrationYears(chatID);
            countButtons = registrationYears.size();

            for (int i = 0; i <registrationYears.size() ; i++) {

                //преобразовываем год в строку
                String str = String.valueOf(registrationYears.get(i));
                categoryList.add(str);
            }

            category = "YEAR_";
        }
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(startMessage);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        for (int i = 0; i < countButtons; i++) {
            String answerNumber = null;

            if (Objects.equals(condition, "Клавиатура оценок")){
                answerNumber = String.valueOf(i);
            }
            else if (Objects.equals(condition, "Клавиатура категорий")){
                answerNumber = categoryList.get(i);
            }
            else if (Objects.equals(condition, "Клавиатура с выбором года")){
                answerNumber = categoryList.get(i);
            }

            try {
                rowInline.add(createInlineKeyboardButton(answerNumber, category + answerNumber + "_" + quest));
                log.info(answerNumber + " пробел " + category + answerNumber + "_" + quest);
            } catch (Exception e) {
                log.info("ОШИИБКА СОЗДАНИЯ КЛАВИАТУРЫ " + e);
            }

            if (rowInline.size() == 3) {
                rowsInLine.add(rowInline);
                rowInline = new ArrayList<>();
            }
        }

        rowsInLine.add(rowInline);
        markupInline.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInline);
        return message;

    }
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    public SendMessage getKeyboard(Long chatID, LocalDate currentDate, String quest ) {
        int countButtons = 11;
        String startMessage = "Оцени от 0 до 10";
        String category = "BUTTON_";

        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(startMessage);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        for (int i = 0; i < countButtons; i++) {
            String answerNumber = String.valueOf(i);

            try {
                // Маска кнопки(пример): BUTTON_3_Здоровье_2023-10-5
                rowInline.add(createInlineKeyboardButton(answerNumber, category + answerNumber
                        + "_" + quest + "_" + currentDate));
                log.info("Создана маска кнопки: " + answerNumber + " пробел " + category + answerNumber
                        + "_" + quest + "_" + currentDate);
            } catch (Exception e) {
                log.info("ОШИИБКА СОЗДАНИЯ КЛАВИАТУРЫ " + e);
            }

            if (rowInline.size() == 3) {
                rowsInLine.add(rowInline);
                rowInline = new ArrayList<>();
            }
        }

        rowsInLine.add(rowInline);
        markupInline.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInline);
        return message;
    }

    public SendMessage getKeyboard(Long chatID, List<String> CATEGORY_LIST ) {
        int countButtons = CATEGORY_LIST.size();
        String startMessage = "Выбери категорию стикера: ";
        String category = "CATEGORY_";

        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(startMessage);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        for (int i = 0; i < countButtons; i++) {

            String currentCategory = CATEGORY_LIST.get(i);
            try {
                // Маска кнопки(пример): CATEGORY_Здоровье
                rowInline.add(createInlineKeyboardButton(currentCategory, category + currentCategory ));
                log.info("Создана маска кнопки: " + currentCategory + " пробел " + category);
            } catch (Exception e) {
                log.info("ОШИИБКА СОЗДАНИЯ КЛАВИАТУРЫ " + e);
            }

            if (rowInline.size() == 3) {
                rowsInLine.add(rowInline);
                rowInline = new ArrayList<>();
            }

        }

        rowsInLine.add(rowInline);
        markupInline.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInline);
        return message;
    }

    public SendMessage getKeyboard(Long chatID ) {

        String startMessage = "Выбери нужный год: ";
        List<Integer> registrationYears = new ArrayList<>();
        registrationYears = periodHasData.getRegistrationYears(chatID);
        int countButtons = registrationYears.size();
        String category = "YEAR_";

        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(startMessage);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        for (int i = 0; i <registrationYears.size() ; i++) {

            //преобразовываем год в строку
            String str = String.valueOf(registrationYears.get(i));

            try {
                // Маска кнопки(пример): YEAR_2023
                rowInline.add(createInlineKeyboardButton(str, category + str));
                log.info("Создана маска кнопки: " + str + " пробел " + category + str);
            } catch (Exception e) {
                log.info("ОШИИБКА СОЗДАНИЯ КЛАВИАТУРЫ " + e);
            }

            if (rowInline.size() == 3) {
                rowsInLine.add(rowInline);
                rowInline = new ArrayList<>();
            }
        }
        rowsInLine.add(rowInline);
        markupInline.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInline);
        return message;
    }

    public SendMessage getKeyboard(Long chatID,int year ) {

        String startMessage = "Выбери нужный месяц: ";
        List<String> monthHasData = new ArrayList<>();
        monthHasData = periodHasData.getRegistrationMonths(chatID,year);
        int countButtons = monthHasData.size();
        String category = "MONTH_";

        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(startMessage);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        for (int i = 0; i <monthHasData.size() ; i++) {

            //преобразовываем месяц в строку
            String str = String.valueOf(monthHasData.get(i));

            try {
                // Маска кнопки(пример): MONTH_Октябрь_YEAR_2023
                rowInline.add(createInlineKeyboardButton(str, category + str + "_YEAR_" + year));
                log.info("Создана маска кнопки: " + str + " пробел " + category + str + "_YEAR_" + year);
            } catch (Exception e) {
                log.info("ОШИИБКА СОЗДАНИЯ КЛАВИАТУРЫ " + e);
            }
            if (rowInline.size() == 3) {
                rowsInLine.add(rowInline);
                rowInline = new ArrayList<>();
            }
        }
        rowsInLine.add(rowInline);
        markupInline.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInline);
        return message;
    }


}
