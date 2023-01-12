package com.testSpringBoot.SpringDemoBot.model;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
@Slf4j
@Component
public class CreateDateColumn {

    @Scheduled(cron = "0 */10 * * * *")
    public void addNewColumn() {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
        Session session = factory.getCurrentSession();
        session.beginTransaction();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String today = "\"" + formatter.format(new Date()) + "\"";
        String sql = "ALTER TABLE public.data_base_quest ADD " + today + "  VARCHAR(255)";

        try {
            session.createSQLQuery(sql).executeUpdate();
        }
        catch (Exception e){
            log.info("Проверка прошла успешно, столбец уже создан");
        }
        finally {
            session.getTransaction().commit();
        }
    }
}
