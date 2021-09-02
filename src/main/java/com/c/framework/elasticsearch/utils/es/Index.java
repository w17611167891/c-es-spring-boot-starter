package com.c.framework.elasticsearch.utils.es;

import lombok.Data;

import java.util.Map;

/**
 * 索引结构体
 * @author W.C
 */
@Data
public class Index {

    /**
     * 索引名称
     */
    private String index;

    /**
     * 映射配置
     */
    private IndexMapping mapping;

    /**
     * 索引统一配置
     */
    private IndexSettings settings;

}
