package com.github.order.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.common.Const;

/**
 * <p>类上的注解相当于下面的配置</p>
 *
 * &lt;dubbo:service exported="false" unexported="false"
 *     interface="com.github.order.service.OrderService"
 *     listener="" version="1.0" filter="" timeout="5000"
 *     id="com.github.order.service.OrderService" /&gt;
 */
@Service(version = Const.DUBBO_VERSION, timeout = Const.DUBBO_TIMEOUT, filter = Const.DUBBO_FILTER)
public class OrderServiceImpl implements OrderService {

    @Override
    public String example(String name) {
        return "hello " + name;
    }
}
