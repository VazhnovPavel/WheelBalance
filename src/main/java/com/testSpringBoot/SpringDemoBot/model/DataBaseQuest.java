package com.testSpringBoot.SpringDemoBot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;


@Getter
@Setter
@Entity
@Table(name = "data_base_quest")
public class DataBaseQuest implements Serializable {

    @Column(name = "date")
    private LocalDate date;
    @EmbeddedId
    private DataBaseQuestId id;

    public DataBaseQuest() {
    }

}


