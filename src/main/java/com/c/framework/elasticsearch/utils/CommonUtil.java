package com.c.framework.elasticsearch.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * 通用工具类
 * @author W.C
 */
public class CommonUtil {

    /**
     * 名称非空覆盖字段名
     *
     * @param name
     * @param fieldName
     * @return
     */
    public static String getFieldName(String name, String fieldName) {
        return StringUtils.isEmpty(name) ? fieldName : name;
    }

    /**
     * 设置非空value
     *
     * @param param
     * @param field
     * @param consumer
     */
    public static void setNotNullValue(Object param, Field field, Consumer consumer) {
        Object value = ClassUtil.getValue(param, field);
        if (value == null) return;
        consumer.accept(value);
    }
}
