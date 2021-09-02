package com.c.framework.elasticsearch.constants;

/**
 * es字段类型
 *
 * @author W.C
 */
public interface EsFieldType {

    /**
     * 支持分词
     */
    String TEXT = "text";

    /**
     * 不支持分词
     */
    String KEYWORD = "keyword";

    /**
     * 经纬度坐标
     */
    String GEO_POINT = "geo_point";

    /**
     * 日期类型
     */
    String DATE = "date";
}
