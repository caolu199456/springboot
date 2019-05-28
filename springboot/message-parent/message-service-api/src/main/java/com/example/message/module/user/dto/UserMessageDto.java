package com.example.message.module.user.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserMessageDto implements Serializable{
    private String userId;
    private String userName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
