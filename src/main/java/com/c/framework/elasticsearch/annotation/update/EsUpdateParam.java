package com.c.framework.elasticsearch.annotation.update;


import com.c.framework.elasticsearch.constants.Constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 更新方法参数注解 注解标识的才会被当做更新条件
 * 标注String类型 名称必填 为过滤条件
 * 标注引用类型 名称没用 在bean中通过 @EsQueryField 设置过滤条件
 * 引用型里的所有属性和实体中同名字段进行非空更新
 * @author W.C
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EsUpdateParam {

    /**
     * 参数名
     * @return
     */
    String name() default Constants.S_EMPTY;

}
