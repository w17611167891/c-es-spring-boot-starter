package com.c.framework.elasticsearch.utils.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 文档排序条件
 * @author W.C
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceSort {

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String sortType;

    /**
     * 是否倒序
     */
    private boolean isDesc;

    /**
     * 经度
     */
    private double lon;

    /**
     * 纬度
     */
    private double lat;

}
