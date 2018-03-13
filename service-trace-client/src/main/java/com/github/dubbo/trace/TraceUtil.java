package com.github.dubbo.trace;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;

final class TraceUtil {

    static String createSpanName(Invoker<?> invoker, Invocation invocation) {
        return invoker.getUrl().getParameter("interface") + ":"
                + invocation.getMethodName() + ":" + invoker.getUrl().getParameter("version")
                + "(" + invoker.getUrl().getHost() + ")";
    }
}
