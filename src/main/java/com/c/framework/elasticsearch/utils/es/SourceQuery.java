package com.c.framework.elasticsearch.utils.es;

import com.c.framework.elasticsearch.constants.SortType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询文档参数集
 * @param <T>
 * @author W.C
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceQuery<T extends BaseSource> {

    /**
     * 索引名称
     */
    private String index;

    /**
     * 查询条件列表
     */
    private List<SourceCondition> conditionList;

//    /**
//     * 查询条件列表集
//     */
//    private List<List<SourceCondition>> conditionLists;

    /**
     * 过滤条件列表
     */
    private List<SourceCondition> filterConditionList;

    /**
     * 分组 折叠数据 目前只支持单字段分组
     */
    private String groupField;


//    private List<List<SourceCondition>> filterConditionLists;

    /**
     * 页码
      */
    private Integer curPage = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 查询实体类型
     */
    private Class<T> returnType;

//    private List<String> highlightFieldList;

//    private List<String> queryFields;

    /**
     * 排序条件列表
     */
    private List<SourceSort> sortList;

    /**
     * 脚本字段参数
     */
    private Map<String,Object[]> scriptParam;

    /**
     * 获取起始值
     * @return
     */
    public Integer getFrom() {
        return curPage == null || pageSize == null ? null : (curPage > 0 ? (curPage - 1) : 0) * pageSize;
    }

    /**
     * 添加查询条件
     * @param key
     * @param value
     * @param relationType
     * @param queryType
     */
    public void addCondition(String key, Object value, String relationType, String queryType) {
        addCondition(key, value, relationType, queryType, null,null);
    }

    /**
     * 添加查询条件
     * @param key
     * @param value
     * @param relationType
     * @param queryType
     * @param weight
     * @param rangeType
     */
    public void addCondition(String key, Object value, String relationType, String queryType, Integer weight,String rangeType) {
        if (conditionList == null) conditionList = new ArrayList<>();
        conditionList.add(new SourceCondition(key, value, relationType, queryType, weight,rangeType));
    }

    /**
     * 添加过滤条件
     * @param key
     * @param value
     * @param relationType
     * @param queryType
     */
    public void addFilterCondition(String key, Object value, String relationType, String queryType) {
        addFilterCondition(key, value, relationType, queryType, null,null);
    }

    /**
     * 添加过滤条件
     * @param key
     * @param value
     * @param relationType
     * @param queryType
     * @param weight
     * @param rangeType
     */
    public void addFilterCondition(String key, Object value, String relationType, String queryType, Integer weight,String rangeType) {
        if (filterConditionList == null) filterConditionList = new ArrayList<>();
        filterConditionList.add(new SourceCondition(key, value, relationType, queryType, weight,rangeType));
    }

    /**
     * 添加排序条件
     * @param fieldName
     * @param sortType
     * @param isDesc
     * @param lon
     * @param lat
     */
    public void addSort(String fieldName, String sortType, boolean isDesc, double lon, double lat) {
        if (sortList == null) sortList = new ArrayList<>();
        sortList.add(new SourceSort(fieldName, sortType, isDesc, lon, lat));
    }

    /**
     * 添加位置正序条件
     * @param fieldName
     * @param lon
     * @param lat
     */
    public void addSort(String fieldName, double lon, double lat) {
        addSort(fieldName, SortType.GEN_SORT, false, lon, lat);
    }

    /**
     * 添加字段排序条件
     * @param fieldName
     * @param isDesc
     */
    public void addSort(String fieldName, boolean isDesc) {
        addSort(fieldName, SortType.FIELD_SORT, isDesc, 0, 0);
    }

    /**
     * 添加字段正序条件
     * @param fieldName
     */
    public void addSort(String fieldName) {
        addSort(fieldName, SortType.FIELD_SORT, false, 0, 0);
    }
}
