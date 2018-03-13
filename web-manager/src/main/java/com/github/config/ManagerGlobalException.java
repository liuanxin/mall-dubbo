package com.github.config;

import com.github.common.exception.ForbiddenException;
import com.github.common.exception.NotLoginException;
import com.github.common.exception.ServiceException;
import com.github.common.json.JsonResult;
import com.github.common.util.A;
import com.github.common.util.LogUtil;
import com.github.common.util.RequestUtils;
import com.github.common.util.U;
import com.github.util.ManagerSessionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 处理全局异常的控制类
 *
 * @see org.springframework.boot.autoconfigure.web.ErrorController
 * @see org.springframework.boot.autoconfigure.web.ErrorProperties
 * @see org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration
 */
@ControllerAdvice
public class ManagerGlobalException {

    private static final String SERVICE = ServiceException.class.getName();
    private static final String FORBIDDEN = ForbiddenException.class.getName();
    private static final String NOT_LOGIN = NotLoginException.class.getName();

    @Value("${online:false}")
    private boolean online;

    /** 业务异常. 非 rpc 调用抛出此异常时 */
    @ExceptionHandler(ServiceException.class)
    public JsonResult serviceException(ServiceException e) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug(e.getMessage());
        }
        return JsonResult.fail(e.getMessage());
    }
    /** 请求时没权限. 非 rpc 调用抛出此异常时 */
    @ExceptionHandler(ForbiddenException.class)
    public JsonResult forbidden(ForbiddenException e) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug(e.getMessage());
        }
        return JsonResult.fail(e.getMessage());
    }
    /** 请求时没登录. 非 rpc 调用抛出此异常时 */
    @ExceptionHandler(NotLoginException.class)
    public JsonResult noLogin(NotLoginException e) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug(e.getMessage());
        }
        return JsonResult.notLogin();
    }

    /** 请求没有相应的处理 */
    @ExceptionHandler(NoHandlerFoundException.class)
    public JsonResult forbidden(NoHandlerFoundException e) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.bind(RequestUtils.logContextInfo());
            LogUtil.ROOT_LOG.debug(e.getMessage(), e);
            LogUtil.unbind();
        }
        return JsonResult.fail("404");
    }
    /** 请求不支持相应的方法 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public JsonResult notSupported(HttpRequestMethodNotSupportedException e) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.bind(RequestUtils.logContextInfo()
                    .setId(String.valueOf(ManagerSessionUtil.getUserId()))
                    .setName(ManagerSessionUtil.getUserName()));
            LogUtil.ROOT_LOG.debug(e.getMessage());
            LogUtil.unbind();
        }

        String msg = U.EMPTY;
        if (!online) {
            msg = String.format(" 当前方式(%s), 支持方式(%s)", e.getMethod(), A.toStr(e.getSupportedMethods()));
        }
        return JsonResult.fail("不支持此种请求方式!" + msg);
    }
    /** 上传文件太大 */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public JsonResult notFound(MaxUploadSizeExceededException e) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug("文件太大: " + e.getMessage(), e);
        }
        // 右移 20 位相当于除以两次 1024, 正好表示从字节到 Mb
        return JsonResult.fail("上传文件太大! 请保持在 " + (e.getMaxUploadSize() >> 20) + "M 以内");
    }

    /** 未知的所有其他异常 */
    @ExceptionHandler(Throwable.class)
    public JsonResult exception(Throwable e) {
        String msg = e.getMessage();
        if (U.isNotBlank(msg)) {
            // x.xxException: abc\nx.xxException: abc\n
            msg = msg.split("\n")[0].trim();
            // 业务异常
            if (msg.startsWith(SERVICE)) {
                if (LogUtil.ROOT_LOG.isDebugEnabled()) {
                    LogUtil.ROOT_LOG.debug(e.getMessage(), e);
                }
                return JsonResult.fail(msg.substring(SERVICE.length() + 1));
            }
            // 请求时没权限
            else if (msg.startsWith(FORBIDDEN)) {
                if (LogUtil.ROOT_LOG.isDebugEnabled()) {
                    LogUtil.ROOT_LOG.debug(e.getMessage(), e);
                }
                return JsonResult.fail(msg.substring(FORBIDDEN.length() + 1));
            }
            // 请求时没登录
            else if (msg.startsWith(NOT_LOGIN)) {
                if (LogUtil.ROOT_LOG.isDebugEnabled()) {
                    LogUtil.ROOT_LOG.debug(e.getMessage(), e);
                }
                return JsonResult.notLogin();
            }
        }

        if (LogUtil.ROOT_LOG.isErrorEnabled()) {
            LogUtil.ROOT_LOG.error("有错误: " + e.getMessage(), e);
        }
        if (online) {
            msg = "请求时出现错误, 我们将会尽快处理";
        } else if (e instanceof NullPointerException && U.isBlank(msg)) {
            msg = "空指针异常, 联系后台查看日志进行处理";
        }
        return JsonResult.fail(msg);
    }
}
