package com.c.framework.elasticsearch.annotation.Index;


import com.c.framework.elasticsearch.constants.Constants;
import com.c.framework.elasticsearch.constants.EsFieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * es索引实体类字段注解 声明字段属性等（暂不支持符合类型）
 * @author W.C
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EsIndexMappingField {

    /**
     * 默认不进行分词 适用多数字段
     * @return
     */
    String type() default EsFieldType.KEYWORD;

    /**
     * 插入时禁止非空 不判断空串
     * @return
     */
    boolean notNull() default Constants.B_FALSE;

    /**
     * text类型时可以指定分词器
     * @return
     */
    String analyzer() default Constants.S_EMPTY;

//    /**
//     * 暂时没实现
//     * @return
//     */
//    boolean fielddata() default Constants.B_FALSE;
//
//    /**
//     * 暂时没实现
//     * @return
//     */
//    boolean dynamic() default Constants.B_FALSE;
//
//    /**
//     * 暂时没实现
//     * @return
//     */
//    boolean enabled() default Constants.B_FALSE;
}
