package com.testSpringBoot.SpringDemoBot.model;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ScheduledMessageSender {       //РАЗРАБОТКА ЗАМОРОЖЕНА
/*
    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "0 * * * * *")
    public void sendScheduledMessages() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            String cron = user.getTimeToQuestions();
            scheduledTask(cron, () -> sendMessage(user));
        }
    }

    @Scheduled(cron = "#{@cron}")
    public void scheduledTask(String cron, Runnable task) {
        task.run();
    }

    private void sendMessage(User user) {
        // code to send "Hello World" message to the user goes here
    }*/
}
