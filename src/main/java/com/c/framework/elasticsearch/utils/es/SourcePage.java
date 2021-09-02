package com.c.framework.elasticsearch.utils.es;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询文档结果页
 * @param <T>
 * @author W.C
 */
@Data
@ToString
public class SourcePage<T> {

    /**
     * 查询结果总数
     */
    private Long total = 0L;

    /**
     * 当前页数据
     */
    private List<T> resultList = new ArrayList<>();
}
