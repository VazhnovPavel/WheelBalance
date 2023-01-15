package com.testSpringBoot.SpringDemoBot.model;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Repository
@Slf4j
public class CheckAndSendQuest {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CheckAndSendQuest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void checkDateAndChatId(Long chat_id) {
        log.info("Executing query to fetch quest");
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String sql = "SELECT quest FROM data_base_quest WHERE chat_id = ? AND date_" + formattedDate + " IS NULL";

        List<String> quests = null;
        try {
            quests = jdbcTemplate.query(sql, new Object[]{chat_id}, (rs, rowNum) -> rs.getString("quest"));
        } catch (Exception e) {
            log.error("Error while executing query", e);
            System.out.println("Error while executing query" + e);
        }
        log.info("quest = " + quests);
        sendQuest(chat_id, quests);
    }


        private void sendQuest(Long chatId, List quests) {
        System.out.println("Пользователь " + chatId + " Получил вопрос " + quests);

    }
}


