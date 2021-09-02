package com.c.framework.elasticsearch.annotation.query;

import com.c.framework.elasticsearch.constants.Constants;
import com.c.framework.elasticsearch.constants.SortType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询排序条件
 * @author W.C
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EsQuerySort {

    /**
     * 排序字段名称 经纬度必须排序对应两个字段
     * @return
     */
    String name() default Constants.S_EMPTY;

    /**
     * 排序类型
     * @return
     */
    String sortType() default SortType.FIELD_SORT;

    /**
     * 排序类型规则 默认正序 倒序
     * @return
     */
    boolean isDesc() default Constants.B_FALSE;

    /**
     * 位置类型 经纬度标识
     * @return
     */
    String location() default Constants.S_EMPTY;
}
