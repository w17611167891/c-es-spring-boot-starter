package com.c.framework.elasticsearch.utils;

import com.c.framework.elasticsearch.constants.Constants;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 反射工具类
 * @author W.C
 */
public class ClassUtil {


    /**
     * 反射获取bean属性
     *
     * @param param
     * @param field
     * @return
     */
    public static Object getValue(Object param, Field field) {
        String fieldName = field.getName();
        Class paramClass = param.getClass();
        String prefix = field.getType().equals(Boolean.class) ? Constants.S_IS : Constants.S_GET;
        Method method = ExUtil.exceptionHandler("获取属性方法", () -> paramClass.getMethod(prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)));
        if (method == null) return null;
        return ExUtil.exceptionHandler("获取属性方法", () -> method.invoke(param));
    }

    /**
     * 设置同名目标属性值
     *
     * @param field
     * @param source
     * @param target
     */
    public static void setNotNullTargetField(Field field, Object source, Object target) {
        String fieldName = field.getName();
        Class targetClass = target.getClass();
        Method targetMethod = ExUtil.exceptionHandler("获取目标属性设置方法", () -> targetClass.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1),field.getType()));
        if (targetMethod != null) {
            Object value = ClassUtil.getValue(source, field);
            if (value != null) {
                ExUtil.exceptionHandler("设置目标属性值", () -> targetMethod.invoke(target, value));
            }
        }
    }

    /**
     * 非空值覆盖
     *
     * @param source
     * @param target
     */
    public static void copyBeanForNotNullField(Object source, Object target) {
        Class<?> sourceClass = source.getClass();
        Field[] fields = sourceClass.getDeclaredFields();
        Arrays.stream(fields).forEach(field -> ClassUtil.setNotNullTargetField(field, source, target));
    }

    /**
     * 根据全类名和方法名获取方法对象
     * @param classFullName
     * @param methodName
     * @param params
     * @return
     */
    public static Method getMethod(String classFullName, String methodName, Object[] params) {
        Class<?> targetClass = ExUtil.exceptionHandler("全类名获取类对象",()->Class.forName(classFullName));
        Class[] paramTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++)
            paramTypes[i] = params[i] == null ? null : params[i].getClass();
        return ExUtil.exceptionHandler("根据名称获取方法",()->targetClass.getMethod(methodName, paramTypes));
    }
}
