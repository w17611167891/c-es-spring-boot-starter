package com.c.framework.elasticsearch.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 * yml读取处理器
 * @author W.C
 */
public class YamlHandler {

    /**
     * classpath路径
     */
    private String path;

    public YamlHandler(String path) {
        this.path = path;
    }

    public String getKey(String keyPath) {
        Map<String, Object> obj = null;
        try {
            Yaml yaml = new Yaml();
            InputStream resourceAsStream = YamlHandler.class.getClassLoader().getResourceAsStream(path);
            obj = (Map) yaml.load(resourceAsStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] keys = keyPath.split("\\.");
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            Object o = obj.get(key);
            if (o instanceof Map) {
                obj = (Map<String, Object>) o;
            } else {
                if (i != keys.length - 1) throw new EsException("yml文件读取异常:"+keyPath);
                return (String) o;
            }
        }
        return null;
    }
}
