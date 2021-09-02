package com.c.framework.elasticsearch.core;


import com.c.framework.elasticsearch.annotation.query.EsQuery;
import com.c.framework.elasticsearch.annotation.update.EsUpdate;
import com.c.framework.elasticsearch.utils.EsUtil;
import com.c.framework.elasticsearch.utils.es.SourceQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * es接口代理实现
 * @author W.C
 */
public class EsProxyService {

    /**
     * java动态代理实现
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T getInstance(Class<T> cls) {
        MethodProxy invocationHandler = new MethodProxy();
        Object newProxyInstance = Proxy.newProxyInstance(
                cls.getClassLoader(),
                new Class[]{cls},
                invocationHandler);
        return (T) newProxyInstance;
    }

    /**
     * 通用代理类
     */
    public static class MethodProxy implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Object.class.equals(method.getDeclaringClass())) {
                try {
                    return method.invoke(this, args);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                //如果传进来的是一个接口（核心)
            } else {
                return run(method, args);
            }
            return null;
        }

        public Object run(Method method, Object[] args) {
            EsQuery esQuery = method.getAnnotation(EsQuery.class);
            if (esQuery != null) {
                SourceQuery query = EsQueryService.generateQuery(method);
                for (int i = 0; i < args.length; i++) {
                    if (args[i] == null) continue;
                    EsQueryService.generateCondition(args[i], null, query);
                    return EsUtil.query(query);
                }
            }
            EsUpdate esUpdate = method.getAnnotation(EsUpdate.class);
            if (esUpdate != null)
                EsUpdateService.updateSource(method, args);
            return null;
        }

    }
}
