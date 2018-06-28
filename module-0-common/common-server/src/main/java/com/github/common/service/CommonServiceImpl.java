package com.github.common.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.common.Const;
import com.github.common.util.LogUtil;

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
