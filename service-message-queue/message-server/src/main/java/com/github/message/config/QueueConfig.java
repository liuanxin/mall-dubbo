package com.github.message.config;

import com.github.message.constant.MessageConst;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;

@Configuration
public class QueueConfig {

    @Bean(MessageConst.SIMPLE_MQ_NAME)
    public Queue simpleQueue() {
        return new ActiveMQQueue(MessageConst.SIMPLE_MQ_NAME);
    }
}
