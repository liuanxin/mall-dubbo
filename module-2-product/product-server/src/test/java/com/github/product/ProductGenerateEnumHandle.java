package com.github.product;

import com.github.common.Const;
import com.github.common.util.GenerateEnumHandle;
import com.github.product.constant.ProductConst;
import org.junit.Test;

public class ProductGenerateEnumHandle {

    @Test
    public void generate() {
        GenerateEnumHandle.generateEnum(getClass(), Const.BASE_PACKAGE, ProductConst.MODULE_NAME);
    }
}
