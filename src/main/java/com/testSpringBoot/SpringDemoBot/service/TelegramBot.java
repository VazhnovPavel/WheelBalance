package com.testSpringBoot.SpringDemoBot.service;

import com.testSpringBoot.SpringDemoBot.config.BotConfig;
import com.testSpringBoot.SpringDemoBot.model.*;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;




@Slf4j
    @Component
public class TelegramBot extends TelegramLongPollingBot {
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private DataBaseQuestRepository dataBaseQuestRepository;
        @Autowired
        private SendAllUserRepository sendAllUserRepository;
        @Autowired
        private JdbcTemplate jdbcTemplate;

         BotConfig config;
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

        static final String HELP_TEXT =
                "/start - запустить бота \n\n" +
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
            //  если нам прислали текст, то...
            if (update.hasMessage() && update.getMessage().hasText()) {
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
                    //Если юзер передает дату в формате ЧЧ:ММ
                } else if (messageText.matches("^\\d{2}:\\d{2}$")) {
                    log.info("Пользователь ввел время для вопросов");
                    setTextTimetoQuestions(messageText);
                    verificationTimeQuestion(chatID, messageText);
                } else {
                    //Switch срабатывает, если не было send
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
                            prepareAndSendMessage(chatID, "В какое время вам было бы удобно " +
                                    "получать вопросы? Напишите в формате ЧЧ:ММ по Москве");
                        default:
                            prepareAndSendMessage(chatID, "Я не знаю, как работать с этой командой");
                    }

                }

            }
            //провереям, вдруг помимо текста нам передали значение
            else if (update.hasCallbackQuery()) {
                String callBackData = update.getCallbackQuery().getData();
                long messageId = update.getCallbackQuery().getMessage().getMessageId();
                long chatId = update.getCallbackQuery().getMessage().getChatId();

                if (callBackData.equals(YES_BUTTON)) {
                    executeEditMessageText("Ты нажал(а) ДА", chatId, messageId);
                    timeToQuestions(chatId);

                } else if (callBackData.equals(NO_BUTTON)) {
                    executeEditMessageText("Ты нажал(а) НЕТ", chatId, messageId);

                } else if (callBackData.equals(NO_BUTTON_verificationTimeQuestion)) {
                    log.info("Пользователь ввел некоректные данные");
                    sendMessage(chatId, "Попробуйте еще раз");
                    timeToQuestions(chatId);

                } else if (callBackData.equals(YES_BUTTON_verificationTimeQuestion)) {
                    log.info("Пользователь ввел корректные данные");
                    addTimeToDB(chatId, getTextTimetoQuestions());
                    sendMessage(chatId, "Наш бот сохранил время, но пока он не присылает вопросы." +
                            " Мы работаем над этим");
                    addDataBaseQuest(chatId);
                }
            }
        }
        //реализуем клавиатуру на вопросе
        private void timeToQuestions(long chatID) {
            SendMessage message = new SendMessage();
            message.setChatId(chatID);
            message.setText("В какое время вам было бы удобно получать вопросы? Напишите в формате ЧЧ:ММ по Москве");
            executedMessage(message);
        }

        private void registerUser(Message msg, Update update) {
            // Check if user already exists
            if (userRepository.findById(msg.getChatId()).isEmpty()) {
                // Create new user
                User user = new User();
                user.setChatId(msg.getChatId());
                user.setFirstName(update.getMessage().getChat().getFirstName());
                user.setLastName(update.getMessage().getChat().getLastName());
                user.setUserName("@" + update.getMessage().getChat().getUserName());
                user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

                // Save user to repository
                userRepository.save(user);
                log.info("Saved user: " + user);
            }
        }




        private void startCommandReceived(long chatID, String name) {
            // Send message and create reply keyboard
            sendMessage(chatID, name + START_MESSAGE);
            log.info("Replied to user" + name);
            smartKeyboard(chatID, "ДА", "Нет");
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
        //чтобы запускался автоматически
        @Scheduled(cron = "${interval-in-cron} ")
        private void SendAskUser() {
            // все записи, которые есть в таблице
            var askUser = sendAllUserRepository.findAll();
            var users = userRepository.findAll();
            for (SendAllUser ask : askUser) {
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

        public void executedMessage(SendMessage message) {
            try {

                execute(message);
                System.out.println("Сообщение пользователю отправлено");
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
            // Create message and reply markup
            SendMessage message = new SendMessage();
            message.setChatId(chatID);
            message.setText("Попробуем?");
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(createInlineKeyboardButton(yes, YES_BUTTON));
            rowInline.add(createInlineKeyboardButton(no, NO_BUTTON));
            rowsInLine.add(rowInline);
            markupInline.setKeyboard(rowsInLine);
            message.setReplyMarkup(markupInline);

            // Send message
            executedMessage(message);
        }






        private void verificationTimeQuestion(long chatID, String messageText) {
            // Extract hour and minute from messageText
            String[] parts = messageText.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            // Validate hour and minute
            if (hour < 0 || hour >= 24 || minute < 0 || minute >= 60) {
                prepareAndSendMessage(chatID, "Неправильный формат даты, попробуйте еще раз");
                return;
            }

            // Create message and reply markup
            SendMessage message = new SendMessage();
            message.setChatId(chatID);
            message.setText("Вы хотите получать вопросы в " + hour + " часов " + minute + " минут?");
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(createInlineKeyboardButton("Да, верно", YES_BUTTON_verificationTimeQuestion));
            rowInline.add(createInlineKeyboardButton("Нет, исправить", NO_BUTTON_verificationTimeQuestion));
            rowsInLine.add(rowInline);
            markupInline.setKeyboard(rowsInLine);
            message.setReplyMarkup(markupInline);

            // Send message
            executedMessage(message);
        }

        private InlineKeyboardButton createInlineKeyboardButton(String text, String callbackData) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(text);
            button.setCallbackData(callbackData);
            return button;
        }


        private void addTimeToDB(long chatId, String timeToQuestions) {

            String[] parts = timeToQuestions.split(":");
            timeToQuestions = "* " + parts[1] + " " + parts[0] + " * " + "*" + " *" ;
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
        private void addDataBaseQuest (Long chatId){
            DataBaseQuest userDB = new DataBaseQuest();
            DataBaseQuestId id = new DataBaseQuestId();

            List<String> questions = Arrays.asList("Как здоровье?", "Как работа?", "Саморазвитие?"
                    , "Как деньги?", "Как вещи?", "Как отношения?", "Развлечения?"
                    , "Семья?", "Как крастота?" , "Как друзья?");
            try {

                id.setChatId(chatId);
                userDB.setId(id);
                for (String question : questions) {
                    id.setQuest(question);
                    dataBaseQuestRepository.save(userDB);
                }
                log.info("Saved user to DB: " + userDB);

            } catch (Exception e) {
                log.error("Error saving user to DB: " + userDB, e);

            }
        }
        //СКАНИРУЕМ КАЖДУЮ МИНУТУ СПИСОК НА НАЛИЧИЕ СОВПАДЕНИЙ CRON
        @Scheduled(cron = "0 * * * * *")
        public void schedulerService(){
            List<User> userList = userRepository.findAll();
                for (User user: userList) {
                    String cronExpression = user.getTimeToQuestions();
                    Long chat_id = user.getChatId();
                    try {
                        CronSequenceGenerator generator = new CronSequenceGenerator(cronExpression);
                        Date nextExecutionTime = generator.next(new Date());
                        Date currentDate = new Date();

                        if(nextExecutionTime != null && nextExecutionTime.getMinutes() == currentDate.getMinutes()){
                            log.info("Время cron соответствует текущему времени");
                            checkDateAndChatId(chat_id);
                        }
                    } catch (IllegalArgumentException e) {
                        log.info("Какая то ошибка" + e);
                    }
                }
        }


    //КАКИЕ СТОЛБЦЫ quest СВОБОДНЫ У ДАННОГО chat_id ЗА ПОСЛЕДНИЕ 3 ДНЯ
    public void checkDateAndChatId(Long chat_id) {
        log.info("Выполнение запроса для получения квеста");
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate dayBeforeYesterday = today.minusDays(2);
        String formattedTodayDate = today.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String formattedYesterdayDate = yesterday.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String formattedDayBeforeYesterdayDate = dayBeforeYesterday.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String sql = "SELECT quest FROM data_base_quest WHERE chat_id = ? AND (date_" + formattedTodayDate
                + " IS NULL AND date_" + formattedYesterdayDate
                + " IS NULL AND date_" + formattedDayBeforeYesterdayDate
                + " IS NULL) ORDER BY random() LIMIT 3";
        List<String> quests = null;
        try {
            quests = jdbcTemplate.query(sql, new Object[]{chat_id}, (rs, rowNum) -> rs.getString("quest"));
        } catch (Exception e) {
            log.error("Error while executing query", e);
        }
        log.info("quest = " + quests);
        sendQuest(chat_id, quests);
    }
        //СПИСОК ИЗ 3 РАНДОМНЫХ ВОПРОСОВ, ПОДПАДАЮЩИХ ПОД УСЛОВИЕ
        private void sendQuest(Long chatId, List<String> quests) {
            System.out.println("User " + chatId + " Received questions " + quests);
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            for(String quest : quests) {
                message.setText(quest);
                executedMessage(message);
            }
        }









}
