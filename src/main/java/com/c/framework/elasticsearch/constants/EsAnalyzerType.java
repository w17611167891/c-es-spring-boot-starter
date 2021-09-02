package com.c.framework.elasticsearch.constants;

/**
 * es分词器类型 暂时没用枚举 注解里用不了暂时先用这个
 *
 * @author W.C
 */
public interface EsAnalyzerType {

    /**
     * 默认分词器，按词切分，小写处理
     * 它提供了基于语法的标记化（基于Unicode文本分割算法），适用于大多数语言
     */
    String STANDARD = "standard";

    /**
     * 按照非字母切分(符号被过滤), 小写处理
     * 当它遇到只要不是字母的字符，就将文本解析成term，而且所有的term都是小写的。
     */
    String SIMPLE = "simple";

    /**
     * 小写处理，停用词过滤(the,a,is)
     */
    String STOP = "stop";

    /**
     * 按照空格切分，不转小写
     */
    String WHITESPACE = "whitespace";

    /**
     * 不分词，直接将输入当作输出
     */
    String KEYWORD = "keyword";

    /**
     * 正则表达式，默认\W+(非字符分割)
     */
    String PATTER = "patter";

    /**
     * 正则表达式，默认\W+(非字符分割)
     */
    String PATTERN = "pattern";

    /**
     * 提供了30多种常见语言的分词器
     */
    String LANGUAGE = "language";

    /**
     * 自定义分词器
     */
    String customer = "customer";

    /**
     * 全量连续分词
     */
    String FULLTEXT = "fulltext";

    /**
     * 全量连续分词
     */
    String IK_MAX = "fulltext";
}
