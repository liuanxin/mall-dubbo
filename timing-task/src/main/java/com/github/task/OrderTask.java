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

    /** 每分钟 --> 取消订单 */
    @Scheduled(cron = "0 1 * * * *")
    public void cancelOrder() {
        LogUtil.recordTime();

        int cancelCount = 0;
        try {
            // cancelCount = orderService.yyy();
        } catch (Exception e) {
            if (LogUtil.ROOT_LOG.isErrorEnabled()) {
                LogUtil.ROOT_LOG.error("取消订单时异常", e);
            }
        }

        if (LogUtil.ROOT_LOG.isInfoEnabled()) {
            LogUtil.ROOT_LOG.info("取消订单操作完成. 共取消 {} 笔订单", cancelCount);
        }
        LogUtil.unbind();
    }
}
