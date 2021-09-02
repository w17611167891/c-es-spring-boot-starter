package com.c.framework.elasticsearch.utils.es;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 文档更新参数集
 * @param <T>
 * @author W.C
 */
@Data
public class SourceUpdate<T> {

    /**
     * 索引名称
     */
    private String index;

    /**
     * 待更新内容
     */
    private T source;

    /**
     * 文档id
     */
    private String id;

    /**
     * 文档过滤条件
     */
    private List<SourceCondition> conditionList;

    public void addCondition(String key, String value, String relationType,String queryType) {
        if (conditionList == null) conditionList = new ArrayList<>();
        conditionList.add(new SourceCondition(key, value, relationType, queryType,null,null));
    }
}
