package com.c.framework.elasticsearch.core;

import com.c.framework.elasticsearch.annotation.Index.EsIndex;
import com.c.framework.elasticsearch.annotation.Index.EsScriptField;
import com.c.framework.elasticsearch.annotation.query.*;
import com.c.framework.elasticsearch.config.EsConfig;
import com.c.framework.elasticsearch.constants.Constants;
import com.c.framework.elasticsearch.constants.QueryFieldType;
import com.c.framework.elasticsearch.constants.SortLocationType;
import com.c.framework.elasticsearch.constants.SortType;
import com.c.framework.elasticsearch.utils.*;
import com.c.framework.elasticsearch.utils.es.SourcePage;
import com.c.framework.elasticsearch.utils.es.SourceQuery;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 查询注解处理服务
 *
 * @author W.C
 */
@Aspect
@Component
public class EsQueryService {


    private static final Log LOGGER = LogFactory.getLog(EsQueryService.class);

    public static void init(EsConfig esConfig) {
        EsUtil.init(esConfig);
    }

    /**
     * 拦截查询注解标注的方法
     */
    @Pointcut("@annotation(com.c.framework.elasticsearch.annotation.query.EsQuery)")
    public void esQuery() {
    }

    /**
     * 环绕处理 不影响正常流程查询es
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("esQuery()")
    public Object handle(ProceedingJoinPoint point) {
        return ExUtil.exceptionHandler("不影响主流程查询:", () -> this.queryData(point));
    }

    /**
     * 查询数据
     *
     * @param point
     * @return
     */
    private Object queryData(ProceedingJoinPoint point) throws Throwable {
        Method method = ClassUtil.getMethod(point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), point.getArgs());
        SourceQuery query = EsQueryService.generateQuery(method);
        Object[] args = point.getArgs();
        int resultIndex = -1;
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null || args[i] instanceof SourcePage) resultIndex = i;
            if (args[i] == null) continue;
            EsQueryService.generateCondition(args[i], null, query);
        }
        if (resultIndex > -1) {
            args[resultIndex] = EsUtil.query(query);
            return point.proceed(args);
        } else if (method.getReturnType().equals(SourcePage.class)) {
            return EsUtil.query(query);
        }
        return point.proceed();
    }

    /**
     * 生成查询query
     *
     * @param method
     * @return
     */
    public static SourceQuery generateQuery(Method method) {
        EsQuery query = method.getAnnotation(EsQuery.class);
        Class<?> queryType = query.queryType();
        EsIndex index = queryType.getAnnotation(EsIndex.class);
        SourceQuery sourceQuery = new SourceQuery<>();
        if (index == null) throw new EsException("查询类型未指明索引");
        sourceQuery.setIndex(index.name());
        sourceQuery.setReturnType(query.queryType());
        EsQueryService.generateScriptField(sourceQuery);
        return sourceQuery;
    }

    /**
     * 生成查询脚本返回值
     *
     * @param sourceQuery
     */
    public static void generateScriptField(SourceQuery sourceQuery) {
        Field[] fields = sourceQuery.getReturnType().getDeclaredFields();
        for (Field field : fields) {
            EsScriptField scriptField = field.getAnnotation(EsScriptField.class);
            if (scriptField != null) {
                Map<String, Object[]> scriptParam = new HashMap<>();
                scriptParam.put(scriptField.byField(), new Object[]{null, null, Constants.S_EMPTY.equals(scriptField.name()) ? field.getName() : scriptField.name()});
                sourceQuery.setScriptParam(scriptParam);
            }
        }
    }


    /**
     * 递归注解和非注解属性
     *
     * @param param
     * @param paramClass
     * @param query
     */
    public static void generateCondition(Object param, Class paramClass, SourceQuery query) {
        paramClass = paramClass == null ? param.getClass() : paramClass;
        if (!paramClass.equals(Object.class)) {
            //递归父类
            EsQueryService.generateCondition(param, paramClass.getSuperclass(), query);
            Map<String, Map<String, Object>> genSortField = new HashMap<>();
            Field[] fields = paramClass.getDeclaredFields();
            for (Field field : fields) {
                Annotation[] annotations = field.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof EsQueryField) {
                        CommonUtil.setNotNullValue(param, field, (value) -> EsQueryService.generateQueryCondition(query, (EsQueryField) annotation, value, field.getName()));
                        if (((EsQueryField) annotation).isGroup()) {
                            String name = CommonUtil.getFieldName(((EsQueryField) annotation).name(), field.getName());
                            query.setGroupField(name);
                        }
                    } else if (annotation instanceof EsQuerySort) {
                        CommonUtil.setNotNullValue(param, field, (value) -> EsQueryService.generateQuerySort(query, (EsQuerySort) annotation, value, field.getName(), genSortField));
                    } else if (annotation instanceof EsQueryCurPage) {
                        CommonUtil.setNotNullValue(param, field, (value) -> query.setCurPage((Integer) value));
                    } else if (annotation instanceof EsQueryPageSize) {
                        CommonUtil.setNotNullValue(param, field, (value) -> query.setPageSize((Integer) value));
                    }
                }
            }
        }
    }

    /**
     * 生成查询条件
     *
     * @param query
     * @param queryField
     * @param value
     * @param fieldName
     */
    public static void generateQueryCondition(SourceQuery query, EsQueryField queryField, Object value, String fieldName) {
        String name = CommonUtil.getFieldName(queryField.name(), fieldName);
        if (QueryFieldType.QUERY.equals(queryField.type())) {
            query.addCondition(name
                    , EsQueryService.getValue(value), queryField.relationType()
                    , queryField.queryType(), null, queryField.rangeType());
        } else {
            query.addFilterCondition(name
                    , EsQueryService.getValue(value), queryField.relationType()
                    , queryField.queryType(), null, queryField.rangeType());
        }
    }

    /**
     * 日期类型转换
     *
     * @param val
     * @return
     */
    public static String getValue(Object val) {
        if (val instanceof Date) {
            return String.valueOf(((Date) val).getTime());
        }
        return val.toString();
    }

    /**
     * 生成排序条件
     *
     * @param query
     * @param querySort
     * @param value
     * @param fieldName
     * @param genSortField
     */
    private static void generateQuerySort(SourceQuery query, EsQuerySort querySort, Object value, String fieldName, Map<String, Map<String, Object>> genSortField) {
        String name = CommonUtil.getFieldName(querySort.name(), fieldName);
        if (SortType.GEN_SORT.equals(querySort.sortType())) {
            EsQueryService.generateGenSort(query, name, querySort, value, genSortField.compute(name, (k, v) -> v == null ? new HashMap<>() : v));
        } else {
            query.addSort(name, querySort.isDesc());
        }
    }

    /**
     * 生成距离排序
     *
     * @param query
     * @param name
     * @param querySort
     * @param value
     * @param map
     */
    private static void generateGenSort(SourceQuery query, String name, EsQuerySort querySort, Object value, Map<String, Object> map) {
        map.put(querySort.location(), value);
        if (map.size() == 2) {
            query.addSort(name, querySort.sortType(), querySort.isDesc()
                    , Double.valueOf(map.get(SortLocationType.GEN_LON).toString())
                    , Double.valueOf(map.get(SortLocationType.GEN_LAT).toString()));
            EsQueryService.generateGenScriptParam(query, name, map);
        }
    }

    /**
     * 生成距离计算参数
     *
     * @param query
     * @param name
     * @param map
     */
    private static void generateGenScriptParam(SourceQuery query, String name, Map<String, Object> map) {
        Map<String, Object[]> scriptParam = query.getScriptParam();
        Object[] objects = scriptParam.get(name);
        if (objects != null) {
            scriptParam.remove(name);
            scriptParam.put(objects[2].toString(), new Object[]{Double.valueOf(map.get(SortLocationType.GEN_LON).toString()), Double.valueOf(map.get(SortLocationType.GEN_LAT).toString())});
        }
    }

}
