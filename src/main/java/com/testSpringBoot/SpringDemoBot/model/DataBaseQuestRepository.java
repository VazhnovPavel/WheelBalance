package com.testSpringBoot.SpringDemoBot.model;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DataBaseQuestRepository extends JpaRepository<DataBaseQuest, DataBaseQuestId> {

    List<DataBaseQuest> findByChat_idAndDateIsNull(long chatId);
}

