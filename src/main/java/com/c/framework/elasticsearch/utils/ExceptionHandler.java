package com.c.framework.elasticsearch.utils;

/**
 * 异常处理函数接口
 * @param <T>
 * @author W.C
 */
@FunctionalInterface
public interface ExceptionHandler<T> {

    /**
     * 异常向上抛出处理
     * @return
     * @throws Throwable
     */
    T handler() throws Throwable;

}
