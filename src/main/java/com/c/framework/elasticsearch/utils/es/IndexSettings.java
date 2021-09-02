package com.c.framework.elasticsearch.utils.es;

import lombok.Data;

import java.util.Map;

/**
 * 索引统一配置结构
 * @author W.C
 */
@Data
public class IndexSettings {

    /**
     * 自定义分词集
     */
    private IndexAnalysis analysis;
}
