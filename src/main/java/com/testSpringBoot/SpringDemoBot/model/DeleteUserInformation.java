package com.testSpringBoot.SpringDemoBot.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DeleteUserInformation {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void deleteDataUser(Long Chatid){
        String delete_data_base_quest = "DELETE FROM data_base_quest WHERE chat_id = ?";
        jdbcTemplate.update(delete_data_base_quest, Chatid);
        String delete_all_user_data = "DELETE FROM all_user_data WHERE chat_id = ?";
        jdbcTemplate.update(delete_all_user_data, Chatid);
    }

}
