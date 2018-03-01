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
            offline();
        } finally {
            LogUtil.unbind();
        }
    }
    private void offline() {
        int offline = 0;

        // productService.yyy();
        if (LogUtil.ROOT_LOG.isInfoEnabled()) {
            LogUtil.ROOT_LOG.info("共下架 {} 个商品", offline);
        }
    }
}
