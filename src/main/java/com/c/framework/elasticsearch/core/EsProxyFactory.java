package com.c.framework.elasticsearch.core;


import org.springframework.beans.factory.FactoryBean;

/**
 * 接口实例工厂，这里主要是用于ES抽象方法实现
 * @author W.C
 */
public class EsProxyFactory<T> implements FactoryBean<T> {

    private Class<T> interfaceType;

    public EsProxyFactory(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public T getObject() throws Exception {
        if(interfaceType.isInterface())
            //这里主要是创建接口对应的实例，便于注入到spring容器中
            return EsProxyService.getInstance(interfaceType);
        return interfaceType.newInstance();
    }

    @Override
    public Class<T> getObjectType() {
        return interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
