package com.c.framework.elasticsearch.utils;

/**
 * 框架运行异常
 * @author W.C
 */
public class EsException extends RuntimeException {

    public EsException(String err){
        super(err);
    }
}
