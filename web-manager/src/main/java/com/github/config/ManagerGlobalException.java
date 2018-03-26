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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private static final HttpStatus FAIL = HttpStatus.INTERNAL_SERVER_ERROR;
    private static final HttpStatus NEED_LOGIN = HttpStatus.UNAUTHORIZED;
    private static final HttpStatus NEED_PERMISSION = HttpStatus.FORBIDDEN;

    @Value("${online:false}")
    private boolean online;

    /** 业务异常. 非 rpc 调用抛出此异常时 */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<JsonResult> service(ServiceException e) {
        String msg = e.getMessage();
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug(msg);
        }
        return new ResponseEntity<>(JsonResult.fail(msg), FAIL);
    }

    /** 未登录 */
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<JsonResult> notLogin(NotLoginException e) {
        String msg = e.getMessage();
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug(msg);
        }
        return new ResponseEntity<>(JsonResult.notLogin(msg), NEED_LOGIN);
    }

    /** 无权限 */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<JsonResult> forbidden(ForbiddenException e) {
        String msg = e.getMessage();
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug(msg);
        }
        return new ResponseEntity<>(JsonResult.notPermission(msg), NEED_PERMISSION);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<JsonResult> noHandler(NoHandlerFoundException e) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.bind(RequestUtils.logContextInfo()
                    .setId(String.valueOf(ManagerSessionUtil.getUserId()))
                    .setName(ManagerSessionUtil.getUserName()));
            LogUtil.ROOT_LOG.debug(e.getMessage(), e);
            LogUtil.unbind();
        }
        return new ResponseEntity<>(JsonResult.notFound(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<JsonResult> notSupported(HttpRequestMethodNotSupportedException e) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.bind(RequestUtils.logContextInfo()
                    .setId(String.valueOf(ManagerSessionUtil.getUserId()))
                    .setName(ManagerSessionUtil.getUserName()));
            LogUtil.ROOT_LOG.debug(e.getMessage(), e);
            LogUtil.unbind();
        }

        String msg = U.EMPTY;
        if (!online) {
            msg = String.format(" 当前方式(%s), 支持方式(%s)", e.getMethod(), A.toStr(e.getSupportedMethods()));
        }
        return new ResponseEntity<>(JsonResult.fail("不支持此种请求方式!" + msg), FAIL);
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<JsonResult> uploadSizeExceeded(MaxUploadSizeExceededException e) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug("文件太大: " + e.getMessage(), e);
        }
        // 右移 20 位相当于除以两次 1024, 正好表示从字节到 Mb
        JsonResult<Object> result = JsonResult.fail("上传文件太大! 请保持在 " + (e.getMaxUploadSize() >> 20) + "M 以内");
        return new ResponseEntity<>(result, FAIL);
    }

    /** 未知的所有其他异常 */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<JsonResult> exception(Throwable e) {
        String msg = e.getMessage();
        if (U.isNotBlank(msg)) {
            // x.xxException: abc\nx.xxException: abc\n
            msg = msg.split("\n")[0].trim();
            if (msg.startsWith(NOT_LOGIN)) {
                // 没登录
                if (LogUtil.ROOT_LOG.isDebugEnabled()) {
                    LogUtil.ROOT_LOG.debug(msg, e);
                }
                JsonResult<Object> result = JsonResult.notLogin(msg.substring(NOT_LOGIN.length() + 1));
                return new ResponseEntity<>(result, NEED_LOGIN);
            }
            else if (msg.startsWith(FORBIDDEN)) {
                // 没权限
                if (LogUtil.ROOT_LOG.isDebugEnabled()) {
                    LogUtil.ROOT_LOG.debug(msg, e);
                }
                JsonResult<Object> result = JsonResult.notPermission(msg.substring(FORBIDDEN.length() + 1));
                return new ResponseEntity<>(result, NEED_PERMISSION);
            }
            else if (msg.startsWith(SERVICE)) {
                // 业务异常
                if (LogUtil.ROOT_LOG.isDebugEnabled()) {
                    LogUtil.ROOT_LOG.debug(msg, e);
                }
                JsonResult<Object> result = JsonResult.fail(msg.substring(SERVICE.length() + 1));
                return new ResponseEntity<>(result, FAIL);
            }
        }

        if (online) {
            msg = "请求出现错误, 我们将会尽快处理";
        } else if (e instanceof NullPointerException) {
            msg = "空指针异常, 联系后台查看日志进行处理";
        }
        if (LogUtil.ROOT_LOG.isErrorEnabled()) {
            LogUtil.ROOT_LOG.error("有错误", e);
        }
        return new ResponseEntity<>(JsonResult.fail(msg), FAIL);
    }
}
