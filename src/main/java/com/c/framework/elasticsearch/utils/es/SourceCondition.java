package com.c.framework.elasticsearch.utils.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档条件
 * @author W.C
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceCondition {

    /**
     * 字段名
     */
    private String key;

    /**
     * 字段值
     */
    private Object value;

    /**
     * 条件关系类型
     */
    private String relationType;

    /**
     * 查询类型
     */
    private String queryType;

    /**
     * 权重
     */
    private Integer weight;

    /**
     * 范围类型
     */
    private String rangeType;
}
