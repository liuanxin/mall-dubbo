package com.github.task;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.common.Const;
import com.github.common.util.LogUtil;
import com.github.product.service.ProductService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 商品相关的定时任务 */
@Component
public class ProductTask {

    @Reference(version = Const.DUBBO_VERSION)
    private ProductService productService;

    /** 每小时 --> 预售商品自动下架 */
    @Scheduled(cron = "0 0 0/1 * * *")
    public void offlineProduct() {
        LogUtil.recordTime();

        try {
            // productService.yyy();
        } catch (Exception e) {
            if (LogUtil.ROOT_LOG.isErrorEnabled()) {
                LogUtil.ROOT_LOG.error("商品下架时异常", e);
            }
        }

        if (LogUtil.ROOT_LOG.isInfoEnabled()) {
            LogUtil.ROOT_LOG.info("商品下架操作完成");
        }
        LogUtil.unbind();
    }
}
