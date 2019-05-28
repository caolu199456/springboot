package com.example.message.module.user.service.impl;

import com.example.message.module.Constants;
import com.example.message.module.user.dto.UserMessageDto;
import com.example.message.module.user.service.UserMessageService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

@com.alibaba.dubbo.config.annotation.Service
public class UserMessageServiceImpl implements UserMessageService {
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private Gson gson;
    @Override
    public void sendMessage(UserMessageDto userMessageDto) {

        kafkaTemplate.send(Constants.USER_MESSAGE_TOPIC, userMessageDto);

    }
}
