package com.c.framework.elasticsearch.utils.es;

import lombok.Data;

import java.util.Map;

/**
 * 索引自定义分词集合
 * @author W.C
 */
@Data
public class IndexAnalysis {

    private Map<String,IndexAnalyzer> analyzer;
}
