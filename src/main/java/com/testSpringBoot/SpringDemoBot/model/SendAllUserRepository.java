package com.testSpringBoot.SpringDemoBot.model;

import org.springframework.data.repository.CrudRepository;

//интерфейс, через который мы будем общаться с нашей таблицей
    public interface SendAllUserRepository extends CrudRepository<SendAllUser,Long> {

    }


