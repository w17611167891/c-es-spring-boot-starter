package com.c.framework.elasticsearch.core;

import com.alibaba.fastjson.JSON;
import com.c.framework.elasticsearch.annotation.Index.EsIndex;
import com.c.framework.elasticsearch.annotation.query.EsQueryField;
import com.c.framework.elasticsearch.annotation.update.EsUpdate;
import com.c.framework.elasticsearch.annotation.update.EsUpdateParam;
import com.c.framework.elasticsearch.constants.QueryType;
import com.c.framework.elasticsearch.constants.RelationType;
import com.c.framework.elasticsearch.constants.UpdateType;
import com.c.framework.elasticsearch.utils.ClassUtil;
import com.c.framework.elasticsearch.utils.CommonUtil;
import com.c.framework.elasticsearch.utils.EsUtil;
import com.c.framework.elasticsearch.utils.ExUtil;
import com.c.framework.elasticsearch.utils.es.SourceUpdate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 更新注解处理服务
 *
 * @author W.C
 */
@Aspect
@Component
public class EsUpdateService {


    private static final Log LOGGER = LogFactory.getLog(EsUpdateService.class);

    /**
     * 拦截更新注解标注的方法
     */
    @Pointcut("@annotation(com.c.framework.elasticsearch.annotation.update.EsUpdate)")
    public void esUpdate() {
    }

    /**
     * 不影响主流程更新
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("esUpdate()")
    public Object handle(ProceedingJoinPoint point) throws Throwable {
        Object proceed = point.proceed();
        ExUtil.exceptionHandler("不影响主流程更新:", () -> this.updateSource(point));
        return proceed;
    }

    /**
     * 更新es数据
     *
     * @param point
     * @return
     * @throws Exception
     */
    public SourceUpdate updateSource(ProceedingJoinPoint point) {
        Object[] args = point.getArgs();
        Method method = ClassUtil.getMethod(point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), args);
        return EsUpdateService.updateSource(method, args);
    }

    public static SourceUpdate updateSource(Method method,Object[] args){
        EsUpdate update = method.getAnnotation(EsUpdate.class);
        SourceUpdate sourceUpdate = EsUpdateService.getIndexSource(update);
        EsUpdateService.generateSource(method.getParameterAnnotations(), args, sourceUpdate);
        LOGGER.info(update.name() + ":" + JSON.toJSONString(sourceUpdate));
        return EsUpdateService.updateSourceByType(update, sourceUpdate);
    }

    /**
     * 生成基础更新参数
     *
     * @param update
     * @return
     */
    public static SourceUpdate getIndexSource(EsUpdate update) {
        Class<?> updateType = update.updateType();
        EsIndex index = updateType.getAnnotation(EsIndex.class);
        SourceUpdate sourceUpdate = new SourceUpdate<>();
        if (index == null) return null;
        sourceUpdate.setIndex(index.name());
        sourceUpdate.setSource(ExUtil.exceptionHandler("生成更新实体实例", () -> updateType.newInstance()));
        return sourceUpdate;
    }

    /**
     * 根据参数注解获取更新条件和更新参数
     *
     * @param parameterAnnotations
     * @param args
     * @param sourceUpdate
     */
    public static void generateSource(Annotation[][] parameterAnnotations, Object[] args, SourceUpdate sourceUpdate) {
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof EsUpdateParam) {
                    if (args[i] == null) break;
                    if (args[i] instanceof String) {
                        sourceUpdate.addCondition(((EsUpdateParam) annotation).name(), args[i].toString(), RelationType.MUST, QueryType.TERM);
                    } else {
                        EsUpdateService.annotationHandle(args[i], null, sourceUpdate);
                    }
                }
            }
        }
    }

    /**
     * 根据更新类型执行
     *
     * @param update
     * @param sourceUpdate
     */
    public static SourceUpdate updateSourceByType(EsUpdate update, SourceUpdate sourceUpdate) {
        switch (update.type()) {
            case UpdateType.INSERT:
                EsUtil.addSource(sourceUpdate);
                break;
            case UpdateType.DELETE:
                EsUtil.deleteSourceByCondition(sourceUpdate);
                break;
            default:
                EsUtil.updateSourceByCondition(sourceUpdate);
        }
        return sourceUpdate;
    }


    /**
     * 递归注解和非注解属性
     *
     * @param param
     * @param paramClass
     * @param source
     */
    public static void annotationHandle(Object param, Class paramClass, SourceUpdate source) {
        paramClass = paramClass == null ? param.getClass() : paramClass;
        if (!paramClass.equals(Object.class)) {
            //递归父类
            EsUpdateService.annotationHandle(param, paramClass.getSuperclass(), source);
            for (Field field : paramClass.getDeclaredFields()) {
                for (Annotation annotation : field.getAnnotations()) {
                    if (annotation instanceof EsQueryField) {
                        CommonUtil.setNotNullValue(param, field, (value) -> source.addCondition(CommonUtil.getFieldName(((EsQueryField) annotation).name(), field.getName())
                                , value.toString(), ((EsQueryField) annotation).relationType()
                                , ((EsQueryField) annotation).queryType()));
                    }
                }
                ClassUtil.setNotNullTargetField(field, param, source.getSource());
            }
        }
    }

}
