package com.testSpringBoot.SpringDemoBot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity( name = "adsTable")
public class AskUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)   // спринг сам генерирует id
    private long id;
    private String ad;     // текст, который мы отправляем пользователям
}
