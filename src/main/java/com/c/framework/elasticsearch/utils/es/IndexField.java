package com.c.framework.elasticsearch.utils.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 索引字段
 * @author W.C
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexField {

    /**
     * 字段类型
     */
    private String type;

    /**
     * 字段分词器 keyword类型不支持分词
     */
    private String analyzer;

//    private Boolean fielddata;
//
//    private Boolean dynamic;
//
//    private Boolean enabled;

    private Map<String, IndexField> properties;

    public IndexField(String type) {
        this.type = type;
    }

    public IndexField(String type,String analyzer) {
        this.type = type;
        this.analyzer = analyzer;
    }
}
