package com.testSpringBoot.SpringDemoBot.model;

import com.testSpringBoot.SpringDemoBot.service.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;


import java.time.LocalDate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.sql.*;
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

}



