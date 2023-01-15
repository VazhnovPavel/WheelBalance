package com.testSpringBoot.SpringDemoBot.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class SchedulerService {
   /* @Autowired
    private  JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository repository;

    @Scheduled(cron = "0 * * * * *")
    public void scheduleQuestions() {
        List<User> userList = repository.findAll();
        for (User user: userList) {
            String cronExpression = user.getTimeToQuestions();
            Long chat_id = user.getChatId();
            try {
                CronSequenceGenerator generator = new CronSequenceGenerator(cronExpression);
                Date nextExecutionTime = generator.next(new Date());
                Date currentDate = new Date();

                if(nextExecutionTime != null && nextExecutionTime.getMinutes() == currentDate.getMinutes()){
                    caseQuestion(chat_id);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Какая то ошибка" + e);
            }
        }
    }
    private void caseQuestion(Long chat_id) {
        System.out.println("Время cron соответствует текущему времени");
        CheckAndSendQuest checkAndSendQuest = new CheckAndSendQuest(jdbcTemplate);
        checkAndSendQuest.checkDateAndChatId(chat_id);
    }*/
}
