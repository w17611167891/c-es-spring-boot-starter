package com.c.framework.elasticsearch.annotation.update;

import com.c.framework.elasticsearch.constants.Constants;
import com.c.framework.elasticsearch.constants.UpdateType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;

/**
 * 更新操作方法注解 新增 删除 更新
 * @author W.C
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EsUpdate {

    /**
     * 名称 日志用
     * @return
     */
    String name() default Constants.S_EMPTY;

    /**
     * 类型
     * @return
     */
    String type() default UpdateType.UPDATE;
//
//    /**
//     * 查询索引名称 使用更新数据时填写 暂时不用
//     * @return
//     */
//    String indexName() default Constants.S_EMPTY;

    /**
     * 查询类型
     * @return
     */
    Class<?> updateType() default HashMap.class;

}
