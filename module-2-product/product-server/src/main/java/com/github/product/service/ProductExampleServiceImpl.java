package com.github.product.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.common.Const;
import com.github.common.page.Page;
import com.github.common.page.PageInfo;

@Service(version = Const.DUBBO_VERSION, timeout = Const.DUBBO_TIMEOUT, filter = Const.DUBBO_FILTER)
public class ProductExampleServiceImpl implements ProductExampleService {

    // @Autowired
    // private ProductMapper productMapper;

    @Override
    public PageInfo demo(Page page) {
        // return Pages.returnList(productMapper.selectByExample(xx, Pages.param(page)));
        return null;
    }
}
