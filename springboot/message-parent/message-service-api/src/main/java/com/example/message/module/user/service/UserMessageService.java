package com.example.message.module.user.service;


import com.example.message.module.user.dto.UserMessageDto;

public interface UserMessageService {

    void sendMessage(UserMessageDto userMessageDto);
}
