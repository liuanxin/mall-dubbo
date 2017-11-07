package com.github.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.common.Const;
import com.github.common.json.JsonResult;
import com.github.common.page.Page;
import com.github.product.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static com.github.common.json.JsonResult.success;

@Controller
public class ProductController {

    @Reference(version = Const.DUBBO_VERSION, lazy = true, check = false, timeout = Const.DUBBO_TIMEOUT)
    private ProductService productService;

    @GetMapping("/demo")
    public JsonResult demo(Page page) {
        return success("demo", productService.demo(page));
    }
}
