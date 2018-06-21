package com.github.message;

import com.github.common.Const;
import com.github.common.util.GenerateEnumHandler;
import com.github.message.constant.MessageConst;
import org.junit.Test;

public class MessageGenerateEnumHandler {

    @Test
    public void generate() {
        GenerateEnumHandler.generateEnum(getClass(), Const.BASE_PACKAGE, MessageConst.MODULE_NAME);
    }
}
