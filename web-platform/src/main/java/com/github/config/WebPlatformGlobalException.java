package com.github.config;

import com.github.common.exception.ForbiddenException;
import com.github.common.exception.NotLoginException;
import com.github.common.exception.ServiceException;
import com.github.common.util.A;
import com.github.common.util.LogUtil;
import com.github.common.util.RequestUtils;
import com.github.common.util.U;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.common.json.JsonResult.fail;
import static com.github.common.json.JsonResult.notLogin;

/**
 * 处理全局异常的控制类
 *
 * @see org.springframework.boot.autoconfigure.web.ErrorController
 * @see org.springframework.boot.autoconfigure.web.ErrorProperties
 * @see org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration
 */
@ControllerAdvice
public class WebPlatformGlobalException {

    /** dubbo 返回的异常 */
    private static final Pattern DUBBO_EXCEPTION_REGEX = Pattern.compile("(([^\\n]*)\n){2}");
    private static final String SERVICE = ServiceException.class.getName();
    private static final String FORBIDDEN = ForbiddenException.class.getName();
    private static final String NOTLOGIN = NotLoginException.class.getName();

    @Value("${online:false}")
    private boolean online;

    /** 请求没有相应的处理 */
    @ExceptionHandler(NoHandlerFoundException.class)
    public void forbidden(NoHandlerFoundException e, HttpServletResponse response) throws IOException {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug(e.getMessage(), e);
        }
        RequestUtils.toJson(fail("404"), response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public void notSupported(HttpRequestMethodNotSupportedException e,
                             HttpServletResponse response) throws IOException {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug(e.getMessage());
        }

        String msg = U.EMPTY;
        if (!online) {
            msg = String.format(" 当前方式(%s), 支持方式(%s)", e.getMethod(), A.toStr(e.getSupportedMethods()));
        }
        RequestUtils.toJson(fail("不支持此种请求方式!" + msg), response);
    }

    /** 上传文件太大 */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public void notFound(MaxUploadSizeExceededException e, HttpServletResponse response) throws IOException {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug("文件太大: " + e.getMessage(), e);
        }
        // 右移 20 位相当于除以两次 1024, 正好表示从字节到 Mb
        RequestUtils.toJson(fail("上传文件太大! 请保持在 " + (e.getMaxUploadSize() >> 20) + "M 以内"), response);
    }

    /** 未知的所有其他异常 */
    @ExceptionHandler(Throwable.class)
    public void exception(Throwable e, HttpServletResponse response) throws IOException {
        // x.xxException: abc\nx.xxException: abc\n
        String msg = e.getMessage();
        Matcher m = DUBBO_EXCEPTION_REGEX.matcher(msg);
        if (m.matches()) {
            msg = m.group(2);
        }
        // 业务异常
        if (msg.startsWith(SERVICE)) {
            if (LogUtil.ROOT_LOG.isDebugEnabled()) {
                LogUtil.ROOT_LOG.debug(e.getMessage(), e);
            }
            RequestUtils.toJson(fail(msg.substring(SERVICE.length() + 1)), response);
        }
        // 请求时没权限
        else if (msg.startsWith(FORBIDDEN)) {
            if (LogUtil.ROOT_LOG.isDebugEnabled()) {
                LogUtil.ROOT_LOG.debug(e.getMessage(), e);
            }
            RequestUtils.toJson(fail(msg.substring(FORBIDDEN.length() + 1)), response);
        }
        // 请求时没登录
        else if (msg.startsWith(NOTLOGIN)) {
            if (LogUtil.ROOT_LOG.isDebugEnabled()) {
                LogUtil.ROOT_LOG.debug(e.getMessage(), e);
            }
            RequestUtils.toJson(notLogin(), response);
        }
        else {
            if (LogUtil.ROOT_LOG.isErrorEnabled()) {
                LogUtil.ROOT_LOG.error("有错误: " + e.getMessage(), e);
            }
            RequestUtils.toJson(fail(online ? "请求时出现错误, 我们将会尽快处理." : e.getMessage()), response);
        }
    }
}
