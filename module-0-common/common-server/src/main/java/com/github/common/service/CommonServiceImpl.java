package com.github.common.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.common.Const;
import com.github.common.util.LogUtil;

/**
 * <p>类上的注解相当于下面的配置</p>
 *
 * &lt;dubbo:service exported="false" unexported="false"
 *     interface="com.github.common.service.CommonService"
 *     listener="" version="1.0" filter="" timeout="5000"
 *     id="com.github.common.service.CommonService" /&gt;
 */
@Service(version = Const.DUBBO_VERSION, timeout = Const.DUBBO_TIMEOUT, filter = Const.DUBBO_FILTER)
public class CommonServiceImpl implements CommonService {

    @Override
    public String example(String name) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug("param: {}", name);
        }
        return "hello " + name;
    }
}
