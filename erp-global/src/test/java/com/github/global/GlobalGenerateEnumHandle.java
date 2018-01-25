package com.github.global;

import com.github.common.Const;
import com.github.common.util.GenerateEnumHandle;
import com.github.global.constant.GlobalConst;
import org.junit.Test;

public class GlobalGenerateEnumHandle {

    @Test
    public void generate() {
        GenerateEnumHandle.generateEnum(getClass(), Const.BASE_PACKAGE, GlobalConst.MODULE_NAME);
    }
}
