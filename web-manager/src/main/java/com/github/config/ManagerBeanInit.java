package com.github.config;

import com.github.common.RenderViewResolver;
import com.github.liuanxin.api.annotation.EnableApiInfo;
import com.github.util.ManagerDataCollectUtil;
import com.github.util.ManagerSessionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 项目中需要额外加载的类 */
@Configuration
@EnableApiInfo
public class ManagerBeanInit {

    @Value("${online:false}")
    private boolean online;

    /** freemarker 的默认配置 */
    private final FreeMarkerProperties properties;

    public ManagerBeanInit(FreeMarkerProperties properties) {
        this.properties = properties;
    }

    /**
     * 覆盖默认的 viewResolver<br>
     *
     * @see org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration.FreeMarkerWebConfiguration
     */
    @Bean(name = "freeMarkerViewResolver")
    public RenderViewResolver viewResolver() {
        RenderViewResolver resolver = new RenderViewResolver();
        resolver.putVariable(online)
                .putClass(ManagerSessionUtil.class)
                .putEnum(ManagerDataCollectUtil.VIEW_ENUM_ARRAY);
        properties.applyToViewResolver(resolver);
        return resolver;
    }
}
