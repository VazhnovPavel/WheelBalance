package com.testSpringBoot.SpringDemoBot.service;

import com.testSpringBoot.SpringDemoBot.config.BotConfig;
import com.testSpringBoot.SpringDemoBot.model.AskUser;
import com.testSpringBoot.SpringDemoBot.model.AskUserRepository;
import com.testSpringBoot.SpringDemoBot.model.User;
import com.testSpringBoot.SpringDemoBot.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.xml.stream.events.Comment;
import java.security.Key;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

    @Slf4j
    @Component
    public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AskUserRepository askUserRepository;
    final BotConfig config;
    static final String START_MESSAGE = " Привет! Я помогу тебе отслеживать твое состояние во всех основных аспектах " +
            "жизни (или в каких пожелаешь).\n\n Я буду ежедневно задавать тебе простые вопросы об аспектах твоей жизни, " +
            "а тебе нужно будет ответить по десятибалльной шкале, насколько ты удовлетворен на данный момент.\n\n " +
            "А в конце недели/месяца/года мы с тобой будем подводить итоги, как идут у нас успехи. \n\n" +
            "Попробуем? ";
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
            "/month - показать статистику за месяц " ;


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
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(),null));
        }
        catch (TelegramApiException e) {
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

        if (update.hasMessage() && update.getMessage().hasText()){              // если нам прислали текст, то...
            String messageText = update.getMessage().getText() ;
            long chatID = update.getMessage().getChatId();
            switch (messageText.toLowerCase()) {
                case "/start":
                    // передаем Имя пользователя
                    registerUser(update.getMessage());
                    startCommandReceived(chatID,update.getMessage().getChat().getFirstName());
                    break;
                case  "/help":
                    sendMessage(chatID,HELP_TEXT);
                    break;
                case "да":
                    sendMessage(chatID,"В какое время вам было бы удобно получать вопросы? Напишите в " +
                            "формате ЧЧ:ММ по Москве");
                    break;
                default: sendMessage(chatID,"Я не знаю, как работать с этой командой");
            }
        }
    }

    private void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {         //Если новый user id, то...
            var chatId = msg.getChatId();
            var chat = msg.getChat();
            User user = new User();    // создаем юзера
            user.setChatId(chatId);
            user.setFirstName(user.getFirstName());       // берем данные о юзере
            user.setLastName(user.getLastName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));  // время регистрации
            userRepository.save(user);
            log.info("User saved" + user);
        }
    }

    private void startCommandReceived(long chatID, String name){            //метод обработки сообщения
        //String answer = "Привет "+ name+" ,как делы?";
        sendMessage(chatID,name + START_MESSAGE);
        log.info("Replied to user" + name );
    }
    //метод отправки сообщения пользоваелю
    public void sendMessage(long chatID,String texToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.setText(texToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();   //  создем клавиатуру
        List<KeyboardRow> keyboardRows = new ArrayList<>(); // создаем лист для вариантов ответа

        KeyboardRow row = new KeyboardRow(); // создаем ряд кнопок
        row.add("Да");
        row.add("Пока нет");
        keyboardRows.add(row);


//        row = new KeyboardRow(); // еще один ряд кнопок
//        row.add("Добавить свой раздел");
//        row.add("Удалить раздел");
//        row.add("Изменить время вопросов");
//        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows); //добавляем в клавиатуру наши ряды
        sendMessage.setReplyMarkup(keyboardMarkup); //привязываем к сообщению клавиатуру

        try {
            execute(sendMessage);
        }
        catch(TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }
   // @Scheduled   (cron ="${cron.scheduler}")                       //чтобы запускался автоматически
    private void SendAskUser(){
        var askUser = askUserRepository.findAll(); // все записи, которые есть в таблице
        var users = userRepository.findAll();
        for (AskUser ask: askUser){
            for (User user : users){
              //  prepareAndSendMessage(user.getChatId(),ask.getAd());
            }
        }

    }
}
