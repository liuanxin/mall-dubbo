package com.github.common;

import com.github.common.Const;
import com.github.common.util.GenerateEnumHandle;
import com.github.common.constant.CommonConst;
import org.junit.Test;

public class CommonGenerateEnumHandle {

    @Test
    public void generate() {
        GenerateEnumHandle.generateEnum(getClass(), Const.BASE_PACKAGE, CommonConst.MODULE_NAME);
    }
}
