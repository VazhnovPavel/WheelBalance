package com.testSpringBoot.SpringDemoBot.Keyboard;

import com.testSpringBoot.SpringDemoBot.statistic.PeriodHasData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
@Component
public class Keyboard {
    @Autowired
    private PeriodHasData monthCategory;
    private ArrayList<String> categoryList = new ArrayList<String>();

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
            registrationYears = monthCategory.getRegistrationYears(chatID);
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


}
