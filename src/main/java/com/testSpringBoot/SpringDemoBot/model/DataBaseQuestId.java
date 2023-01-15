package com.testSpringBoot.SpringDemoBot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;


    @Embeddable
    @Getter
    @Setter
    public class DataBaseQuestId implements Serializable {
        public DataBaseQuestId() {
        }
        @Column(name = "chat_id", insertable = false, updatable = false)
        private long chatId;
        @Column(name = "quest", insertable = false, updatable = false)
        private String quest;
    }

