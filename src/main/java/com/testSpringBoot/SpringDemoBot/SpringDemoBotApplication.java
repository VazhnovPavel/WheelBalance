package com.testSpringBoot.SpringDemoBot;

import com.testSpringBoot.SpringDemoBot.model.DataBaseQuest;
import com.testSpringBoot.SpringDemoBot.model.DataBaseQuestId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackageClasses={DataBaseQuest.class, DataBaseQuestId.class})
public class SpringDemoBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDemoBotApplication.class, args);
	}
}
