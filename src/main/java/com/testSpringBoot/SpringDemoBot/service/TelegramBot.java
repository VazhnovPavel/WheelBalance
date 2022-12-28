package com.testSpringBoot.SpringDemoBot.service;

import com.testSpringBoot.SpringDemoBot.config.BotConfig;
import com.testSpringBoot.SpringDemoBot.model.AskUser;
import com.testSpringBoot.SpringDemoBot.model.AskUserRepository;
import com.testSpringBoot.SpringDemoBot.model.User;
import com.testSpringBoot.SpringDemoBot.model.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.persistence.EntityNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


    @Slf4j
    @Component
    public class TelegramBot extends TelegramLongPollingBot {
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private AskUserRepository askUserRepository;
        final BotConfig config;
        static final String START_MESSAGE = EmojiParser.parseToUnicode(" Привет! \uD83E\uDEF6 Я помогу тебе отслеживать твое состояние во всех основных аспектах " +
                "жизни (или в каких пожелаешь).\n\n Я буду ежедневно задавать тебе простые вопросы об аспектах твоей жизни, " +
                "а тебе нужно будет ответить по десятибалльной шкале, насколько ты удовлетворен на данный момент.\n\n " +
                "А в конце недели/месяца/года мы с тобой будем подводить итоги, как идут у нас успехи. \n\n");
        static final String YES_BUTTON = "YES_BUTTON";
        static final String NO_BUTTON = "NO_BUTTON";
        static final String YES_BUTTON_verificationTimeQuestion = "YES_BUTTON_verificationTimeQuestion";
        static final String NO_BUTTON_verificationTimeQuestion = "NO_BUTTON_verificationTimeQuestion";
        static final String ERROR_OCCURED = "Error occurred: ";
        private String textTimetoQuestions;

        static final String HELP_TEXT = "/start - запустить бота \n\n" +
                "/addSection - добавить свой раздел в “Колесо” \n\n" +
                "/deleteSection - удалить раздел из “Колеса” \n\n" +
                "/renameSection - переименовать раздел из “Колеса” \n\n" +
                "/help - вывести все команды \n\n" +
                "/deleteAll - удалить все данные о пользователе \n\n" +
                "/download - скачать все данные в формате таблицы \n\n" +
                "/when - настроить время для вопросов \n\n" +
                "/addSkip - ввести запись в дневник за прошедшую дату \n\n" +
                "/freeSession - получить бесплатную консультацию от психолога/коуча \n\n" +
                "/week - показать статистику за неделю \n\n" +
                "/month - показать статистику за месяц ";


        public TelegramBot(BotConfig config) {
            this.config = config;
            List<BotCommand> listofCommands = new ArrayList<>();   //создаем лист меню
            listofCommands.add(new BotCommand("/start", "поприветствовать братуху"));  //добавляем команды
            listofCommands.add(new BotCommand("/addSection", "добавить свой раздел в “Колесо” "));
            listofCommands.add(new BotCommand("/deleteSection", " удалить раздел из “Колеса”"));
            listofCommands.add(new BotCommand("/renameSection", "переименовать раздел из “Колеса”"));
            listofCommands.add(new BotCommand("/help", "вывести все команды "));
            listofCommands.add(new BotCommand("/deleteAll", "удалить все данные о пользователе"));
            listofCommands.add(new BotCommand("/download", "скачать все данные в формате таблицы"));
            listofCommands.add(new BotCommand("/when", "настроить время для вопросов"));
            listofCommands.add(new BotCommand("/addSkip", "ввести запись в дневник за прошедшую дату"));
            listofCommands.add(new BotCommand("/freeSession", "получить бесплатную консультацию от психолога/коуча"));
            listofCommands.add(new BotCommand("/week", "показать статистику за неделю"));
            listofCommands.add(new BotCommand("/month", "показать статистику за месяц"));
            listofCommands.add(new BotCommand("/month", "Просто попробуем добавить строку"));


            try {
                this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
            } catch (TelegramApiException e) {
                log.error("Error setting bots command list" + e.getMessage());
            }
        }

        @Override
        public String getBotUsername() {
            return config.getBotName();
        }

        @Override
        public String getBotToken() {
            return config.getToken();
        }

        @Override
        public void onUpdateReceived(Update update) {

            if (update.hasMessage() && update.getMessage().hasText()) {              // если нам прислали текст, то...
                String messageText = update.getMessage().getText();
                long chatID = update.getMessage().getChatId();

                if (messageText.contains("/send") && (config.getOwnerId() == chatID)) {
                    var textToSend
                            = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                    //после /send пишем сообщение, которое хотим отправить
                    var users = userRepository.findAll();
                    for (User user : users) {
                        prepareAndSendMessage(user.getChatId(), textToSend);
                    }
                } else if (messageText.matches("^\\d{2}:\\d{2}$")) {     //Если юзер передает дату в формате ЧЧ:ММ
                    log.info("Пользователь ввел время для вопросов");
                    setTextTimetoQuestions(messageText);
                    verificationTimeQuestion(chatID, messageText);
                } else {                                                      //Switch срабатывает, если не было send
                    switch (messageText /*.toLowerCase()*/) {
                        case "/start":
                            // передаем Имя пользователя
                            registerUser(update.getMessage(), update);
                            startCommandReceived(chatID, update.getMessage().getChat().getFirstName());


                            break;
                        case "/help":
                            prepareAndSendMessage(chatID, HELP_TEXT);
                            break;
                        case "да":
                            prepareAndSendMessage(chatID, "В какое время вам было бы удобно получать вопросы? Напишите в " +
                                    "формате ЧЧ:ММ по Москве");
                        default:
                            prepareAndSendMessage(chatID, "Я не знаю, как работать с этой командой");
                    }

                }

            } else if (update.hasCallbackQuery()) {           //провереям, вдруг помимо текста нам передали значение
                String callBackData = update.getCallbackQuery().getData();
                long messageId = update.getCallbackQuery().getMessage().getMessageId();
                long chatId = update.getCallbackQuery().getMessage().getChatId();


                if (callBackData.equals(YES_BUTTON)) {
                    String text = "Ты нажал(а) ДА";
                    executeEditMessageText(text, chatId, messageId);
                    timeToQuestions(chatId);
                } else if (callBackData.equals(NO_BUTTON)) {
                    String text = "Ты нажал(а) НЕТ";
                    executeEditMessageText(text, chatId, messageId);
                } else if (callBackData.equals(NO_BUTTON_verificationTimeQuestion)) {
                    log.info("Пользователь сказал, что задал неправильное время");
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("Давай попробуем еще раз");
                    executedMessage(message);
                    timeToQuestions(chatId);

                } else if (callBackData.equals(YES_BUTTON_verificationTimeQuestion)) {
                    log.info("Пользователь задал верное время");
                    //тут нужен метод добавление времени в БД
                    // public static  String textTimeToQuest;
                    addTimeToDB(chatId, getTextTimetoQuestions());
                }
            }
        }

        private void timeToQuestions(long chatID) {   //реализуем клавиатуру на вопросе
            SendMessage message = new SendMessage();
            message.setChatId(chatID);
            message.setText("В какое время вам было бы удобно получать вопросы? Напишите в формате ЧЧ:ММ по Москве");
            executedMessage(message);
        }

        private void registerUser(Message msg, Update update) {
            if (userRepository.findById(msg.getChatId()).isEmpty()) {         //Если новый user id, то...
                var chatId = msg.getChatId();
                var chat = msg.getChat();

                User user = new User();    // создаем юзера

                user.setChatId(chatId);
                user.setFirstName(update.getMessage().getChat().getFirstName());       // берем данные о юзере
                user.setLastName(update.getMessage().getChat().getLastName());
                user.setUserName("@" + update.getMessage().getChat().getUserName());
                user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));  // время регистрации

                userRepository.save(user);

                log.info("User saved" + user);
            }
        }

        private void startCommandReceived(long chatID, String name) {            //метод обработки сообщения

            sendMessage(chatID, name + START_MESSAGE);
            log.info("Replied to user" + name);
            String yes = "ДА(смарт)";
            String no = "Нет(смарт)";
            smartKeyboard(chatID, yes, no);


        }

        //метод отправки сообщения пользоваелю
        public void sendMessage(long chatID, String texToSend) {
            SendMessage message = new SendMessage();
            message.setChatId(chatID);
            message.setText(texToSend);

            ////////////////ПОСТОЯННАЯ КЛАВИАТУРА/////
       /* ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();   //  создем клавиатуру
        List<KeyboardRow> keyboardRows = new ArrayList<>(); // создаем лист для вариантов ответа

        KeyboardRow row = new KeyboardRow(); // создаем ряд кнопок
        row.add("Да");
        row.add("Пока нет");

        keyboardRows.add(row);

        row = new KeyboardRow();             // еще один ряд кнопок
        row.add("Добавить свой раздел");
       row.add("Удалить раздел");
       row.add("Изменить время вопросов");
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows); //добавляем в клавиатуру наши ряды
        message.setReplyMarkup(keyboardMarkup); //привязываем к сообщению клавиатуру*/
////////////////ПОСТОЯННАЯ КЛАВИАТУРА КОНЕЦ/////
            executedMessage(message);

        }

        @Scheduled(cron = "${interval-in-cron} ")                     //чтобы запускался автоматически
        private void SendAskUser() {
            var askUser = askUserRepository.findAll(); // все записи, которые есть в таблице
            var users = userRepository.findAll();
            for (AskUser ask : askUser) {
                for (User user : users) {
                    prepareAndSendMessage(user.getChatId(), ask.getTextAskUser());
                }
            }
        }


        private void executeEditMessageText(String text, long chatId, long messageId) {
            EditMessageText message = new EditMessageText();   // меняем введеннный текст
            message.setChatId(chatId);
            message.setText(text);
            message.setMessageId((int) messageId);   //должно быть не просто отправлено, а заменено в сообщении

            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(ERROR_OCCURED + e.getMessage());
            }

        }

        private void executedMessage(SendMessage message) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(ERROR_OCCURED + e.getMessage());
            }

        }

        private void prepareAndSendMessage(long chatID, String texToSend) {
            SendMessage message = new SendMessage();
            message.setChatId(chatID);
            message.setText(texToSend);
            executedMessage(message);
        }

        private void smartKeyboard(long chatID, String yes, String no) {

            SendMessage message = new SendMessage();
            message.setChatId(chatID);
            message.setText("Попробуем?");

            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            var buttonYES = new InlineKeyboardButton();
            buttonYES.setText(yes);
            buttonYES.setCallbackData(YES_BUTTON); // позволяет боту понять, какая кнопка была нажата
            var buttonNO = new InlineKeyboardButton();
            buttonNO.setText(no);
            buttonNO.setCallbackData(NO_BUTTON);
            rowInline.add(buttonYES);
            rowInline.add(buttonNO);
            rowsInLine.add(rowInline);
            markupInline.setKeyboard(rowsInLine);
            message.setReplyMarkup(markupInline);


            executedMessage(message);
        }

        private void verificationTimeQuestion(long chatID, String messageText) {
            SendMessage message = new SendMessage();
            message.setChatId(chatID);
            String[] parts = messageText.split(":");
            message.setText("Вы хотите получать вопросы в " + parts[0] + " часов " + parts[1] + " минут, верно?");
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            var buttonYES = new InlineKeyboardButton();
            buttonYES.setText("Да верно");
            buttonYES.setCallbackData(YES_BUTTON_verificationTimeQuestion); // позволяет боту понять, какая кнопка была нажата
            var buttonNO = new InlineKeyboardButton();
            buttonNO.setText("Нет, не верно");
            buttonNO.setCallbackData(NO_BUTTON_verificationTimeQuestion);
            rowInline.add(buttonYES);
            rowInline.add(buttonNO);
            rowsInLine.add(rowInline);
            markupInline.setKeyboard(rowsInLine);
            message.setReplyMarkup(markupInline);


            executedMessage(message);
        }

        private void addTimeToDB(long chatId, String timeToQuestions) {

            String[] parts = timeToQuestions.split(":");
            timeToQuestions = "* " + parts[1] + " " + parts[0] + " * " + "*" + " *" + " *";
            log.error(timeToQuestions);     //конвертируем дату в формат cron
            User user = userRepository.findById(chatId).orElseThrow(() -> new EntityNotFoundException());
            user.setTimeToQuestions(timeToQuestions);
            userRepository.save(user);
            log.info("Добавили время в базу данных" + user);






        }







        public String getTextTimetoQuestions() {
            return textTimetoQuestions;
        }

        public void setTextTimetoQuestions(String textTimetoQuestions) {
            this.textTimetoQuestions = textTimetoQuestions;
        }
}
