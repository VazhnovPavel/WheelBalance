package com.testSpringBoot.SpringDemoBot.service;

import com.testSpringBoot.SpringDemoBot.config.BotConfig;
import com.testSpringBoot.SpringDemoBot.model.*;
import com.testSpringBoot.SpringDemoBot.statistic.CompareWeekLastWeek;
import com.testSpringBoot.SpringDemoBot.statistic.GetStatCurrentDays;
import com.testSpringBoot.SpringDemoBot.statistic.LastWeekValues;
import com.testSpringBoot.SpringDemoBot.statistic.WeekValues;
import com.testSpringBoot.SpringDemoBot.visual.CreateEmoji;
import com.testSpringBoot.SpringDemoBot.visual.GetResultEmoji;
import com.testSpringBoot.SpringDemoBot.visual.GetSticker;
import com.vdurmont.emoji.EmojiParser;
import io.quickchart.QuickChart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;



    @Slf4j
    @Component
public class TelegramBot extends TelegramLongPollingBot {


        private ArrayList<Long> listReportingUser = new ArrayList<>();
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public TelegramBot(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
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
    private GetStatCurrentDays getStatCurrentDays;
    @Autowired
    private CreateEmoji createEmoji;
    @Autowired
    private CreateQueryToCheck3Days createQueryToCheck3Days;

    @Value("${bot.Owner}")
    private Long botOwner;

    static final String START_MESSAGE = ", привет! \uD83E\uDEF6 \nЯ помогу тебе отслеживать твое состояние во всех основных сферах " +
            "жизни.\n\n Я буду ежедневно задавать тебе простые вопросы о сферах твоей жизни, " +
            "а тебе нужно будет ответить по десятибалльной шкале \u0031\u20E3 - \uD83D\uDD1F, насколько ты удовлетворен на данный момент.\n\n " +
            "А в конце недели/месяца/года мы с тобой будем подводить итоги, как идут у нас успехи \uD83D\uDE42 \n\n";
    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String NO_BUTTON_DELETE = "NO_BUTTON_DELETE";
    static final String YES_BUTTON_DELETE = "YES_BUTTON_DELETE";
    static final String TEXT_ABOVE_KEYBOARD_START = "Попробуем?";
    static final String TEXT_ABOVE_KEYBOARD_DELETE = "Точно?";
    static final String YES_BUTTON_verificationTimeQuestion = "YES_BUTTON_verificationTimeQuestion";
    static final String NO_BUTTON_verificationTimeQuestion = "NO_BUTTON_verificationTimeQuestion";
    static final String ERROR_OCCURED = "Error occurred: ";
    static final String WEEK_STRING = "Текущая неделя";
    static final String WEEK_LAST_STRING = "Предыдущая неделя";
    static final String WEEK_COMPARE_STRING = "Сравнение текущей и предыдущей недели";
    static final String WEEK_COMPARE_TEXT = "Сравниваем эту и предыдущую неделю:\n\n";
    static final String MONTH_STRING = "Текущий месяц";
    static final String MONTH_LAST_STRING = "Предыдущий месяц";
    static final String MONTH_COMPARE_STRING = "Сравнение текущего и предыдущего месяца";
    static final String MONTH_COMPARE_TEXT = "Сравниваем этот и предыдущий месяц:\n\n";
    static final String SUNDAY_TEXT = "Давай подведем итоги нашей недели: \n\n";
    private String textTimetoQuestions;
    static final String MESSAGE_ = "Сравниваем этот и предыдущий месяц:\n\n";
    final private String sendQuestAboutTimeToQuestion = "\n\nВ какое время тебе было бы удобно получать вопросы?\n" +
            "Напиши в формате ЧЧ:ММ , например 20:30\n" +
            "(по московскому времени)";
    final private String sendTextToReport = "Опиши свою идею или ошибку. Можно добавить скриншоты. \n\n" +
            "Важно отправить текст и изображения ОДНИМ сообщением. Если нужно будет в дальнейшем, я с тобой свяжусь)";
    final private String thxForAsking = "Спасибо за ответы! Завтра спишемся в то же время \uD83D\uDE09\n \n\n" +
            "Узнать статистику за последние 7 дней /week\n\n"+
            "Узнать статистику за последние 30 дней /month\n\n"+
            "Список всех статистик /statistic\n\n";


    static final String HELP_TEXT =
            "/start - запустить бота \n\n" +
                    "/statistic - вся интересная статистика тут) \n\n" +
                    "/when - настроить время для вопросов \n\n" +
                    "/report - сообщить об ошибке / предложить идею \n\n" +
                    "/deleteAll - удалить все твои персональные данные из бота \n\n";

    static final String HELP_STATISTIC =
                    "/week - показать статистику за 7 дней \n\n" +
                    "/compareWeek - сравнить текущие 7 дней с 7-ю предыдущими днями \n\n" +
                    "/month - показать статистику за 30 дней \n\n" +
                    "/compareMonth - сравнить текущие 30 дней с 30-ю предыдущими днями \n\n"+
                    "/help - вывести все команды \n\n";


    public TelegramBot(BotConfig config) {
        this.config = config;

        /**
         * Create list menu, add command
         */

        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Start"));
        listofCommands.add(new BotCommand("/help", "вывести все команды "));
        listofCommands.add(new BotCommand("/statistic", "вывести все статистики "));
        listofCommands.add(new BotCommand("/when", "настроить время для вопросов"));
        listofCommands.add(new BotCommand("/week", "показать статистику за неделю"));
        listofCommands.add(new BotCommand("/compareWeek", "сравнить с предыдущей неделей"));
        listofCommands.add(new BotCommand("/month", "показать статистику за месяц"));
        listofCommands.add(new BotCommand("/compareMonth", "сравнить с предыдущим месяцем"));
        listofCommands.add(new BotCommand("/report", "отправить сообщение об ошибке"));
        listofCommands.add(new BotCommand("/deleteAll", "удалить все данные о пользователе"));


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
                 * Если юзер передает дату в формате ЧЧ:ММ или Ч:ММ
                 */

            }

            else if (messageText.matches("^\\d{1,2}:\\d{2}$")) {
                log.info("Пользователь ввел время для вопросов");
                setTextTimetoQuestions(messageText);
                verificationTimeQuestion(chatID, messageText);
            } else if (messageText.matches("^\\d{2};\\d{2}$")) {
                prepareAndSendMessage(chatID, "Для установки времени для вопросов \n\n" +
                        "Замени ; на : \n\nНапример: 20:30");
            }



            else {

                switch (messageText /*.toLowerCase()*/) {
                    case "/start":
                        registerUser(update.getMessage(), update);
                        startCommandReceived(chatID, update.getMessage().getChat().getFirstName());
                        break;
                    case "/help":
                        prepareAndSendMessage(chatID, HELP_TEXT);
                        break;
                    case "/statistic":
                        prepareAndSendMessage(chatID, HELP_STATISTIC);
                        break;
                    case "/when":
                        prepareAndSendMessage(chatID, sendQuestAboutTimeToQuestion);
                        break;
                    case "/week":
                        sendPieChart(chatID,weekValues.getMeanQuest(chatID,7),7);
                        sendMessage(chatID, getStatCurrentDays.getStatFromCurrentDays(chatID,7));
                        break;
                    case "/compareWeek":
                        sendRadarChart(chatID,weekValues.getMeanQuest(chatID,7),
                                lastWeekValues.getMeanQuest(chatID), WEEK_STRING, WEEK_LAST_STRING,
                                WEEK_COMPARE_STRING,7);
                        sendMessage(chatID,compareWeekLastWeek.compareWeekAndLastWeek(chatID,7,
                                WEEK_COMPARE_TEXT));
                        break;
                    case "/month":
                        sendPieChart(chatID,weekValues.getMeanQuest(chatID,30),30);
                        sendMessage(chatID, getStatCurrentDays.getStatFromCurrentDays(chatID,30));
                        break;
                    case "/compareMonth":
                        sendRadarChart(chatID,weekValues.getMeanQuest(chatID,30),
                                lastWeekValues.getMeanQuest(chatID), MONTH_STRING, MONTH_LAST_STRING,
                                MONTH_COMPARE_STRING,30);
                        sendMessage(chatID,compareWeekLastWeek.compareWeekAndLastWeek(chatID,30,
                                MONTH_COMPARE_TEXT));
                        break;
                    case "/report":
                        sendMessage(chatID, sendTextToReport);
                        listReportingUser.add(chatID);
                        break;
                    case "/deleteAll":
                        prepareAndSendMessage(chatID, "Ты хочешь удалить все данные без " +
                                "возможности восстановления?");
                        smartKeyboard(chatID, "Да", "Нет","YES_BUTTON_DELETE",
                                "NO_BUTTON_DELETE",TEXT_ABOVE_KEYBOARD_DELETE);
                        break;
                    default:
                        if (checkOnReportMessage(chatID,update))
                            break;

                        else prepareAndSendMessage(chatID, "Я не знаю, как работать с этой командой \n\n" +
                                "Но я думаю, тебе поможет это /help");

                }


            }

        }
        else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            // Если сообщение содержит фото, отправляем его вместе с сообщением обратной связи администратору
            long chatID = update.getMessage().getChatId();
            checkOnReportMessage(chatID,update);
            listReportingUser.remove(chatID);
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
                executeEditMessageText("Ты нажал(а) Нет \n\nНо если передумаешь, введи еще раз" +
                        " команду /start \uD83D\uDE09", chatId, messageId);

            } else if (callBackData.equals(NO_BUTTON_DELETE)) {
                executeEditMessageText( "Ты нажал(а) Нет \nВернуться к списку команд /help", chatId,messageId);

            } else if (callBackData.equals(YES_BUTTON_DELETE)) {
                executeEditMessageText( "Ты нажал(а) Да \n Твои персональные данные полностью удалены \n\n" +
                        "Для запуска бота еще раз /start ",chatId,messageId);
                deleteUserInformation.deleteDataUser(chatId);

            } else if (callBackData.equals(NO_BUTTON_verificationTimeQuestion)) {
                log.info("Пользователь ввел некоректные данные");
                executeEditMessageText("Попробуй еще раз",chatId, messageId);
                timeToQuestions(chatId);

            } else if (callBackData.startsWith("YES_BUTTON_verificationTimeQuestion_")) {
                log.info("Пользователь ввел корректные данные");
                String[] data = callBackData.split("_");
                addTimeToDB(chatId, data[3]);
              //  addTimeToDB(chatId, getTextTimetoQuestions());
                executeEditMessageText( "Супер!️ Я сохранил время, в которое тебе будут приходить вопросы." +
                        " \n\n" +
                        "Если захочешь его изменить, можешь просто написать боту новое время, например:" +
                        " 20:30 \n\n" +
                        "Он поймет \uD83D\uDE42",chatId,messageId);
                addDataBaseQuest(chatId);
            } else if (callBackData.startsWith("BUTTON_")) {

                String[] data = callBackData.split("_");
                int answer = Integer.parseInt(data[1]);
                String emojiNumber = createEmoji.createFunnyEmoji(answer);
                executeEditMessageText("Ты оценил(а) на  " + emojiNumber, chatId, messageId);
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

        // get the input stream for the image file
        InputStream inputStream = getClass().getResourceAsStream("/Start.png");

        try {
            // create an InputFile object from the input stream
            InputFile inputFile = new InputFile(inputStream, "Start.png");

            // create a SendPhoto object and set its parameters
            SendPhoto sendPhotoRequest = new SendPhoto();
            sendPhotoRequest.setChatId(chatID);
            sendPhotoRequest.setPhoto(inputFile);

            // send the photo to the chat
            execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        smartKeyboard(chatID, "Да", "Нет","YES_BUTTON","NO_BUTTON",
                TEXT_ABOVE_KEYBOARD_START);
    }








    public void sendMessage(long chatID, String texToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(texToSend);
        executedMessage(message);
    }




    public void sendPieChart(long chatID, Map<String, Double> chartToSend, int currentDays) {


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

        // Calculate the date range
        Calendar endDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -currentDays +1);
        DateFormat dateFormatFirst = new SimpleDateFormat("d MMMM", new Locale("ru"));
        DateFormat dateFormatSecond = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));
        //Если год один, то пишем один раз. Если года разные - выводим оба
        String titleString;
        if (startDate.get(Calendar.YEAR) == endDate.get(Calendar.YEAR)) {
            titleString = String.format("@Wheel_Balance_bot                 Отчет c %s по %s года",
                    dateFormatFirst.format(startDate.getTime()),
                    dateFormatSecond.format(endDate.getTime()));
        } else {
            titleString = String.format("@Wheel_Balance_bot                 Отчет c %s года по %s года",
                    dateFormatSecond.format(startDate.getTime()),
                    dateFormatSecond.format(endDate.getTime()));
        }


        try {
            QuickChart chart = new QuickChart();
            chart.setWidth(900);
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
                    + "text: '" + titleString + "',"
                    + "fontColor: 'grey',"
                    + "fontSize: 25,"
                    + "fontFamily: 'Roboto'"
                    + "},"
                    + "legend: {"
                    + "position: 'left',"
                    + "labels: {"
                    + "fontColor: 'white',"
                    + "fontSize: 22,"
                    + "fontFamily: 'Roboto'"
                    + "}"
                    + "},"
                    + "scale: {"
                    + "gridLines: {"
                    + "color: '#9E9E9E'"
                    + "},"
                    + "ticks: {"
                    + "display: false,"
                    + "min: 0,"
                    + "max: 10,"
                    + "}"
                    + "},"
                    + "plugins: {"
                    + "datalabels: {"
                    + "color: 'white',"
                    + "font: {"
                    + "size: 18,"
                    + "family: 'Roboto'"
                    + "},"
                    + "display: true"
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



    public void sendRadarChart(long chatID, Map<String, Double> mapFirst, Map<String, Double> mapSecond,
                               String firstCompareName, String secondCompareName, String titleChart, int currentDays) {

        String labels = mapFirst.keySet().stream()
                .filter(key -> mapFirst.get(key) != null && mapFirst.get(key) != 0 && mapFirst.get(key) != 0.0)
                .map(key -> "'" + key + "'")
                .collect(Collectors.joining(", "));

        String data1 = mapFirst.values().stream()
                .filter(value -> value != null && value != 0 && value != 0.0)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        String data2 = mapSecond.values().stream()
                .filter(value -> value != null && value != 0 && value != 0.0)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        // Calculate the date range
        Calendar endDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        //Увеличиваем currentDays в 2 раза и 1 день для корректности периода
        startDate.add(Calendar.DAY_OF_MONTH, (-currentDays * 2) +1);
        DateFormat dateFormatFirst = new SimpleDateFormat("d MMMM", new Locale("ru"));
        DateFormat dateFormatSecond = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));
        //Если год один, то пишем один раз. Если года разные - выводим оба
        String titleString;
        if (startDate.get(Calendar.YEAR) == endDate.get(Calendar.YEAR)) {
            titleString = String.format("@Wheel_Balance_bot                         Отчет c %s по %s года",
                    dateFormatFirst.format(startDate.getTime()),
                    dateFormatSecond.format(endDate.getTime()));
        } else {
            titleString = String.format("@Wheel_Balance_bot                         Отчет c %s года по %s года",
                    dateFormatSecond.format(startDate.getTime()),
                    dateFormatSecond.format(endDate.getTime()));
        }


        try {
            QuickChart chart = new QuickChart();
            chart.setWidth(900);
            chart.setHeight(600);
            chart.setBackgroundColor("#141449");
            chart.setConfig("{"
                    + "type: 'radar',"
                    + "data: {"
                    + "labels: [" + labels + "],"
                    + "datasets: [{"
                    + "label: '"+firstCompareName+"',"
                    + "data: [" + data1 + "],"
                    + "backgroundColor: 'rgba(255, 99, 132, 0.2)',"
                    + "borderColor: 'rgba(255, 99, 132, 1)',"
                    + "borderWidth: 2,"
                    + "pointBackgroundColor: 'rgba(255, 99, 132, 1)'"
                    + "}, {"
                    + "label: '"+secondCompareName+"',"
                    + "data: [" + data2 + "],"
                    + "backgroundColor: 'rgba(54, 162, 235, 0.2)',"
                    + "borderColor: 'rgba(54, 162, 235, 1)',"
                    + "borderWidth: 2,"
                    + "pointBackgroundColor: 'rgba(54, 162, 235, 1)'"
                    + "}]"
                    + "},"
                    + "options: {"
                    + "title: {"
                    + "display: true,"
                    + "text: '" + titleString + "',"
                    + "fontColor: 'grey',"
                    + "fontSize: 25,"
                    + "fontFamily: 'Roboto'"
                    + "},"
                    + "legend: {"
                    + "position: 'left',"
                    + "labels: {"
                    + "fontColor: 'white',"
                    + "fontSize: 22,"
                    + "fontFamily: 'Roboto'"
                    + "}"
                    + "},"
                    + "scale: {"
                    + "gridLines: {"
                    + "color: '#9E9E9E'"
                    + "},"
                    + "pointLabels: {"
                    + "fontSize: 18,"
                    + "fontColor: 'white'"
                    + "},"
                    + "ticks: {"
                    + "display: false,"
                    + "min: 0,"
                    + "max: 10,"
                    + "color: 'white'"
                    + "}"
                    + "},"
                    + "elements: {"
                    + "line: {"
                    + "tension: 0.4"
                    + "}"
                    + "}"
                    + "}"
                    + "}");



            // Get the image
            byte[] imageBytes = chart.toByteArray();

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
    private void smartKeyboard(long chatID, String yes, String no, String condition1, String condition2,
                               String textAboveKeyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(textAboveKeyboard);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(createInlineKeyboardButton(yes, condition1));
        rowInline.add(createInlineKeyboardButton(no, condition2));
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
            prepareAndSendMessage(chatID, "Неправильный формат даты, попробуй еще раз");
            return;
        }

        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText("Ты хочешь получать вопросы в " + hour + " часов " + minute + " минут? " +
                "(по московскому времени) ");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(createInlineKeyboardButton("Да, верно",
                "YES_BUTTON_verificationTimeQuestion_" + messageText));
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
    @Async
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
        message.setText("Оцени от 0 до 10");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            String answerNumber = String.valueOf(i);
            try {
                rowInline.add(createInlineKeyboardButton(answerNumber, "BUTTON_" + answerNumber + "_" + quest));
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




//        private void sendFeedbackToAdmin(Long chatId, Message message) {
//            String feedback = message.getText();
//            if (message.hasPhoto()) {
//                // Если сообщение содержит фото, получаем информацию о фото и отправляем ее вместе с сообщением
//                // в качестве описания к фото
//                feedback += "\n(Фото)";
//            }
//
//            sendMessage(350511326, "Отзыв от пользователя:\n" + feedback);
//
//            if (message.hasPhoto()) {
//                // Если сообщение содержит фото, отправляем фото администратору
//                List<PhotoSize> photoSizes = message.getPhoto();
//                String fileId = photoSizes.get(0).getFileId();
//                SendPhoto sendPhoto = new SendPhoto();
//                        sendPhoto.setChatId(350511326L);
//                        sendPhoto.setPhoto(new InputFile(fileId));
//
//                try {
//                    // Отправляем фото
//                    execute(sendPhoto);
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }
//            }
//            listReportingUser.remove(chatId);
//        }

        private void sendFeedbackToAdmin(Long chatId, Message message) {
            String username = "";
            if (message.getChat().getUserName() != null   )
                username = "@" + message.getChat().getUserName();
            else
                username = chatId.toString();

            String feedback = "";
            if (message.hasPhoto()) {
                feedback = message.getCaption() != null ? message.getCaption() : ""; // Используем подпись к фото, если она есть
                feedback += "\n(Фото)";
            } else {
                feedback = message.getText() != null ? message.getText() : ""; // Используем текст сообщения, если фото нет
                feedback += "\n(Без фото)";
            }

            sendMessage(botOwner, "Отзыв от пользователя: " + username + " \n" + feedback);

            if (message.hasPhoto()) {
                // Если сообщение содержит фото, отправляем фото администратору
                List<PhotoSize> photoSizes = message.getPhoto();
                String fileId = photoSizes.get(0).getFileId();
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(botOwner);
                sendPhoto.setPhoto(new InputFile(fileId));

                try {
                    // Отправляем фото
                    execute(sendPhoto);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            sendMessage(chatId,"Спасибо! Я зафиксировал информацию, и скоро она дойдет до админа. \n" +
                    " Вернуться в главное меню\n /help");

        }



            /**
         * Если все вопросы на сегодня заданы, завершающее сообщение
         */
    private void sendEndMessage(long chatId) {
        sendMessage(chatId, thxForAsking);
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            sendMessage(chatId,SUNDAY_TEXT);
            sendPieChart(chatId, weekValues.getMeanQuest(chatId, 7), 7);
            sendMessage(chatId, getStatCurrentDays.getStatFromCurrentDays(chatId, 7));

            log.info("Проверка на воскресенье");
        }
        if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)) {
            sendPieChart(chatId, weekValues.getMeanQuest(chatId, 30), 30);
            sendMessage(chatId, getStatCurrentDays.getStatFromCurrentDays(chatId, 30));
        }
    }

        public boolean checkOnReportMessage(long chatID,Update update ) {
            if (update.hasMessage() && listReportingUser.contains(chatID) ) {
                        listReportingUser.remove(chatID);
                        sendFeedbackToAdmin(chatID, update.getMessage());
                        return true;
            }
            return false;
        }
}



