package com.testSpringBoot.SpringDemoBot.model;

import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository <User,Long> {

}

