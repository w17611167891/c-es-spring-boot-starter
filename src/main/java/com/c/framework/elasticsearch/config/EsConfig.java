package com.c.framework.elasticsearch.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring配置读取类
 * @author W.C
 */
@ConfigurationProperties(prefix = "c.elasticsearch")
@Data
public class EsConfig {

    /**
     * 请求路径列表 ,分割
     */
    private String hosts = "http://10.151.31.193:9200";

    /**
     * dao接口存放位置
     */
    private String daoPath = "";

    /**
     * 最大链接数
     */
    private int maxConnTotal = 100;

    /**
     * 最大路由链接数
     */
    private int maxConnPerRoute = 100;

    /**
     * 总连接超时时间
     */
    private int connectTimeout = 1000;

    /**
     * socket超时时间
     */
    private int socketTimeout = 3*1000;

    /**
     * 请求连接超时时间
     */
    private int connectionRequestTimeout = 500;

}
