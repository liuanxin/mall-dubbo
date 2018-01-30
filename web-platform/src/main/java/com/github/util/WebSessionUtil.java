package com.github.util;

import com.github.common.exception.NotLoginException;
import com.github.common.util.LogUtil;
import com.github.common.util.RequestUtils;
import com.github.common.util.U;

/** 操作 session 都基于此, 其他地方不允许操作! 避免 session 被滥用 */
public class WebSessionUtil {

    /** 放在 session 里的图片验证码 key */
    private static final String CODE = WebSessionUtil.class.getName() + "-CODE";
    /** 放在 session 里的用户 的 key */
    private static final String USER = WebSessionUtil.class.getName() + "-USER";

    /** 验证图片验证码 */
    public static boolean checkImageCode(String code) {
        if (U.isBlank(code)) {
            return false;
        }

        Object securityCode = RequestUtils.getSession().getAttribute(CODE);
        return securityCode != null && code.equalsIgnoreCase(securityCode.toString());
    }
    /** 将图片验证码的值放入 session */
    public static void putImageCode(String code) {
        RequestUtils.getSession().setAttribute(CODE, code);
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug("put image code ({}) in session ({})", code, RequestUtils.getSession().getId());
        }
    }

    /** 用户在登录之后调用此方法, 主要就是将 用户信息、可访问的 url 等放入 session */
    @SuppressWarnings("unchecked")
    /* todo
    public static void toSession(Account account, List<AccountPermission> permissions) {
        RequestUtils.getSession().setAttribute(USER, WebSessionModel.assemblyData(account, permissions));
    }
    */

    /** 获取用户信息, 从 token 中获取, 没有则从 session 中获取 */
    public static WebSessionModel getSessionInfo() {
        WebSessionModel sessionModel =
                (WebSessionModel) RequestUtils.getSession().getAttribute(USER);
        return sessionModel == null ? WebSessionModel.defaultUser() : sessionModel;
    }

    /** 从 session 中获取用户 id */
    public static Long getUserId() {
        return getSessionInfo().getId();
    }

    /** 从 session 中获取用户名 */
    public static String getUserName() {
        return getSessionInfo().getUserName();
    }

    /** 是否是超级管理员, 是则返回 true */
    public static boolean isSuper() {
        return getSessionInfo().wasSuper();
    }
    /** 是否是超级管理员, 不是则返回 true */
    public static boolean isNotSuper() {
        return !isSuper();
    }

    /** 验证用户是否有登录, 如果有则返回 true */
    public static boolean isLogin() {
        return getSessionInfo().wasLogin();
    }
    /** 验证用户是否有登录, 如果没有则返回 true */
    public static boolean isNotLogin() {
        return !isLogin();
    }

    /** 验证登录, 未登录则抛出异常 */
    public static void checkLogin() {
        if (isNotLogin()) {
            throw new NotLoginException();
        }
    }

    /** 退出登录时调用. 清空 session */
    public static void signOut() {
        RequestUtils.getSession().invalidate();
    }
}
