package com.testSpringBoot.SpringDemoBot.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class SchedulerService {

    @Autowired
    private UserRepository repository;

    @Scheduled(cron = "0 * * * * *")
    public void scheduleQuestions() {
        System.out.println("Программа побывала тут");
        List<User> userList = repository.findAll();
        for (User user: userList) {
            String cronExpression = user.getTimeToQuestions();
            System.out.println("cronExpression = " + cronExpression);
            try {
                //далее магия от ИИ
                CronSequenceGenerator generator = new CronSequenceGenerator(cronExpression);
                Date nextExecutionTime = generator.next(new Date());
                System.out.println("nextExecutionTime = "+ nextExecutionTime);
                if(nextExecutionTime != null && nextExecutionTime.after(new Date())){
                    //caseQuestion(chatId);
                    System.out.println("Программа была тут");
                }
            } catch (IllegalArgumentException e) {
                // Handle invalid cron expression
                System.out.println("Какая то ошибка" + e);
            }
        }
    }
    private void caseQuestion(Long chatId) {
        // do something
    }
}
