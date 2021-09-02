package com.c.framework.elasticsearch.constants;

/**
 * es查询类型
 *
 * @author W.C
 */
public interface QueryType {

    /**
     * 默认分词匹配 支持索引聚合查询
     */
    String MATH = "MATH";

    /**
     * 查询条件不分词 短语精确匹配 不支持聚合
     */
    String TERM = "TERM";

    /**
     * 范围查询
     */
    String RANGE = "RANGE";
}
