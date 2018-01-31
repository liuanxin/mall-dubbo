package com.github.config;

import com.github.common.AppVersion;
import com.github.common.RenderViewResolver;
import com.github.global.model.Develop;
import com.github.liuanxin.api.annotation.EnableApiInfo;
import com.github.liuanxin.api.model.DocumentCopyright;
import com.github.util.WebDataCollectUtil;
import com.github.util.WebSessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 项目中需要额外加载的类 */
@Configuration
@EnableApiInfo
public class WebPlatformBeanInit {

    @Value("${online:false}")
    private boolean online;

    /** freemarker 的默认配置 */
    @Autowired
    private FreeMarkerProperties properties;

    /**
     * 覆盖默认的 viewResolver<br>
     *
     * @see org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration.FreeMarkerWebConfiguration
     */
    @Bean(name = "freeMarkerViewResolver")
    public RenderViewResolver viewResolver() {
        RenderViewResolver resolver = new RenderViewResolver();
        resolver.putVariable(online).putClass(WebSessionUtil.class).putEnum(WebDataCollectUtil.ENUM_CLASS);
        properties.applyToViewResolver(resolver);
        return resolver;
    }

    @Bean
    public DocumentCopyright urlCopyright() {
        return new DocumentCopyright()
                .setTitle(Develop.TITLE)
                .setContact(Develop.CONTACT)
                .setTeam(Develop.TEAM)
                .setVersion(AppVersion.currentVersion())
                //.setCommentInReturnExample(false)
                .setOnline(online);
    }
}
