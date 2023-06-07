package com.testSpringBoot.SpringDemoBot.model;


import lombok.extern.slf4j.Slf4j;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Slf4j
@Component
public class Onboarding {


    public boolean checkOnboarding(Long chat_id) {
        boolean isOnboardingToday = false;
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(User.class)
                .buildSessionFactory();
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            User user = session.get(User.class, chat_id);
            if (user != null) {
                LocalDate registeredDate = user.getRegisteredAt().toLocalDateTime().toLocalDate();
                LocalDate today = LocalDate.now();
                isOnboardingToday = registeredDate.equals(today);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            factory.close();
        }
        return isOnboardingToday;
    }

    public boolean checkTimeToQuestion(Long chat_id) {
        boolean hasTimeToQuestion = false;
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(User.class)
                .buildSessionFactory();
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            User user = session.get(User.class, chat_id);
            if (user != null && user.getTimeToQuestions() != null) {
                hasTimeToQuestion = true;
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            factory.close();
        }
        return hasTimeToQuestion;
    }

}



