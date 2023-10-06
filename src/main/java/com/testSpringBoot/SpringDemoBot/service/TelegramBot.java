package com.testSpringBoot.SpringDemoBot.service;


import com.testSpringBoot.SpringDemoBot.Keyboard.Keyboard;
import com.testSpringBoot.SpringDemoBot.Keyboard.KeyboardNew;
import com.testSpringBoot.SpringDemoBot.config.BotConfig;
import com.testSpringBoot.SpringDemoBot.model.*;
import com.testSpringBoot.SpringDemoBot.model.User;
import com.testSpringBoot.SpringDemoBot.statistic.*;
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
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import javax.persistence.EntityNotFoundException;
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
import java.util.ArrayList;
import java.util.Calendar;





@Slf4j
    @Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private Onboarding onboarding;
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
    private PreviousStatValues previousStatValues;
    @Autowired
    private CurrentStatValues currentStatValues;
    @Autowired
    BotConfig config;
    @Autowired
    private GetResultEmoji getResultEmoji;
    @Autowired
    private CompareCurrentAndPreviousPeriod compareCurrentAndPreviousPeriod;
    @Autowired
    private GetStatCurrentPeriod getStatCurrentPeriod;
    @Autowired
    private CreateEmoji createEmoji;
    @Autowired
    private CreateQueryToCheck3Days createQueryToCheck3Days;
    @Autowired
    private EndStatisticFromCurrentPeriod endStatisticFromCurrentPeriod;
    @Autowired
    private DaysRegistered daysRegistered;

    @Autowired
    private CountUser countUser;
    @Autowired
    private PeriodHasData monthCategory;
    @Autowired
    private Keyboard keyboard;
    @Autowired
    private KeyboardNew keyboardNew;
    @Autowired
    private Chart newChart;

    private ArrayList<Long> whoAnsweredTodayList = new ArrayList<Long>();
    private Map<Long,String> stickerUser = new HashMap<>();
    public static final List<String> CATEGORY_LIST;

    static {
        ArrayList<String> tempList = new ArrayList<>();
        tempList.add("Здоровье");
        tempList.add("Работа");
        tempList.add("Саморазвитие");
        tempList.add("Деньги, капитал");
        tempList.add("Друзья");
        tempList.add("Отношения");
        tempList.add("Развлечения");
        tempList.add("Семья");
        tempList.add("Внешность");
        tempList.add("Материальный мир");

        CATEGORY_LIST = Collections.unmodifiableList(tempList);
    }
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
    static final String NOW_ASK_QUESTION = "NOW_ASK_QUESTION";
    static final String AFTER_ASK_QUESTION = "AFTER_ASK_QUESTION";
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
    static final String CHECK_DELETE_TEXT = "Ты хочешь удалить все данные без " +
            "возможности восстановления?";
    static final String UNKNOWN_COMMAND = "Я не знаю, как работать с этой командой \n\n" +
                "Но я думаю, тебе поможет это /help";
    static final String USER_NOT_WANT_STARTED = "Ты нажал(а) Нет \n\nНо если передумаешь, введи еще раз" +
                " команду /start \uD83D\uDE09";
    static final String USER_WANT_STARTED = "Ты нажал(а) ДА \n\nПрежде, чем ответить на вопросы, давай " +
            "зададим удобное время на будущее";
    static final String USER_WANT_DELETE_CONFIDENT_INFORMATION ="Ты нажал(а) Да " +
            "\n Твои персональные данные полностью удалены \n\n" +
            "Для запуска бота еще раз /start ";
    static final String USER_NOT_WANT_DELETE_CONFIDENT_INFORMATION = "Ты нажал(а) Нет " +
            "\nВернуться к списку команд /help";
    static final String SUCCESS_SAVE_TIME_TO_DB ="Супер!️ Я сохранил время, в которое тебе будут приходить вопросы." +
            " \n\n" +
            "Если захочешь его изменить, можешь просто написать боту новое время, например:" +
            " 20:30 \n\n" +
            "Он поймет \uD83D\uDE42";
    static final String YOU_CHOOSE ="Ты оценил(а) на  ";
    static final String YOU_CHOOSE_CATEGORY = "Пришли в чат стикер для  категории: ";

    private String textTimetoQuestions;
    static final String MESSAGE_ = "Сравниваем этот и предыдущий месяц:\n\n";
    final private String SEND_QUEST_ABOUT_TIME_TO_QUESTION = "\n\nВ какое время тебе было бы удобно получать вопросы?\n" +
            "Напиши в формате ЧЧ:ММ , например 20:30\n" +
            "(по московскому времени)";
    final private String SEND_TEXT_TO_REPORT = "Все замечания, предложения и найденные ошибки можно присылать" +
            " мне, @pavel_fortex " +
            "\n\nВыслушаю всех обязательно \uD83D\uDE0C";
    final private String THX_FOR_ASKING = "Спасибо за ответы! Завтра спишемся в заданное время \uD83D\uDE09\n \n\n" +
            "Узнать статистику за последние 7 дней /week\n\n"+
            "Узнать статистику за последние 30 дней /month\n\n"+
            "Список всех статистик /statistic\n\n";
    final private String THX_FOR_FEEDBACK = "Спасибо! Я зафиксировал информацию, и скоро она дойдет до админа. \n" +
            " Вернуться в главное меню\n /help";
    static final String WANT_ASK_NOW_NEW_USER = "Хочешь ответить на первые 3 вопроса сейчас, или в заданное время?";
    static final String WANT_ASK_NOW_OLD_USER = "Хочешь ответить на 3 вопроса сейчас, или в заданное время? \n\n" +
            "Если сейчас, то вопросы в заданное время на сегодня уже не придут";

    static final String STICKER_MESSAGE = "Ты можешь предложить свой стикер, выбрав одну из категорий. " +
            "Если стикер будет актуальным и забавным, я добавлю его в список, и он появится у всех " +
            "пользователей \uD83D\uDE0A \n\n";
    static final String HELP_TEXT =
            "/start - запустить бота \n\n" +
                    "/statistic - вся интересная статистика тут \uD83D\uDCCA \n\n" +
                    "/sticker - предложить свой стикер (beta)  \n\n" +
                    "/now - ответить на вопросы сейчас  \n\n" +
                    "/when - настроить время для вопросов \n\n" +
                    "/report - сообщить об ошибке / предложить идею \n\n" +
                    "/delete - удалить все твои персональные данные из бота \n\n";

    static final String HELP_STATISTIC =
                    "/week - показать статистику за 7 дней \n\n" +
                    "/compareWeek - сравнить текущие 7 дней с 7-ю предыдущими днями \n\n" +
                    "/month - показать статистику за 30 дней \n\n" +
                    "/compareMonth - сравнить текущие 30 дней с 30-ю предыдущими днями \n\n" +
                            "/monthCategory - данные за определённый месяц\n\n" +
                            "/all - данные за всё время \n\n"+
                            "/help - главное меню \n\n";

    private Long ownerId;


    public TelegramBot(BotConfig config) {
        this.config = config;
        /**
         * Добавляем наши команды в бота
         */

        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Start"));
        listofCommands.add(new BotCommand("/help", "вывести все команды "));
        listofCommands.add(new BotCommand("/statistic", "вывести все статистики "));
        listofCommands.add(new BotCommand("/sticker", "предложить свой стикер "));
        listofCommands.add(new BotCommand("/now", "ответить на вопросы сейчас "));
        listofCommands.add(new BotCommand("/when", "настроить время для вопросов"));
        listofCommands.add(new BotCommand("/week", "показать статистику за неделю"));
        listofCommands.add(new BotCommand("/compareWeek", "сравнить с предыдущей неделей"));
        listofCommands.add(new BotCommand("/month", "показать статистику за месяц"));
        listofCommands.add(new BotCommand("/compareMonth", "сравнить с предыдущим месяцем"));
        listofCommands.add(new BotCommand("/all", "данные за все время"));
        listofCommands.add(new BotCommand("/monthCategory", "категории по месяцам"));
        listofCommands.add(new BotCommand("/report", "отправить сообщение об ошибке"));
        listofCommands.add(new BotCommand("/delete", "удалить все данные о пользователе"));

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
     * Если в бот прислали текст, фото или значение
     */

    @Override
    public void onUpdateReceived(Update update) {
        this.ownerId = config.getOwnerId();
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatID = update.getMessage().getChatId();

            /**
             * Отправить сообщение всем пользователям (для админа или рекламы)
             */

            if (messageText.contains("/send") && (config.getOwnerId() == chatID)) {
                var textToSend
                        = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();
                for (User user : users) {
                    prepareAndSendMessage(user.getChat_id(), textToSend);
                }
            }

            /**
             * Узнать, сколько за сегодня присоединилось пользователей
             */

            if (messageText.contains("/count") && (config.getOwnerId() == chatID)) {

                String message = "Сегодня было зарегистрированно " + countUser.countDeadUserToday()+ " пользователей\n" +
                        "Из них " + countUser.countUserToday() + " живых пользователей";
                sendMessage(chatID,message);

            }

            /**
             * Если пользователь передает дату в формате ЧЧ:ММ или Ч:ММ
            */
            else if (messageText.matches("^\\d{1,2}:\\d{2}$")) {
                log.info("Пользователь ввел время для вопросов");
                setTextTimetoQuestions(messageText);
                verificationTimeQuestion(chatID, messageText);
            } else if (messageText.matches("^\\d{2};\\d{2}$")) {
                prepareAndSendMessage(chatID, "Для установки времени для вопросов \n\n" +
                        "Замени ; на : \n\nНапример: 20:30");
            }

            /**
             * Команды от пользователей
            */

            else {

                switch (messageText) {
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
                    case "/sticker":
                        prepareAndSendMessage(chatID, STICKER_MESSAGE);
                        executedMessage(keyboardNew.getKeyboard(chatID,CATEGORY_LIST));

                        break;
                    case "/now":
                        if (onboarding.checkTimeToQuestion(chatID)){
                            //запускаем метод в обход крона
                            smartKeyboard(chatID, "Сейчас", "Я передумал",NOW_ASK_QUESTION,AFTER_ASK_QUESTION,
                                    WANT_ASK_NOW_OLD_USER);
                       }
                        else {
                            sendMessage(chatID,"Для начала надо установить время по умолчанию");
                            prepareAndSendMessage(chatID, SEND_QUEST_ABOUT_TIME_TO_QUESTION);
                        }
                        break;
                    case "/when":
                        prepareAndSendMessage(chatID, SEND_QUEST_ABOUT_TIME_TO_QUESTION);
                        break;
                    case "/week":
                        sendPieChart(chatID, currentStatValues.getMeanQuest(chatID,7),
                                newChart.generateTitleString(7));
                        sendMessage(chatID, getStatCurrentPeriod.getStatFromCurrentDays(chatID,7));
                        break;
                    case "/compareWeek":
                        sendRadarChart(chatID, currentStatValues.getMeanQuest(chatID,7),
                                previousStatValues.getMeanQuest(chatID,7), WEEK_STRING, WEEK_LAST_STRING,
                                WEEK_COMPARE_STRING,7);
                        sendMessage(chatID, compareCurrentAndPreviousPeriod.compareWeekAndLastWeek(chatID,7,
                                WEEK_COMPARE_TEXT));
                        break;
                    case "/month":
                        sendPieChart(chatID, currentStatValues.getMeanQuest(chatID,30),
                                newChart.generateTitleString(30));
                        sendMessage(chatID, getStatCurrentPeriod.getStatFromCurrentDays(chatID,30));
                        break;
                    case "/compareMonth":
                        sendRadarChart(chatID, currentStatValues.getMeanQuest(chatID,30),
                                previousStatValues.getMeanQuest(chatID,30), MONTH_STRING, MONTH_LAST_STRING,
                                MONTH_COMPARE_STRING,30);
                        sendMessage(chatID, compareCurrentAndPreviousPeriod.compareWeekAndLastWeek(chatID,30,
                                MONTH_COMPARE_TEXT));
                        break;
                    case "/all":
                        int dayRegistered = (int) daysRegistered.daysUserRegistered(chatID);
                        sendPieChart(chatID, currentStatValues.getMeanQuest(chatID,dayRegistered),
                                newChart.generateTitleString(dayRegistered));
                        sendMessage(chatID, getStatCurrentPeriod.getStatFromCurrentDays(chatID,dayRegistered));
                        break;
                    case "/monthCategory":
                        executedMessage(keyboardNew.getKeyboard(chatID));
                        break;
                    case "/report":
                        sendMessage(chatID, SEND_TEXT_TO_REPORT);
                        break;
                    case "/delete":
                        prepareAndSendMessage(chatID, CHECK_DELETE_TEXT);
                        smartKeyboard(chatID, "Да", "Нет",YES_BUTTON_DELETE,
                                NO_BUTTON_DELETE,TEXT_ABOVE_KEYBOARD_DELETE);
                        break;
                    default:
                         prepareAndSendMessage(chatID,UNKNOWN_COMMAND );

                }
            }

        }
        /**
         * Если сообщение содержит стикер, отправляем его вместе
         * с сообщением о категории и chat_id  администратору
         */


        if (update.hasMessage() && update.getMessage().hasSticker()) {


            Long chatId = update.getMessage().getChatId();
            if (stickerUser.containsKey(chatId)) {
                sendMessage(ownerId, "Пришел стикер! От пользователя " + chatId);
                sendMessage(ownerId, "Категория: " + stickerUser.get(chatId));

                if (chatId != null && stickerUser.containsKey(chatId)) {
                    InputFile sticker = new InputFile(update.getMessage().getSticker().getFileId());
                    SendSticker sendSticker = new SendSticker();
                    sendSticker.setSticker(sticker);
                    sendSticker.setChatId(ownerId);
                    try {
                        execute(sendSticker);
                        sendMessage(chatId, "Стикер пришел админу! \n" +
                                "Надеюсь, ему понравится \uD83D\uDE47\u200D♂");
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    finally {
                        stickerUser.remove(chatId);
                    }
                }
            }
            else {
                sendMessage(chatId,"Если хочешь рекомендовать свой стикер, то тебе сюда /sticker");
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
                executeEditMessageText(USER_WANT_STARTED, chatId, messageId);
                timeToQuestions(chatId);

            } else if (callBackData.equals(NO_BUTTON)) {
                executeEditMessageText(USER_NOT_WANT_STARTED, chatId, messageId);

            } else if (callBackData.equals(NO_BUTTON_DELETE)) {
                executeEditMessageText(USER_NOT_WANT_DELETE_CONFIDENT_INFORMATION , chatId,messageId);

            } else if (callBackData.equals(YES_BUTTON_DELETE)) {
                executeEditMessageText(USER_WANT_DELETE_CONFIDENT_INFORMATION  ,chatId,messageId);
                deleteUserInformation.deleteDataUser(chatId);

            } else if (callBackData.equals(NO_BUTTON_verificationTimeQuestion)) {
                executeEditMessageText("Попробуй еще раз",chatId, messageId);
                timeToQuestions(chatId);

            } else if (callBackData.startsWith("YES_BUTTON_verificationTimeQuestion_")) {
                String[] data = callBackData.split("_");
                addTimeToDB(chatId, data[3]);
                executeEditMessageText( SUCCESS_SAVE_TIME_TO_DB,chatId,messageId);
                addDataBaseQuest(chatId);

                log.info("НАЧАЛАСЬ ПРОВЕРКА НА СЕГОДНЯШНИЙ ДЕНЬ");
                if (onboarding.checkOnboarding(chatId)){
                    //запускаем метод в обход крона
                    smartKeyboard(chatId, "Сейчас", "В заданное время",NOW_ASK_QUESTION,AFTER_ASK_QUESTION,
                            WANT_ASK_NOW_NEW_USER);

                }
            }
            else if (callBackData.equals(NOW_ASK_QUESTION)) {
                if (!whoAnsweredTodayList.contains(chatId)) {
                    executeEditMessageText("Значит отвечаем сейчас \uD83D\uDE43 ",chatId, messageId);
                    checkDateAndChatId(chatId);
                }
                else{
                    executeEditMessageText("Только 3 вопроса в день, не больше \uD83D\uDE43 ",chatId, messageId);
                }



            }
            else if (callBackData.equals(AFTER_ASK_QUESTION)) {
                executeEditMessageText("Спишемся в назначенное время (если сегодня еще не отвечал(а) " +
                        "на вопросы)" +
                        " \uD83D\uDE09 ",chatId, messageId);

            }else if (callBackData.startsWith("BUTTON_")) {

                String[] data = callBackData.split("_");
                int answer = Integer.parseInt(data[1]);
                String emojiNumber = createEmoji.createFunnyEmoji(answer);
                executeEditMessageText(YOU_CHOOSE + emojiNumber, chatId, messageId);
                String quests = data[2];
                // Получаем строку с датой
                String dateString = data[3];
                // Преобразуем строку в объект LocalDate
                LocalDate date = LocalDate.parse(dateString);

                saveAnswerToDb(chatId, quests, answer,date);
                if (date.equals(LocalDate.now())) {
                    checkDateAndChatId(chatId);
                }
            }
            else if (callBackData.startsWith("CATEGORY_")) {

                String[] data = callBackData.split("_");
                String chooseCategory = data[1];
                executeEditMessageText(YOU_CHOOSE_CATEGORY + chooseCategory + " \nЗа раз можно прислать только" +
                        " один стикер. \n\n", chatId, messageId);
                stickerUser.put(chatId,chooseCategory);

            }
            else if (callBackData.startsWith("YEAR_")) {
                String[] data = callBackData.split("_");
                int year = Integer.parseInt(data[1]);
                executedMessage(keyboardNew.getKeyboard(chatId,year));
            }
            else if (callBackData.startsWith("MONTH_")) {
                String[] data = callBackData.split("_");
                String month = data[1];
                int year = Integer.parseInt(data[3]);
                sendMessage(chatId, "Выводим значения за "  + month + " " +year + " года");
                sendPieChart(chatId, currentStatValues.getMeanQuest(chatId,year,month),
                        newChart.generateTitleString(month,year));
               // sendMessage(chatId, getStatCurrentPeriod.getStatFromCurrentDays(chatId,30));

            }
        }


    }


    public void setTextTimetoQuestions(String textTimetoQuestions) {
        this.textTimetoQuestions = textTimetoQuestions;
    }

    private void timeToQuestions(long chatID) {

        sendMessage(chatID, SEND_QUEST_ABOUT_TIME_TO_QUESTION);
    }

    private void registerUser(Message msg, Update update) {

        /**
         * провереям, если пользователь новый, то регистрируем его
         */

        if (userRepository.findById(msg.getChatId()).isEmpty()) {
            User user = new User();
            user.setChat_id(msg.getChatId());
            user.setFirstName(update.getMessage().getChat().getFirstName());
            user.setLastName(update.getMessage().getChat().getLastName());
            user.setUserName("@" + update.getMessage().getChat().getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
            log.info("Saved user: " + user);
        }
    }

    /**
     * Отправляем всем пользователям, кто ввел команду /start
     */

    private void startCommandReceived(long chatID, String name) {
        sendMessage(chatID, name + START_MESSAGE);
        log.info("Replied to user" + name);

        // указываем, откуда загружать приветственную картинку
        InputStream inputStream = getClass().getResourceAsStream("/Start.png");

        try {
            // создаем объект из потока с картинкой
            InputFile inputFile = new InputFile(inputStream, "Start.png");

            // создаем объект SendPhoto и добавляем параметры
            SendPhoto sendPhotoRequest = new SendPhoto();
            sendPhotoRequest.setChatId(chatID);
            sendPhotoRequest.setPhoto(inputFile);

            // отправляем фото по chatID пользователю
            execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        smartKeyboard(chatID, "Да", "Нет",YES_BUTTON,NO_BUTTON,
                TEXT_ABOVE_KEYBOARD_START);
    }

    /**
     * Через этот метод отправляем все сообщения пользователю
     */

    public void sendMessage(long chatID, String texToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(texToSend);
        executedMessage(message);
    }

    /**
     * Создаем и отправляем график по данным пользователя
     */


    public void sendPieChart(long chatID, Map<String, Double> chartToSend, String titleString) {

        String labels = newChart.createLabel(chartToSend);
        String data = newChart.createData(chartToSend);

        // создаем график с помощью библиотеки QuickChart, вставляя в нее уже посчитанные значения data
        // и готовые значения labels
        try {

            QuickChart chart = new QuickChart();
            chart.setWidth(1000);
            chart.setHeight(700);
            chart.setBackgroundColor("#FFFFFF");
            String config = newChart.generatePieChart(labels,data,titleString);
            chart.setConfig(config);

            // собираем график в картинку
            byte[] imageBytes = chart.toByteArray();

            // отправляем картинку пользователю
            SendPhoto sendPhotoRequest = new SendPhoto();
            sendPhotoRequest.setChatId(chatID);
            sendPhotoRequest.setPhoto(new InputFile(chart.getUrl()));
            execute(sendPhotoRequest);

        } catch (Exception e) {
            log.info("ERROR create chart " + e);
            e.printStackTrace();
        }
    }

    /**
     * Создаем график типа "Радар", с помощью которого сравнимаем разные временные периоды
     */

    public void sendRadarChart(long chatID, Map<String, Double> mapFirst, Map<String, Double> mapSecond,
                               String firstCompareName, String secondCompareName, String titleChart,
                               int currentDays) {

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

        // Подсчитываем временной диапазон
        Calendar endDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();

        //Увеличиваем currentDays в 2 раза и 1 день для корректности периода
        startDate.add(Calendar.DAY_OF_MONTH, (-currentDays * 2) +1);
        DateFormat dateFormatFirst = new SimpleDateFormat("d MMMM", new Locale("ru"));
        DateFormat dateFormatSecond = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));

        //Если год один в промежутке сравнения, то пишем один раз. Если года разные - выводим оба
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
            chart.setWidth(1000);
            chart.setHeight(700);
            chart.setBackgroundColor("white");
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
                    + "fontColor: '#141449',"
                    + "fontSize: 25,"
                    + "fontFamily: 'Georgia',"
                    + "fontStyle: 'normal',"
                    + "padding: 20"
                    + "},"
                    + "legend: {"
                    + "position: 'bottom',"
                    + "labels: {"
                    + "fontColor: '#141449',"
                    + "fontSize: 25,"
                    + "fontFamily: 'Georgia',"
                    + "fontStyle: 'normal',"
                    + "padding: 20"
                    + "}"
                    + "},"
                    + "scale: {"
                    + "gridLines: {"
                    + "color: '#9E9E9E'"
                    + "},"
                    + "pointLabels: {"
                    + "fontSize: 18,"
                    + "fontColor: '#9E9E9E'"
                    + "},"
                    + "ticks: {"
                    + "display: false,"
                    + "min: 0,"
                    + "max: 10,"
                    + "color: '#9E9E9E'"
                    + "}"
                    + "},"
                    + "elements: {"
                    + "line: {"
                    + "tension: 0.4"
                    + "}"
                    + "}"
                    + "}"
                    + "}");



            // Собираем график в картинку
            byte[] imageBytes = chart.toByteArray();

            // Отправляем картинку пользователю
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
            /////////////// НЕ ТРОГАТЬ///////////////

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
     * в заданное @Scheduled время.
     * Админ может вызвать эту команду из без @Scheduled с помощью команды /send
     */

    // @Scheduled(cron = "${interval-in-cron} ")
    private void SendAskUser() {
        var askUser = sendAllUserRepository.findAll();
        var users = userRepository.findAll();
        for (SendAllUser ask : askUser) {
            for (User user : users) {
                prepareAndSendMessage(user.getChat_id(), ask.getTextAskUser());
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
            log.info("Пользователь обновил время вопросов " + chatId);
        } catch (Exception e) {
            log.error("Error saving user to DB: " + chatId, e);
        }
    }

    /**
     * Проверка, есть ли в данную минуту пользователи, которым мы должны отправить вопросы
     */
    @Async
    @Scheduled(cron = "0 * * * * *")
    public void schedulerService() {
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            String cronExpression = user.getTimeToQuestions();
            Long chatId = user.getChat_id();
            try {
                CronSequenceGenerator generator = new CronSequenceGenerator(cronExpression);
                Date nextExecutionTime = generator.next(new Date());
                Date currentDate = new Date();
                if (nextExecutionTime != null && nextExecutionTime.getMinutes() == currentDate.getMinutes()
                        && nextExecutionTime.getHours() == currentDate.getHours()) {
                    log.info("Время cron соответствует текущему времени");
                        checkDateAndChatId(chatId);
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
            sendQuest(chat_id, quest);
        } else {
            if (!whoAnsweredTodayList.contains(chat_id)) {
                sendEndMessage(chat_id);
                whoAnsweredTodayList.add(chat_id);
            }
        }
    }

    public void sendQuest(Long chatId, Map<String, String> questMap) {
        String questValue = questMap.get("quest");
        String questStringValue = questMap.get("quest_string");

        try {
            execute(getSticker.addStiker(questValue, chatId));
        } catch (Exception e) {
            log.info("Error" + e);
        }
        sendMessage(chatId, questStringValue);
        LocalDate currentDate = LocalDate.now();
        executedMessage(keyboardNew.getKeyboard(chatId,currentDate,questValue));

    }


    /**
         * Сохраняем значение в БД
         **/

    private void saveAnswerToDb(long chatId, String question, int answer, LocalDate currentDate) {
        log.info("Сохраняем значение " + chatId + question + answer);
        String formattedTodayDate = currentDate.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String dateColumn = "date_" + formattedTodayDate;
        String sql = "UPDATE data_base_quest SET " + dateColumn + " = ? WHERE chat_id = ? AND quest = ?";
        jdbcTemplate.update(sql, answer, chatId, question);
    }



    /**
     * Если все вопросы на сегодня заданы, завершающее сообщение
     */

    private void sendEndMessage(long chatId) {
        sendMessage(chatId, THX_FOR_ASKING);
        /**
         * Если конец недели, то выводим статистику
         */
        checkEndPeriod(chatId);
    }
    private void checkEndPeriod (long chatId){
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            sendMessage(chatId,SUNDAY_TEXT);
            if (previousStatValues.getMeanQuest(chatId,7).isEmpty()) {
                sendPieChart(chatId, currentStatValues.getMeanQuest(chatId, 7),
                        newChart.generateTitleString(7));
                sendMessage(chatId, getStatCurrentPeriod.getStatFromCurrentDays(chatId, 7));
            }
            else
            {
                sendPieChart(chatId, currentStatValues.getMeanQuest(chatId, 7),
                        newChart.generateTitleString(7));
                sendMessage(chatId,endStatisticFromCurrentPeriod.messageEndStatisticFromCurrentPeriod(chatId,7));
            }
            log.info("Проверка на воскресенье");
        }
        if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)) {
            sendPieChart(chatId, currentStatValues.getMeanQuest(chatId, 30),
                    newChart.generateTitleString(30));
            sendMessage(chatId, getStatCurrentPeriod.getStatFromCurrentDays(chatId, 30));
            log.info("Проверка на конец месяца");
        }
    }


}



