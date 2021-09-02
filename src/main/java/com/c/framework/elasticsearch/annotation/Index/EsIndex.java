package com.c.framework.elasticsearch.annotation.Index;


import com.c.framework.elasticsearch.constants.Constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * es索引实体类注解 声明索引名称
 * @author W.C
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EsIndex {

    String name() default Constants.S_EMPTY;

}
