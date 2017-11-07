package com.github.product.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.common.Const;
import com.github.common.page.Page;
import com.github.common.page.PageInfo;

/**
 * <p>类上的注解相当于下面的配置</p>
 *
 * &lt;dubbo:service exported="false" unexported="false"
 *     interface="com.github.product.service.ProductService"
 *     listener="" version="1.0" filter="" timeout="5000"
 *     id="com.github.product.service.ProductService" /&gt;
 */
@Service(version = Const.DUBBO_VERSION, timeout = Const.DUBBO_TIMEOUT, filter = Const.DUBBO_FILTER)
public class ProductServiceImpl implements ProductService {

    @Override
    public PageInfo demo(Page page) {
        // return Pages.returnList(xxx.selectByExample(xx, Pages.param(page)));
        return null;
    }
}
