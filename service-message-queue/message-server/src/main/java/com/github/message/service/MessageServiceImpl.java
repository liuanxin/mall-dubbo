package com.github.message.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.common.Const;
import com.github.message.constant.MessageConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Queue;

/**
 * <p>类上的注解相当于下面的配置</p>
 *
 * &lt;dubbo:service exported="false" unexported="false"
 *     interface="com.github.message.service.MessageService"
 *     listener="" version="1.0" filter="" timeout="5000"
 *     id="com.github.message.service.MessageService" /&gt;
 */
@Service(version = Const.DUBBO_VERSION, timeout = Const.DUBBO_TIMEOUT, filter = Const.DUBBO_FILTER)
public class MessageServiceImpl implements MessageService {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier(MessageConst.SIMPLE_MQ_NAME)
    private Queue simpleQueue;

    @Override
    public void submitSimple(String simpleInfo) {
        jmsTemplate.convertAndSend(simpleQueue, simpleInfo);
    }
}
