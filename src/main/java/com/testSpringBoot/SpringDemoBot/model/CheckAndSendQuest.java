package com.testSpringBoot.SpringDemoBot.model;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class CheckAndSendQuest {
    @Autowired
    private DataBaseQuestRepository dataBaseQuestRepository;

    public void checkDateAndChatId(Long chat_id) {
        try {
            List<DataBaseQuest> data = dataBaseQuestRepository.findByChat_idAndDateIsNull(chat_id);
            for (DataBaseQuest dbq : data) {
                sendQuest(dbq.getId().getChat_id(), dbq.getId().getQuest());
            }
        } catch (Exception e) {
            System.out.println("Error in code: " + e);
            e.printStackTrace();
        }
    }



    private void sendQuest(Long chatId, String quest) {
        System.out.println("Пользователь " + chatId + " Получил вопрос " + quest);
    }
}


