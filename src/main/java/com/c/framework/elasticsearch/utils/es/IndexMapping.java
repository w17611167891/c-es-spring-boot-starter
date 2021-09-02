package com.c.framework.elasticsearch.utils.es;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 索引字段集合
 * @author W.C
 */
@Data
public class IndexMapping {

    /**
     * 索引字段映射
     */
    private Map<String, IndexField> properties;


    public void addProperty(String name, IndexField field) {
        if (properties == null) properties = new HashMap<>();
        properties.put(name, field);
    }
}
