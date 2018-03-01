package com.github.task;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.common.Const;
import com.github.common.util.LogUtil;
import com.github.order.service.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 订单相关的定时任务 */
@Component
public class OrderTask {

    @Reference(version = Const.DUBBO_VERSION)
    private OrderService orderService;

    /** 每分钟 --> 取消下单已经超过了 24 小时的订单 */
    @Scheduled(cron = "0 */1 * * * *")
    public void cancelOrder() {
        LogUtil.recordTime();
        try {
            cancel();
        } finally {
            LogUtil.unbind();
        }
    }
    private void cancel() {
        int cancelCount = 0;

        // cancelCount = orderService.yyy();
        if (LogUtil.ROOT_LOG.isInfoEnabled()) {
            LogUtil.ROOT_LOG.info("共取消 {} 笔订单", cancelCount);
        }
    }
}
