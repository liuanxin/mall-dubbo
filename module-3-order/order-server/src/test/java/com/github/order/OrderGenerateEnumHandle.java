package com.github.order;

import com.github.common.Const;
import com.github.common.util.GenerateEnumHandle;
import com.github.order.constant.OrderConst;
import org.junit.Test;

public class OrderGenerateEnumHandle {

    @Test
    public void generate() {
        GenerateEnumHandle.generateEnum(getClass(), Const.BASE_PACKAGE, OrderConst.MODULE_NAME);
    }
}
