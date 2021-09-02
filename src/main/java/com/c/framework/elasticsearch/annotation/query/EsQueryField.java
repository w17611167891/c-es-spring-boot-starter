package com.c.framework.elasticsearch.annotation.query;

import com.c.framework.elasticsearch.constants.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询条件字段注解
 * @author W.C
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EsQueryField {

    /**
     * 条件字段名称 为空时使用字段名
     * @return
     */
    String name() default Constants.S_EMPTY;

    /**
     * 字段类型 查询条件 过滤条件
     * @return
     */
    String type() default QueryFieldType.FILTER;

    /**
     * 查询条件关系类型 must should
     * @return
     */
    String relationType() default RelationType.MUST;

    /**
     * 查询类型 term math range
     * @return
     */
    String queryType() default QueryType.TERM;

    /**
     * 范围类型
     * @return
     */
    String rangeType() default RangeType.GTE;

    /**
     * 是否根据该字段分组 目前只支持单字段分组 多个只取最后
     * @return
     */
    boolean isGroup() default Constants.B_FALSE;
}
