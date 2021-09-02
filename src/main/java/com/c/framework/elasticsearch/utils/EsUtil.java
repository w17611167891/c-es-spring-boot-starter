package com.c.framework.elasticsearch.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.c.framework.elasticsearch.annotation.Index.EsIndex;
import com.c.framework.elasticsearch.annotation.Index.EsIndexMappingField;
import com.c.framework.elasticsearch.annotation.Index.EsScriptField;
import com.c.framework.elasticsearch.config.*;
import com.c.framework.elasticsearch.constants.*;
import com.c.framework.elasticsearch.utils.es.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * es操作工具类
 *
 * @author W.C
 */
public class EsUtil {

    private static final Log LOGGER = LogFactory.getLog(EsUtil.class);


    private static RestHighLevelClient client;

//    private static HttpHost[] hosts;

    /**
     * 初始化es客户端
     *
     * @param config
     */
    public static void init(EsConfig config) {
        client = ExUtil.exceptionHandler("Elasticsearch客户端初始化:", () -> {
            HttpHost[] hosts = EsUtil.generateHost(config.getHosts());
            RestClientBuilder build = RestClient.builder(hosts)
                    .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder
                            .setMaxConnTotal(config.getMaxConnTotal())
                            .setMaxConnPerRoute(config.getMaxConnPerRoute()))
                    .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                            .setConnectTimeout(config.getConnectTimeout())
                            .setSocketTimeout(config.getSocketTimeout())
                            .setConnectionRequestTimeout(config.getConnectionRequestTimeout()));
            return new RestHighLevelClient(build);
        });
    }

    /**
     * 根据地址字符串生成地址列表
     *
     * @param hosts
     * @return
     */
    private static HttpHost[] generateHost(String hosts) {
        String[] hostArr = hosts.split(",");
        HttpHost[] httpHosts = new HttpHost[hostArr.length];
        for (int i = 0; i < hostArr.length; i++) {
            String[] host = hostArr[i].split("://");
            String ip = host[1].substring(0, host[1].lastIndexOf(":"));
            String port = host[1].substring(host[1].lastIndexOf(":") + 1);
            httpHosts[i] = new HttpHost(ip, Integer.valueOf(port), host[0]);
        }
        return httpHosts;
    }

    /**
     * 创建索引
     *
     * @param entity
     * @return
     */
    public static boolean indexAdd(Class<?> entity) {
        EsIndex esIndex = entity.getAnnotation(EsIndex.class);
        if (esIndex == null) return false;
        if (indexExist(esIndex.name())) return false;
        Index index = new Index();
        index.setIndex(esIndex.name());
        IndexMapping mapping = new IndexMapping();
        for (Field field : entity.getDeclaredFields()) {
            EsIndexMappingField esField = field.getAnnotation(EsIndexMappingField.class);
            if (esField != null) mapping.addProperty(field.getName(), EsUtil.getField(esField));
        }
        index.setMapping(mapping);
        return EsUtil.indexAdd(index);
    }

    /**
     * 生成字段
     *
     * @param esField
     * @return
     */
    private static IndexField getField(EsIndexMappingField esField) {
        return new IndexField(esField.type(), StringUtils.isEmpty(esField.analyzer()) ? null : esField.analyzer());
    }

    /**
     * 新增索引
     *
     * @param index
     * @return
     */
    public static boolean indexAdd(Index index) {
        if (indexExist(index.getIndex())) return false;
        CreateIndexRequest indexRequest = new CreateIndexRequest(index.getIndex());
        int size = index.getMapping().getProperties().size();
        indexRequest.mapping(JSON.toJSONString(EsUtil.generateMapping(index.getMapping())), XContentType.JSON);
        if (index.getMapping().getProperties().size() > size) EsUtil.generateSettings(index);
        if (index.getSettings() != null)
            indexRequest.settings(JSON.toJSONString(index.getSettings()), XContentType.JSON);
        CreateIndexResponse response = ExUtil.exceptionHandler("索引创建:", () -> client.indices().create(indexRequest, RequestOptions.DEFAULT));
        LOGGER.info("索引创建:" + (response.isAcknowledged() ? Constants.S_SUCCESS : Constants.S_FAIL));
        return response.isAcknowledged();
    }

    /**
     * 设置全量索引
     *
     * @param index
     */
    private static void generateSettings(Index index) {
        IndexSettings fullTextAnalysis = getFullTextAnalysis();
        if (index.getSettings() != null) {
            IndexSettings settings = index.getSettings();
            IndexAnalysis analysis = settings.getAnalysis();
            analysis.getAnalyzer().putAll(fullTextAnalysis.getAnalysis().getAnalyzer());
        } else {
            index.setSettings(fullTextAnalysis);
        }
    }

    /**
     * 生成全量索引
     *
     * @return
     */
    private static IndexSettings getFullTextAnalysis() {
        IndexSettings settings = new IndexSettings();
        IndexAnalysis indexAnalysis = new IndexAnalysis();
        Map<String, IndexAnalyzer> analyzer = new HashMap<>();
        IndexAnalyzer analyzerItem = new IndexAnalyzer();
        analyzerItem.setType(EsAnalyzerType.PATTERN);
        analyzerItem.setPattern(" ");
        analyzerItem.setLowercase(false);
        analyzer.put(EsAnalyzerType.FULLTEXT, analyzerItem);
        indexAnalysis.setAnalyzer(analyzer);
        settings.setAnalysis(indexAnalysis);
        return settings;
    }

    /**
     * 生成全量索引字段
     *
     * @param mapping
     * @return
     */
    private static IndexMapping generateMapping(IndexMapping mapping) {
        List<String> fields = new ArrayList<>();
        mapping.getProperties().entrySet().forEach(entry -> {
            IndexField field = entry.getValue();
            String analyzer = field.getAnalyzer();
            if (EsAnalyzerType.FULLTEXT.equals(analyzer)) {
                fields.add(getFullName(entry.getKey()));
                field.setAnalyzer(EsFieldType.KEYWORD);
            }
        });
        fields.forEach(field -> mapping.addProperty(field, new IndexField(EsFieldType.TEXT, EsAnalyzerType.FULLTEXT)));
        return mapping;
    }

    /**
     * 判断索引是否存在
     *
     * @param index
     * @return
     */
    public static boolean indexExist(String index) {
        return ExUtil.exceptionHandler("索引存在查询:", () -> client.indices()
                .exists(new GetIndexRequest(index), RequestOptions.DEFAULT));
    }

    /**
     * 数据添加 系统id
     *
     * @param source
     * @return
     */
    public static String addSource(SourceUpdate source) {
        return ExUtil.exceptionHandler("新增索引文档:" + JSON.toJSONString(source), () -> client.index(new IndexRequest(source.getIndex())
                .source(getJSONString(source.getSource()), XContentType.JSON), RequestOptions.DEFAULT)).getId();

    }

    /**
     * 获取json结果包含全量索引字段
     *
     * @param source
     * @return
     */
    private static String getJSONString(Object source) {
        EsUtil.checkNotNull(source);
        List<String> fullFields = EsUtil.getFullFields(source.getClass());
        if (!fullFields.isEmpty()) {
            JSONObject data = (JSONObject) JSON.toJSON(source);
            if (data != null)
                fullFields.stream().filter(field -> data.getString(field) != null).forEach(field -> data.put(getFullName(field), EsUtil.getFullText(data.getString(field))));
            return data.toJSONString();
        }
        return JSON.toJSONString(source);
    }

    /**
     * 非空字段校验
     *
     * @param source
     */
    private static void checkNotNull(Object source) {
        if (!(source instanceof BaseSource)) return;
        Class<?> sourceClass = source.getClass();
        Field[] fields = sourceClass.getDeclaredFields();
        for (Field field : fields) {
            EsIndexMappingField indexField = field.getAnnotation(EsIndexMappingField.class);
            if (indexField != null && indexField.notNull())
                if (ClassUtil.getValue(source, field) == null) throw new EsException("非空字段设置值不能为空:" + field.getName());
        }
    }

    /**
     * 条件更新
     *
     * @param source
     * @return
     */
    public static <T> void updateSourceByCondition(SourceUpdate<T> source) {
        SourceQuery query = EsUtil.buildUpdateQueryBySource(source);
        SourcePage<T> page = null;
        for (int i = 0; i == 0 || i * query.getPageSize() < page.getTotal().intValue(); i++) {
            query.setCurPage(i + 1);
            page = EsUtil.query(query);
            if (page.getTotal() == 0) {
                EsUtil.addSource(source);
                return;
            }
            if (source.getSource() instanceof Map) {
                EsUtil.mapUpdateHandler((List<Map<String, Object>>) page, source);
            } else {
                EsUtil.updateHandler(page.getResultList(), source);
            }
        }
        LOGGER.info("更新记录" + page.getTotal() + "条");
    }

    /**
     * 实体更新拦截器
     *
     * @param oldSourceList
     * @param source
     * @param <T>
     */
    private static <T> void updateHandler(List<T> oldSourceList, SourceUpdate<T> source) {
        T newSource = source.getSource();
        if (!(newSource instanceof BaseSource)) return;
        for (T oldSource : oldSourceList) {
            ClassUtil.copyBeanForNotNullField(newSource, oldSource);
            source.setId(((BaseSource) oldSource).getId());
            source.setSource(oldSource);
            EsUtil.updateSourceById(source);
        }

    }

    /**
     * map更新拦截器
     *
     * @param oldSourceList
     * @param source
     */
    private static void mapUpdateHandler(List<Map<String, Object>> oldSourceList, SourceUpdate source) {
        Map<String, Object> newSource = (Map<String, Object>) source.getSource();
        for (Map<String, Object> oldSource : oldSourceList) {
            newSource.entrySet().stream().filter(entry -> entry.getValue() != null)
                    .forEach(entry -> oldSource.put(entry.getKey(), entry.getValue()));
            source.setId(oldSource.get("id").toString());
            EsUtil.updateSourceById(source);
        }
    }

    private static <T> SourceQuery buildUpdateQueryBySource(SourceUpdate<T> source) {
        if (source.getConditionList() == null || source.getConditionList().isEmpty())
            throw new EsException(" 条件更新必须设置更新条件");
        SourceQuery query = new SourceQuery();
        query.setIndex(source.getIndex());
        query.setFilterConditionList(source.getConditionList());
        query.setPageSize(200);
        query.setReturnType(!(source.getSource() instanceof Map) ? source.getSource().getClass() : null);
        return query;
    }

    /**
     * 条件删除
     *
     * @param source
     * @return
     */
    public static <T> void deleteSourceByCondition(SourceUpdate<T> source) {
        SourceQuery query = EsUtil.buildUpdateQueryBySource(source);
        SourcePage<T> page = null;
        for (int i = 0; i == 0 || i * query.getPageSize() < page.getTotal().intValue(); i++) {
            query.setCurPage(i + 1);
            page = EsUtil.query(query);
            if (source.getSource() instanceof Map) {
                page.getResultList().forEach(result -> {
                    source.setId(((Map<String, Object>) result).get("id").toString());
                    deleteSourceById(source);
                });
            } else {
                page.getResultList().forEach(result -> {
                    source.setId(((BaseSource) result).getId());
                    deleteSourceById(source);
                });
            }
        }
        LOGGER.info("删除记录" + page.getTotal() + "条");
    }

    /**
     * 通过ID删除文档
     *
     * @param source
     */
    public static void deleteSourceById(SourceUpdate source) {
        ExUtil.exceptionHandler("删除索引文档:" + JSON.toJSONString(source), () -> client.delete(new DeleteRequest(source.getIndex())
                .id(source.getId()), RequestOptions.DEFAULT));
    }

    /**
     * 根据id更新索引文档 实际删了再增
     *
     * @param source
     */
    public static void updateSourceById(SourceUpdate source) {
        ExUtil.exceptionHandler("更新索引文档:" + JSON.toJSONString(source), () -> client.update(new UpdateRequest()
                        .index(source.getIndex())
                        .id(source.getId()).doc(getJSONString(source.getSource()), XContentType.JSON)
                , RequestOptions.DEFAULT));
    }

    /**
     * 查询
     *
     * @param param
     * @param <T>
     * @return
     */
    public static <T> SourcePage<T> query(SourceQuery param) {
        SearchRequest searchRequest = new SearchRequest(param.getIndex());
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource().fetchSource(true);
        List<String> fieldList = EsUtil.generateScriptFields(searchSourceBuilder, param.getReturnType(), param.getScriptParam());
        List<String> fullFields = EsUtil.getFullFields(param.getReturnType());
        if (param.getConditionList() != null) {
            EsUtil.getFullTextCondition(param.getConditionList(), fullFields);
            QueryBuilder query = EsUtil.getBoolQuery(param.getConditionList());
            if (query instanceof FunctionScoreQueryBuilder)
                param.addSort("_score", true);
            searchSourceBuilder.query(query);
        }
        if (param.getFilterConditionList() != null) {
            searchSourceBuilder.postFilter(EsUtil.getBoolQuery(param.getFilterConditionList()));
        }
        if(!StringUtils.isEmpty(param.getGroupField()))
            searchSourceBuilder.collapse(new CollapseBuilder(param.getGroupField()));
        EsUtil.setPage(searchSourceBuilder, param.getFrom(), param.getPageSize());
        if (param.getSortList() != null)
            searchSourceBuilder.sort(getSortBuild(param.getSortList()));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = ExUtil.exceptionHandler("查询文档:" + JSON.toJSONString(param)
                , () -> client.search(searchRequest, RequestOptions.DEFAULT));
        LOGGER.debug("共查询到[{" + searchResponse.getHits().getTotalHits().value + "}]条数据,处理数据条数[{" + searchResponse.getHits().getHits().length + "}]");
        return searchResponse.status().getStatus() == 200 ? EsUtil.getResultList(searchResponse, param.getReturnType(), fieldList) : new SourcePage<>();
    }

    /**
     * 获取全量条件
     *
     * @param conditionList
     * @param fieldList
     */
    private static void getFullTextCondition(List<SourceCondition> conditionList, List<String> fieldList) {
        if (fieldList.isEmpty()) return;
        conditionList.stream().filter(condition -> fieldList.contains(condition.getKey())).forEach(condition -> condition.setKey(EsUtil.getFullName(condition.getKey())));
    }

    /**
     * 获取全量字段名
     *
     * @param fieldName
     * @return
     */
    private static String getFullName(String fieldName) {
        return fieldName + "_" + EsAnalyzerType.FULLTEXT;
    }

    /**
     * 获取全量字段
     *
     * @param sourceClass
     * @return
     */
    private static List<String> getFullFields(Class<?> sourceClass) {
        List<String> fullFieldList = new ArrayList<>();
        if (sourceClass == null) return fullFieldList;
        Field[] fields = sourceClass.getDeclaredFields();
        for (Field field : fields) {
            EsIndexMappingField normal = field.getAnnotation(EsIndexMappingField.class);
            if (normal != null && normal.analyzer().equals(EsAnalyzerType.FULLTEXT)) {
                fullFieldList.add(field.getName());
            }
        }
        return fullFieldList;
    }

    /**
     * 设置查询字段
     *
     * @param searchSourceBuilder
     * @param sourceClass
     * @param scriptParams
     * @return
     */
    private static List<String> generateScriptFields(SearchSourceBuilder searchSourceBuilder, Class<?> sourceClass, Map<String, Object[]> scriptParams) {
        List<String> scriptFieldList = new ArrayList<>();
        if (sourceClass == null || scriptParams == null || scriptParams.isEmpty()) return scriptFieldList;
        Field[] fields = sourceClass.getDeclaredFields();
        for (Field field : fields) {
            //TODO 动态参数
            EsScriptField scriptField = field.getAnnotation(EsScriptField.class);
            if (scriptField != null) {
                String scriptFieldName = Constants.S_EMPTY.equals(scriptField.name()) ? field.getName() : scriptField.name();
                if (scriptParams.get(scriptFieldName) == null) continue;
                searchSourceBuilder.scriptField(scriptFieldName, new Script(String.format(scriptField.script(), scriptParams.get(scriptFieldName))));
                scriptFieldList.add(scriptFieldName);
            }
        }
        return scriptFieldList;
    }

    /**
     * 获取排序列表
     *
     * @param sortList
     * @return
     */
    private static List<SortBuilder> getSortBuild(List<SourceSort> sortList) {
        return sortList.stream().map(sort -> getSortBuild(sort)).collect(Collectors.toList());
    }

    /**
     * 获取排序
     *
     * @param sort
     * @return
     */
    private static SortBuilder getSortBuild(SourceSort sort) {
        SortBuilder sortBuilder = null;
        switch (sort.getSortType()) {
            case SortType.GEN_SORT:
                sortBuilder = SortBuilders.geoDistanceSort(sort.getFieldName(), sort.getLat(), sort.getLon())
                        .unit(DistanceUnit.METERS);
                break;
            default:
                sortBuilder = SortBuilders.fieldSort(sort.getFieldName());
        }
        return sortBuilder.order(sort.isDesc() ? SortOrder.DESC : SortOrder.ASC);
    }

    /**
     * 生成查询返回值
     *
     * @param searchResponse
     * @param targetClass
     * @param fieldList
     * @param <T>
     * @return
     */
    private static <T> SourcePage<T> getResultList(SearchResponse searchResponse, Class<T> targetClass, List<String> fieldList) {
        SourcePage<T> sourcePage = new SourcePage();
        List<T> resultList = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, DocumentField> fields = hit.getFields();
            T result = null;
            Map<String, Object> resultMap = hit.getSourceAsMap();
            resultMap.put("id", hit.getId());
            fieldList.forEach(field -> resultMap.put(field, fields.get(field).getValue()));
            if (targetClass != null) {
                result = JSON.parseObject(JSON.toJSONString(resultMap), targetClass);
            } else {
                resultMap.put("id", hit.getId());
                result = (T) resultMap;
            }
            resultList.add(result);
        }
        sourcePage.setResultList(resultList);
        sourcePage.setTotal(searchResponse.getHits().getTotalHits().value);
        return sourcePage;
    }

    /**
     * 设置分页信息不设置默认10条
     *
     * @param searchSourceBuilder
     * @param from
     * @param size
     */
    private static void setPage(SearchSourceBuilder searchSourceBuilder, Integer from, Integer size) {
        if (from == null || size == null) return;
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
    }

    /**
     * 获取查询条件
     *
     * @param conditionList
     * @return
     */
    private static QueryBuilder getBoolQuery(List<SourceCondition> conditionList) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        long weightNum = conditionList.stream().filter(condition -> EsUtil.generateQuery(boolQuery, condition) > 0).count();
        return weightNum > 0 ? getFunctionScoreQuery(boolQuery, conditionList, (int) weightNum) : boolQuery;
    }

    /**
     * 生成查询条件
     *
     * @param boolQuery
     * @param condition
     * @return
     */
    private static int generateQuery(BoolQueryBuilder boolQuery, SourceCondition condition) {
        switch (condition.getRelationType()) {
            case RelationType.MUST:
                boolQuery.must(EsUtil.getQueryBuilder(condition));
                break;
            default:
                boolQuery.should(EsUtil.getQueryBuilder(condition));
        }
        return condition.getWeight() != null ? 1 : 0;
    }

    /**
     * 生成查询条件
     *
     * @param condition
     * @return
     */
    private static QueryBuilder getQueryBuilder(SourceCondition condition) {
        switch (condition.getQueryType()) {
            case QueryType.MATH:
                return QueryBuilders.matchQuery(condition.getKey(), condition.getValue());
            case QueryType.RANGE:
                return EsUtil.getRangeQuery(condition);
            default:
                return QueryBuilders.termQuery(condition.getKey(), condition.getValue());
        }
    }

    /**
     * 获取范围条件
     *
     * @param condition
     * @return
     */
    private static QueryBuilder getRangeQuery(SourceCondition condition) {
        RangeQueryBuilder range = QueryBuilders.rangeQuery(condition.getKey());
        switch (condition.getRangeType()) {
            case RangeType.GT:
                return range.gt(condition.getValue());
            case RangeType.LT:
                return range.lt(condition.getValue());
            case RangeType.LTE:
                return range.lte(condition.getValue());
            default:
                return range.gte(condition.getValue());
        }
    }

    /**
     * 生成权重查询条件
     *
     * @param query
     * @param conditionList
     * @param weightNum
     * @return
     */
    private static FunctionScoreQueryBuilder getFunctionScoreQuery(BoolQueryBuilder query, List<SourceCondition> conditionList, int weightNum) {
        FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[weightNum];
        for (int i = 0; i < conditionList.size(); i++) {
            SourceCondition condition = conditionList.get(i);
            if (condition.getWeight() != null) {
                filterFunctionBuilders[i] = new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.matchQuery(condition.getKey(), condition.getValue())
                        , ScoreFunctionBuilders.weightFactorFunction(condition.getWeight()));
            }
        }
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(query, filterFunctionBuilders);
        return functionScoreQueryBuilder;
    }

    /**
     * 生成全量文本
     *
     * @param s
     * @param chars
     * @param i
     * @return
     */
    private static String getFullText(String s, char[] chars, int i) {
        if (chars.length == i) return "";
        return getFullText(s + chars[i], chars, i + 1) + s + chars[i] + " ";
    }

    /**
     * 生成全量文本
     *
     * @param text
     * @return
     */
    private static String getFullText(String text) {
        System.out.println(text);
        String s = "";
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            s += getFullText("", chars, i);
        }
        return s;
    }

    public static void main(String[] args) {
//        String s = "啊";//150之后慢
//        for(int i=0;i<100000;i++){
//            System.out.println(i);
//            getFullText(s+='按');
//        }
        System.out.println(generateHost("http://1.1.1.1:22,https://2.3.2.2:2221"));
    }

}
