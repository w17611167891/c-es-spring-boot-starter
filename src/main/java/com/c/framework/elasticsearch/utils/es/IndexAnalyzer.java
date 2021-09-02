package com.c.framework.elasticsearch.utils.es;

import lombok.Data;

/**
 * 索引自定义分词设置
 * @author W.C
 */
@Data
public class IndexAnalyzer {

    private String type;

    private String pattern;

    private Boolean lowercase;
}
