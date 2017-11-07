package com.github.global.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/** 当前类上的方法, 在其他类调用时, 都会异步运行 */
@Async
@Component
public class AsyncService {

    public void sendSms(String phone, String sms) {
        // todo ...
    }
}
