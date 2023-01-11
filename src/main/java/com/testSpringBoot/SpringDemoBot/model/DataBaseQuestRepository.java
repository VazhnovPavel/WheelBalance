package com.testSpringBoot.SpringDemoBot.model;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface DataBaseQuestRepository extends CrudRepository <DataBaseQuest,Long> {
    Optional<DataBaseQuest> findByDate(LocalDate date);
}
