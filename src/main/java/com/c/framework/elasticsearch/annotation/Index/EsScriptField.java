package com.c.framework.elasticsearch.annotation.Index;


import com.c.framework.elasticsearch.constants.Constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * es索引脚本字段注解 声明后查询时会根据脚本返回对应值 目前只支持经纬度算距离
 * @author W.C
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EsScriptField {

    String name() default Constants.S_EMPTY;

    String byField() default Constants.S_EMPTY;

    String script() default Constants.S_EMPTY;
}
