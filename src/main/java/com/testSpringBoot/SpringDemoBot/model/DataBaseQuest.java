package com.testSpringBoot.SpringDemoBot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@Entity( name = "data_base_quest")
@IdClass(DataBaseQuest.DataBaseQuestId.class)
public class DataBaseQuest {
    @Id
    @Column(name = "chat_id")
    private long chat_id;
    @Id
    @Column(name = "quest")
    private String quest;
    @Column(name = "date")
    private LocalDate date;

    public DataBaseQuest() {
    }

    public DataBaseQuest(long chat_id, String quest) {
        this.chat_id = chat_id;
        this.quest = quest;
    }

    public static class DataBaseQuestId implements Serializable {
        private long chat_id;
        private String quest;

        public DataBaseQuestId() {
        }
        public DataBaseQuestId(long chat_id, String quest) {
            this.chat_id = chat_id;
            this.quest = quest;

        }
    }

    @Override
    public String toString() {
        return "DataBaseQuest{" +
                "chat_id=" + chat_id +
                ", quest='" + quest + '\'' +
                ", date=" + date +
                '}';
    }
}