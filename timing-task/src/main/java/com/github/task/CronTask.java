package com.github.task;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.common.Const;
import com.github.common.service.CommonService;
import com.github.common.util.LogUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 定时任务 --> 示例 */
@Component
public class CronTask {

    /** 当前定时任务的业务说明 */
    private static final String BUSINESS_DESC = "取消订单";
    /** 当前任务的表达式 */
    private static final String CRON = "0 */1 * * * *";

    @Reference(version = Const.DUBBO_VERSION, lazy = true, check = false, timeout = Const.DUBBO_TIMEOUT)
    private CommonService commonService;

    /** 取消下单已经超过了 24 小时的订单 */
    @Scheduled(cron = CRON)
    public void cancelOrder() {
        LogUtil.recordTime();
        try {
            handlerBusiness();
        } catch (Exception e) {
            if (LogUtil.ROOT_LOG.isErrorEnabled()) {
                LogUtil.ROOT_LOG.error(String.format("%s时异常", BUSINESS_DESC), e);
            }
        } finally {
            LogUtil.unbind();
        }
    }

    /** 操作具体的业务 */
    private void handlerBusiness() {
        // int cancelCount = commonService.xxx();
        // if (LogUtil.ROOT_LOG.isInfoEnabled()) {
        //     LogUtil.ROOT_LOG.info("{}时共操作了 {} 笔订单", BUSINESS_DESC, cancelCount);
        // }
    }
}
