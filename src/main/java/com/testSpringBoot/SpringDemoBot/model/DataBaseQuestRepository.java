package com.testSpringBoot.SpringDemoBot.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataBaseQuestRepository extends JpaRepository<DataBaseQuest, DataBaseQuestId> {

}

