package com.github.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.common.Const;
import com.github.common.json.JsonResult;
import com.github.common.page.Page;
import com.github.liuanxin.api.annotation.ApiGroup;
import com.github.product.constant.ProductConst;
import com.github.product.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.github.common.json.JsonResult.success;

@ApiGroup({ ProductConst.MODULE_INFO })
@RestController
public class ProductController {

    @Reference(version = Const.DUBBO_VERSION, lazy = true, check = false, timeout = Const.DUBBO_TIMEOUT)
    private ProductService productService;

    @GetMapping("/demo")
    public JsonResult demo(Page page) {
        return success("demo", productService.demo(page));
    }
}
