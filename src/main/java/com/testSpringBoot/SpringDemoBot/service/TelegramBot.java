package com.testSpringBoot.SpringDemoBot.service;

import com.testSpringBoot.SpringDemoBot.config.BotConfig;
import com.testSpringBoot.SpringDemoBot.model.*;
import com.testSpringBoot.SpringDemoBot.statistic.CompareWeekLastWeek;
import com.testSpringBoot.SpringDemoBot.statistic.GetStat7Days;
import com.testSpringBoot.SpringDemoBot.statistic.LastWeekValues;
import com.testSpringBoot.SpringDemoBot.statistic.WeekValues;
import com.testSpringBoot.SpringDemoBot.visual.CreateEmoji;
import com.testSpringBoot.SpringDemoBot.visual.GetResultEmoji;
import com.testSpringBoot.SpringDemoBot.visual.GetSticker;
import com.vdurmont.emoji.EmojiParser;
import io.quickchart.QuickChart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import javax.persistence.EntityNotFoundException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;


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
    @Autowired
    private GetSticker getSticker;
    @Autowired
    private DeleteUserInformation deleteUserInformation;
    @Autowired
    private LastWeekValues lastWeekValues;
    @Autowired
    private WeekValues weekValues;
    @Autowired
    BotConfig config;
    @Autowired
    private GetResultEmoji getResultEmoji;
    @Autowired
    private CompareWeekLastWeek compareWeekLastWeek;
    @Autowired
    private GetStat7Days getStat7Days;
    @Autowired
    private CreateEmoji createEmoji;
    @Autowired
    private CreateQueryToCheck3Days createQueryToCheck3Days;
    static final String START_MESSAGE = " Привет! \uD83E\uDEF6 Я помогу тебе отслеживать твое состояние во всех основных сферах " +
            "жизни.\n\n Я буду ежедневно задавать тебе простые вопросы о сферах твоей жизни, " +
            "а тебе нужно будет ответить по десятибалльной шкале \u0031\u20E3 - \uD83D\uDD1F, насколько ты удовлетворен на данный момент.\n\n " +
            "А в конце недели/месяца/года мы с тобой будем подводить итоги, как идут у нас успехи. \n\n";
    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String YES_BUTTON_verificationTimeQuestion = "YES_BUTTON_verificationTimeQuestion";
    static final String NO_BUTTON_verificationTimeQuestion = "NO_BUTTON_verificationTimeQuestion";
    static final String ERROR_OCCURED = "Error occurred: ";
    private String textTimetoQuestions;
    final private String sendQuestAboutTimeToQuestion = "\n\nВ какое время тебе было бы удобно получать вопросы?\n" +
            "Напиши в формате ЧЧ:ММ , например 20:30\n" +
            "(по Московскому времени)";
    final private String thxForAsking = "Спасибо за ответы! Завтра спишемся в то же время \uD83D\uDE09\n \n\n" +
            "Узнать статистику за последние 7 дней /week";


    static final String HELP_TEXT =
            "/start - запустить бота \n\n" +
                    "/help - вывести все команды \n\n" +
                    "/deleteAll - удалить все ваши персональные данные из бота \n\n" +
                    "/when - настроить время для вопросов \n\n" +
                    "/week - показать статистику за неделю \n\n" +
                    "/compareWeek - сравнить статистику с предыдущей неделей \n\n" +
                    "/month - показать статистику за месяц (beta) ";


    public TelegramBot(BotConfig config) {
        this.config = config;

        /**
         * Create list menu, add command
         */

        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Start"));
        listofCommands.add(new BotCommand("/help", "вывести все команды "));
        listofCommands.add(new BotCommand("/deleteAll", "удалить все данные о пользователе"));
        listofCommands.add(new BotCommand("/when", "настроить время для вопросов"));
        listofCommands.add(new BotCommand("/week", "показать статистику за неделю"));
        listofCommands.add(new BotCommand("/compareWeek", "сравнить с предыдущей неделей"));
        listofCommands.add(new BotCommand("/month", "показать статистику за месяц"));


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

    /**
     * Если нам прислали текст или значение
     */

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatID = update.getMessage().getChatId();

            if (messageText.contains("/send") && (config.getOwnerId() == chatID)) {
                var textToSend
                        = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();
                for (User user : users) {
                    prepareAndSendMessage(user.getChatId(), textToSend);
                }

                /**
                 * Если юзер передает дату в формате ЧЧ:ММ
                 */

            } else if (messageText.matches("^\\d{2}:\\d{2}$")) {
                log.info("Пользователь ввел время для вопросов");
                setTextTimetoQuestions(messageText);
                verificationTimeQuestion(chatID, messageText);
            } else if (messageText.matches("^\\d{2};\\d{2}$")) {
                prepareAndSendMessage(chatID, "Для установки времени для вопросов \n\n" +
                        "Замените ; на : \n\nНапример: 20:30");
            } else {

                switch (messageText /*.toLowerCase()*/) {
                    case "/start":
                        registerUser(update.getMessage(), update);
                        startCommandReceived(chatID, update.getMessage().getChat().getFirstName());
                        break;
                    case "/help":
                        prepareAndSendMessage(chatID, HELP_TEXT);
                        break;
                    case "/when":
                        prepareAndSendMessage(chatID, sendQuestAboutTimeToQuestion);
                        break;
                    case "/week":
                        sendChart(chatID,weekValues.getMeanQuest(chatID));
                        sendMessage(chatID,getStat7Days.getStatFrom7days(chatID));
                        break;
                    case "/compareWeek":
                        sendMessage(chatID,compareWeekLastWeek.compareWeekAndLastWeek(chatID));
                        break;
                    case "/month":
                        prepareAndSendMessage(chatID, "Функция в разработке");
                        break;
                    case "/deleteAll":
                        prepareAndSendMessage(chatID, "Ваши данные полностью удалены");
                        deleteUserInformation.deleteDataUser(chatID);
                        break;
                    default:
                        prepareAndSendMessage(chatID, "Я не знаю, как работать с этой командой \n\n" +
                                "Но я думаю, вам поможет это /help");
                }

            }

        }

        /**
         * провереям, вдруг помимо текста нам передали значение
         */

        else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if (callBackData.equals(YES_BUTTON)) {
                executeEditMessageText("Ты нажал(а) ДА", chatId, messageId);
                timeToQuestions(chatId);

            } else if (callBackData.equals(NO_BUTTON)) {
                executeEditMessageText("Вы нажали Нет \n\nНо если передумаете, введите еще раз" +
                        " команду /start \uD83D\uDE09", chatId, messageId);

            } else if (callBackData.equals(NO_BUTTON_verificationTimeQuestion)) {
                log.info("Пользователь ввел некоректные данные");
                sendMessage(chatId, "Попробуйте еще раз");
                timeToQuestions(chatId);

            } else if (callBackData.equals(YES_BUTTON_verificationTimeQuestion)) {
                log.info("Пользователь ввел корректные данные");
                addTimeToDB(chatId, getTextTimetoQuestions());
                sendMessage(chatId, "Супер!️ Я сохранил время, в которое тебе будут приходить вопросы." +
                        " \n\n" +
                        "Если захочешь его изменить, можешь просто написать боту новое время, например:" +
                        " 20:30 \n\n" +
                        "Он поймет ☺");
                addDataBaseQuest(chatId);
            } else if (callBackData.startsWith("BUTTON_")) {

                String[] data = callBackData.split("_");
                int answer = Integer.parseInt(data[1]);
                String emojiNumber = createEmoji.createFunnyEmoji(answer);
                executeEditMessageText("Вы оценили на  " + emojiNumber, chatId, messageId);
                String quests = data[2];
                saveAnswerToDb(chatId, quests, answer);
                checkDateAndChatId(chatId);

            }
        }
    }

    public String getTextTimetoQuestions() {
        return textTimetoQuestions;
    }

    public void setTextTimetoQuestions(String textTimetoQuestions) {
        this.textTimetoQuestions = textTimetoQuestions;
    }

    private void timeToQuestions(long chatID) {

        sendMessage(chatID, sendQuestAboutTimeToQuestion);
    }

    private void registerUser(Message msg, Update update) {

        /**
         * провереям, существует ли данный пользователь
         */

        if (userRepository.findById(msg.getChatId()).isEmpty()) {
            User user = new User();
            user.setChatId(msg.getChatId());
            user.setFirstName(update.getMessage().getChat().getFirstName());
            user.setLastName(update.getMessage().getChat().getLastName());
            user.setUserName("@" + update.getMessage().getChat().getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("Saved user: " + user);
        }
    }


    private void startCommandReceived(long chatID, String name) {
        sendMessage(chatID, name + START_MESSAGE);
        log.info("Replied to user" + name);
        smartKeyboard(chatID, "ДА", "Нет");

    }


    public void sendMessage(long chatID, String texToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(texToSend);
        executedMessage(message);
    }

//    public void sendChart(long chatID,Map<String, Double> chartToSend){
//        log.info("Начался метод sendChart ");
//        String labels = chartToSend.entrySet().stream()
//                .filter(entry -> entry.getValue() != null && entry.getValue() != 0 && entry.getValue() != 0.0)
//                .map(Map.Entry::getKey)
//                .map(label -> "'" + label + "'")
//                .collect(Collectors.joining(", "));
//
//        String data = chartToSend.entrySet().stream()
//                .filter(entry -> entry.getValue() != null && entry.getValue() != 0 && entry.getValue() != 0.0)
//                .map(Map.Entry::getValue)
//                .map(String::valueOf)
//                .collect(Collectors.joining(", "));
//        log.info("START");
//        try {
//            QuickChart chart = new QuickChart();
//            chart.setWidth(800);
//            chart.setHeight(600);
//            chart.setBackgroundColor("#141449");
//            chart.setConfig("{"
//                    + "type: 'polarArea',"
//                    + "data: {"
//                    + "labels: [" + labels + "],"
//                    + "datasets: [{"
//                    + "data: [" + data + "]"
//                    + "}]"
//                    + "},"
//                    + "options: {"
//                    + "title: {"
//                    + "display: true,"
//                    + "text: 'Отчет за последние 7 дней:'," // добавление надписи
//                    + "fontColor: 'white',"
//                    + "fontSize: 30" // увеличение размера шрифта
//                    + "},"
//                    + "legend: {"
//                    + "position: 'right',"
//                    + "labels: {"
//                    + "fontColor: 'white',"
//                    + "fontSize: 25" // увеличение размера шрифта
//                    + "}"
//                    + "},"
//                    + "scale: {"
//                    + "gridLines: {"
//                    + "color: '#9E9E9E'"
//                    + "},"
//                    + "ticks: {"
//                    + "min: 0,"
//                    + "max: 10,"
//                    + "}"
//                    + "}"
//                    + "}"
//                    + "}");
//            // Get the image
//            byte[] imageBytes = chart.toByteArray();
//            log.info("FINAL");
//
//            // Send the image to the user via Telegram bot
//            SendPhoto sendPhotoRequest = new SendPhoto();
//            sendPhotoRequest.setChatId(chatID);
//            sendPhotoRequest.setPhoto(new InputFile(chart.getUrl()));
//            execute(sendPhotoRequest);
//        }
//        catch (Exception e){
//            log.info("ERROR create chart "+e);
//            e.printStackTrace();
//        }
//
//    }

    public void sendChart(long chatID, Map<String, Double> chartToSend) {
        log.info("Начался метод sendChart ");
        String labels = chartToSend.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue() != 0 && entry.getValue() != 0.0)
                .map(entry -> entry.getKey() + ": " + entry.getValue()) // добавляем значение key и value
                .map(label -> "'" + label + "'")
                .collect(Collectors.joining(", "));

        String data = chartToSend.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue() != 0 && entry.getValue() != 0.0)
                .map(Map.Entry::getValue)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        log.info("START");
        try {
            QuickChart chart = new QuickChart();
            chart.setWidth(800);
            chart.setHeight(600);
            chart.setBackgroundColor("#141449");
            chart.setConfig("{"
                    + "type: 'polarArea',"
                    + "data: {"
                    + "labels: [" + labels + "],"
                    + "datasets: [{"
                    + "data: [" + data + "]"
                    + "}]"
                    + "},"
                    + "options: {"
                    + "title: {"
                    + "display: true,"
                    + "text: 'Отчет за последние 7 дней:'," // добавление надписи
                    + "fontColor: 'white',"
                    + "fontSize: 30" // увеличение размера шрифта
                    + "},"
                    + "legend: {"
                    + "position: 'right',"
                    + "labels: {"
                    + "fontColor: 'white',"
                    + "fontSize: 25" // увеличение размера шрифта
                    + "}"
                    + "},"
                    + "scale: {"
                    + "gridLines: {"
                    + "color: '#9E9E9E'"
                    + "},"
                    + "ticks: {"
                    + "display: false,"  // удаление цифр
                    + "min: 0,"
                    + "max: 10,"
                    + "}"
                    + "}"
                    + "}"
                    + "}");


            // Get the image
            byte[] imageBytes = chart.toByteArray();
            log.info("FINAL");

            // Send the image to the user via Telegram bot
            SendPhoto sendPhotoRequest = new SendPhoto();
            sendPhotoRequest.setChatId(chatID);
            sendPhotoRequest.setPhoto(new InputFile(chart.getUrl()));
            execute(sendPhotoRequest);
        } catch (Exception e) {
            log.info("ERROR create chart " + e);
            e.printStackTrace();
        }
    }


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

    /**
     * Если будет необходимо отправлять сообщение всем пользователям одновременно
     * Админ может вызвать эту команду из без @Scheduled с помощью команды /send
     */

    // @Scheduled(cron = "${interval-in-cron} ")
    private void SendAskUser() {
        var askUser = sendAllUserRepository.findAll();
        var users = userRepository.findAll();
        for (SendAllUser ask : askUser) {
            for (User user : users) {
                prepareAndSendMessage(user.getChatId(), ask.getTextAskUser());
            }
        }
    }

    /**
     * Меняет уже выведенный текст
     */


    private void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_OCCURED + e.getMessage());
        }
    }

    public void executedMessage(SendMessage message) {
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

    /**
     * Создает смарт клавиатуру с двумя кнопками
     */
    private void smartKeyboard(long chatID, String yes, String no) {
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
        executedMessage(message);
    }

    /**
     * Если пользователь ввел числовое значение, проверяем Маску на добавление времени для вопросов
     */

    private void verificationTimeQuestion(long chatID, String messageText) {
        String[] parts = messageText.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        if (hour < 0 || hour >= 24 || minute < 0 || minute >= 60) {
            prepareAndSendMessage(chatID, "Неправильный формат даты, попробуйте еще раз");
            return;
        }

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
        executedMessage(message);
    }

    private InlineKeyboardButton createInlineKeyboardButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    /**
     * Добавляет введенное время пользователя в базу данных в формате cron
     */
    private void addTimeToDB(long chatId, String timeToQuestions) {

        String[] parts = timeToQuestions.split(":");
        timeToQuestions = "* " + parts[1] + " " + parts[0] + " * " + "*" + " *";
        log.error(timeToQuestions);
        User user = userRepository.findById(chatId).orElseThrow(() -> new EntityNotFoundException());
        user.setTimeToQuestions(timeToQuestions);
        userRepository.save(user);
        log.info("Добавили время в базу данных" + user);
    }

    private void addDataBaseQuest(Long chatId) {
        Map<String, String> questions = new HashMap<>();
        questions.put("Здоровье", "Как ты оцениваешь свое здоровье?");
        questions.put("Работа", "Как ты оцениваешь свою работу?");
        questions.put("Саморазвитие", "Как ты оцениваешь свое саморазвитие?");
        questions.put("Деньги, капитал", "Как ты оценивешь свое имущество? (деньги,капитал)");
        questions.put("Материальный мир", "Как ты оцениваешь свой материальный мир?");
        questions.put("Отношения", "Как ты оцениваешь свои отношения?");
        questions.put("Развлечения", "Как ты оцениваешь свои развлечения?");
        questions.put("Семья", "Как ты оцениваешь отношения в семье?");
        questions.put("Внешность", "Как ты оцениваешь свою привлекательность?");
        questions.put("Друзья", "Как ты оцениваешь свое общение с друзьями?");

        try {
            for (Map.Entry<String, String> entry : questions.entrySet()) {
                DataBaseQuest userDB = new DataBaseQuest();
                DataBaseQuestId id = new DataBaseQuestId();
                id.setChatId(chatId);
                id.setQuest(entry.getKey());
                id.setQuestString(entry.getValue());
                userDB.setId(id);
                dataBaseQuestRepository.save(userDB);
            }
            log.info("User updated time of receiving questions: " + chatId);
            System.out.println("User updated time of receiving questions");
        } catch (Exception e) {
            log.error("Error saving user to DB: " + chatId, e);
        }
    }

    /**
     * Проверка, есть ли в данную минуту пользователи, которым мы должны отпрвить вопросы
     */
    @Scheduled(cron = "0 * * * * *")
    public void schedulerService() {
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            String cronExpression = user.getTimeToQuestions();
            Long chat_id = user.getChatId();
            try {
                CronSequenceGenerator generator = new CronSequenceGenerator(cronExpression);
                Date nextExecutionTime = generator.next(new Date());
                Date currentDate = new Date();
                if (nextExecutionTime != null && nextExecutionTime.getMinutes() == currentDate.getMinutes()
                        && nextExecutionTime.getHours() == currentDate.getHours()) {
                    log.info("Время cron соответствует текущему времени");
                    checkDateAndChatId(chat_id);
                }
            } catch (IllegalArgumentException e) {
                log.info("Error: " + e);
            }
        }
    }

    /**
     * База данных выдает 1 рандомный вопрос из вариантов, подподающих под условие
     */

    public void checkDateAndChatId(Long chat_id) {
        List<String> questsToday = null;
        List<Map<String, String>> quests = null;
        try {
            quests = jdbcTemplate.query(createQueryToCheck3Days.sql(chat_id)
                    , new Object[]{chat_id}, (rs, rowNum) ->
                    new HashMap<String, String>() {{
                        put("quest", rs.getString("quest"));
                        put("quest_string", rs.getString("quest_string"));
                    }});
            questsToday = jdbcTemplate.query(createQueryToCheck3Days.sqlToday(chat_id)
                    , new Object[]{chat_id}, (rs, rowNum)
                    -> rs.getString("quest"));
        } catch (Exception e) {
            log.error("Error while executing query", e);
        }
        log.info("questToday = " + questsToday.size());

        if (questsToday != null && questsToday.size() <= 10 && questsToday.size() > 7) {
            Map<String, String> quest = quests.get(0);
            log.info("ЭТАП 1: quests + quest_string = " + quests);
            sendQuest(chat_id, quest);
        } else {
            sendEndMessage(chat_id);
        }
    }

    public void sendQuest(Long chatId, Map<String, String> questMap) {
        System.out.println("User " + chatId + " Received questions ");
        String questValue = questMap.get("quest");
        String questStringValue = questMap.get("quest_string");

        try {
            execute(getSticker.addStiker(questValue, chatId));
        } catch (Exception e) {
            log.info("Error" + e);
        }
        sendMessage(chatId, questStringValue);
        getKeyboard(chatId, questValue);

    }


    /**
     * Создаем клавиатуру для ответов
     * Все кнопки создаются по формату "BUTTON_" + "Номер вопроса" + "Сам вопрос"
     * Эта информация необходима потом для занесения данных в БД
     */
    private void getKeyboard(Long chatID, String quest) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText("Оцени от 1 до 10");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            String answerNumber = String.valueOf(i);
            try {
                rowInline.add(createInlineKeyboardButton(answerNumber, "BUTTON_" + answerNumber + "_"
                        + quest));
            } catch (Exception e) {
                log.info("ОШИИБКА СОЗДАНИЯ КЛАВИАТУРЫ " + e);
            }

            if (rowInline.size() == 5) {
                rowsInLine.add(rowInline);
                rowInline = new ArrayList<>();
            }
        }
        rowsInLine.add(rowInline);
        markupInline.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInline);
        executedMessage(message);
    }

    /**
     * Сохраняем значение в БД
     */
    private void saveAnswerToDb(long chatId, String question, int answer) {
        log.info("Сохраняем значение " + chatId + question + answer);
        LocalDate today = LocalDate.now();
        String formattedTodayDate = today.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String dateColumn = "date_" + formattedTodayDate;
        String sql = "UPDATE data_base_quest SET " + dateColumn + " = ? WHERE chat_id = ? AND quest = ?";
        jdbcTemplate.update(sql, answer, chatId, question);
    }


    /**
     * Если все вопросы на сегодня заданы, завершающее сообщение
     */
    private void sendEndMessage(long chatId) {
        sendMessage(chatId, thxForAsking);
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            sendMessage(chatId,getStat7Days.getStatFrom7days(chatId));
            log.info("Проверка на воскресенье");
        }
    }
}



