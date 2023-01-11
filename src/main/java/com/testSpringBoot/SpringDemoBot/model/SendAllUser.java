package com.testSpringBoot.SpringDemoBot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity( name = "ask_user_table")
public class SendAllUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)   // спринг сам генерирует id
    private long id;
    private String textAskUser ;     // текст, который мы отправляем пользователям
}
