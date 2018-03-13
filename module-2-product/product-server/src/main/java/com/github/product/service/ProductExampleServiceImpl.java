package com.github.product.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.common.Const;
import com.github.common.json.JsonUtil;
import com.github.common.page.Page;
import com.github.common.page.PageInfo;
import com.github.common.util.LogUtil;

@Service(version = Const.DUBBO_VERSION, timeout = Const.DUBBO_TIMEOUT, filter = Const.DUBBO_FILTER)
public class ProductExampleServiceImpl implements ProductExampleService {

    // @Autowired
    // private ProductMapper productMapper;

    @Override
    public PageInfo demo(Page page) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug("test param: " + JsonUtil.toJson(page));
        }
        // return Pages.returnList(productMapper.selectByExample(xx, Pages.param(page)));
        return null;
    }
}
