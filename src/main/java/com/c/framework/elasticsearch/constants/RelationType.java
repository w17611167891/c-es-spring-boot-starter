package com.c.framework.elasticsearch.constants;

/**
 * es条件关系类型
 *
 * @author W.C
 */
public interface RelationType {

    /**
     * 必须的
     */
    String MUST = "must";

    /**
     * 可能的 多个可能必须满足一个
     */
    String SHOULD = "should";
}
