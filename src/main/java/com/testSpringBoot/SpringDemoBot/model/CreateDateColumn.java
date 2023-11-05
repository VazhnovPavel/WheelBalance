package com.testSpringBoot.SpringDemoBot.model;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
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
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy");
        String today ="\""+ "date_"+ formatter.format(new Date()) + "\"";
        String sql = "ALTER TABLE public.data_base_quest ADD " + today + "  INTEGER";

        try {
            session.createSQLQuery(sql).executeUpdate();
        }
        catch (Exception e){
            log.info("Проверка прошла успешно, столбец уже создан");
        }
        finally {
            session.getTransaction().commit();
            factory.close();
        }
    }

    public void addNewColumn(String dateAbsent) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
        Session session = factory.getCurrentSession();
        session.beginTransaction();
        String sql = "ALTER TABLE public.data_base_quest ADD " + dateAbsent + "  INTEGER";

        try {
            session.createSQLQuery(sql).executeUpdate();
        }
        catch (Exception e){
            log.info("Отсутствующий столбец не создался");
        }
        finally {
            session.getTransaction().commit();
            factory.close();
        }
    }
}
