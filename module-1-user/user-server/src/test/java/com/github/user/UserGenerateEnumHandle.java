package com.github.user;

import com.github.common.Const;
import com.github.common.util.GenerateEnumHandle;
import com.github.user.constant.UserConst;
import org.junit.Test;

public class UserGenerateEnumHandle {

    @Test
    public void generate() {
        GenerateEnumHandle.generateEnum(getClass(), Const.BASE_PACKAGE, UserConst.MODULE_NAME);
    }
}
