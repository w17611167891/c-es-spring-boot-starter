package com.c.framework.elasticsearch.config;

import com.c.framework.elasticsearch.core.EsQueryService;
import com.c.framework.elasticsearch.core.EsUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * spring自动装载配置
 * @author W.C
 */
@Configuration
@EnableConfigurationProperties({EsConfig.class})
@ConditionalOnClass(EsQueryService.class)
@ConditionalOnProperty(prefix = "c.elasticsearch", value = "enabled", matchIfMissing = true)
public class EsConfiguration {

    @Autowired
    private EsConfig esConfig;

    /**
     * 查询服务
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(EsQueryService.class)
    public EsQueryService esQueryService() {
        EsQueryService.init(esConfig);
        return new EsQueryService();
    }

    /**
     * 更新服务
     * @return
     */
    @Bean
    public EsUpdateService esUpdateService() {
        return new EsUpdateService();
    }

}
