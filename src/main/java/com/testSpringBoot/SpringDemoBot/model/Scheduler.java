package com.testSpringBoot.SpringDemoBot.model;

import com.testSpringBoot.SpringDemoBot.service.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;

import java.util.Date;
import java.util.List;
@EnableAsync
@Slf4j
public class Scheduler {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TelegramBot telegramBot;

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
                    telegramBot.checkDateAndChatId(chat_id);
                }
            } catch (IllegalArgumentException e) {
                log.info("Error: " + e);
            }
        }
    }
}
