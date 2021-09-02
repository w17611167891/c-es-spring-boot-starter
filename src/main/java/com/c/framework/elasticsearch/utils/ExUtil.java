package com.c.framework.elasticsearch.utils;

import com.c.framework.elasticsearch.constants.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 异常函数工具类
 * @author W.C
 */
public class ExUtil {

    private static final Log LOGGER = LogFactory.getLog(ExUtil.class);

    /**
     * 异常统一处理
     *
     * @param action
     * @param functions
     * @return
     */
    public static <T> T exceptionHandler(String action, ExceptionHandler<T> functions) {
        try {
            T result = functions.handler();
            LOGGER.debug(action + Constants.S_SUCCESS + result);
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
            LOGGER.error(action + Constants.S_FAIL + e.getMessage(), e);
            throw new EsException(action + Constants.S_FAIL + e.getMessage());
        }
    }
}
