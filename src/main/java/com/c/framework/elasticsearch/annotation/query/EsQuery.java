package com.c.framework.elasticsearch.annotation.query;

import com.alibaba.fastjson.JSONObject;
import com.c.framework.elasticsearch.constants.Constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;

/**
 * 查询方法注解
 * @author W.C
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EsQuery {

    /**
     * 查询名称 日志用
     * @return
     */
    String name() default Constants.S_EMPTY;

    /**
     * 查询索引名称 实体上有标明可以不填
     * @return
     */
    String indexName() default Constants.S_EMPTY;

    /**
     * 查询类型
     * @return
     */
    Class<?> queryType() default HashMap.class;

}
