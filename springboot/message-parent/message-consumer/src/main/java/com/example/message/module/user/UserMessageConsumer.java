package com.example.message.module.user;

import com.example.message.module.Constants;
import com.example.message.module.user.dto.UserMessageDto;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@Component
public class UserMessageConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserMessageConsumer.class);
    @Autowired
    KafkaListenerContainerFactory kafkaListenerContainerFactory;
    @Autowired
    private Gson gson;
    int i = 0;
    @KafkaListener(topics = Constants.USER_MESSAGE_TOPIC)
    public void listen(ConsumerRecord<?, ?> cr, Acknowledgment ac) throws Exception {

        UserMessageDto userMessageDto = (UserMessageDto) cr.value();
        LOGGER.info("收到消息:"+gson.toJson(userMessageDto));
        //确认收到消息
        ac.acknowledge();

    }

}
