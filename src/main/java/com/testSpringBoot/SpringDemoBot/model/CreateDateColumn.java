package com.testSpringBoot.SpringDemoBot.model;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@Slf4j
public class CreateDateColumn {
    private final DataBaseQuestRepository dataBaseQuestRepository;

    public CreateDateColumn(DataBaseQuestRepository dataBaseQuestRepository) {
        this.dataBaseQuestRepository = dataBaseQuestRepository;
    }

    @Scheduled(cron = "0 * * * * *")
    public void scheduleTask() {
        createWithCurrentDate();
    }

    public void createWithCurrentDate() {
        DataBaseQuest dataBaseQuest = new DataBaseQuest();
        dataBaseQuest.setDate(LocalDate.now());
        LocalDate date = LocalDate.now();
        if(dataBaseQuestRepository.findByDate(date).isPresent()){
            log.info("Date already exists: {}", date);
            return;
        }
        try {
            dataBaseQuestRepository.save(dataBaseQuest);
        }
            catch  (Exception e) {
                log.error("Error saving Table to DB: " +  e);
                System.out.println("Ошибка добавления столбца Текущей даты в базу данных: " + e);
            }
    }
}
