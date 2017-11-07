package com.github.common.mvc;

import com.github.common.page.Page;
import com.github.common.util.RequestUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PageArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Page.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest request, WebDataBinderFactory factory) throws Exception {
        Page page = new Page(request.getParameter(Page.GLOBAL_PAGE), request.getParameter(Page.GLOBAL_LIMIT));
        page.setWasMobile(RequestUtils.isMobileRequest());
        return page;
    }
}
