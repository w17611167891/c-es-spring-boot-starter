package com.c.framework.elasticsearch.utils.es;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 实体统一父类
 * @author W.C
 */
@Data
@ToString
public class BaseSource implements Serializable {

    private String id;
}
