package com.github.product.service;

import com.github.common.page.PageParam;
import com.github.common.page.PageReturn;
import com.github.product.model.DemoModel;

public interface ProductExampleService {

    PageReturn<DemoModel> demo(PageParam page);
}
