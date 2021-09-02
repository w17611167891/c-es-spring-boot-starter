package demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.c.framework.elasticsearch.config.EsConfig;
import com.c.framework.elasticsearch.constants.*;
import com.c.framework.elasticsearch.utils.DateUtil;
import com.c.framework.elasticsearch.utils.EsUtil;
import com.c.framework.elasticsearch.utils.es.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class A {

    public static final String INDEX_NAME = "shop_item_3";

    public static void main(String[] args) throws Exception {
        EsConfig config = new EsConfig();
        config.setHosts("http://10.151.31.193:9200");
        EsUtil.init(config);
//        testIndexAdd();
//        testBathAddSource();
//        testUpdate();
        testQueryShopSearch();
    }

    private static void testQueryShopSearch() {
        SourceQuery query = new SourceQuery();
        query.setIndex(INDEX_NAME);
        query.setReturnType(EsShopItem.class);
//        query.addCondition("shopName","众劢", RelationType.SHOULD, QueryType.TERM);
//        query.addCondition("itemName","众劢", RelationType.SHOULD, QueryType.TERM);
        query.addFilterCondition("itemStartDate", String.valueOf(DateUtil.getDayStart(100).getTime()), RelationType.MUST, QueryType.RANGE, null, RangeType.LTE);
        query.addFilterCondition("itemEndDate", String.valueOf(DateUtil.getDayStart(100).getTime()), RelationType.MUST, QueryType.RANGE, null, RangeType.GTE);
//        query.addCondition("shopAddress", "天津市", RelationType.SHOULD, QueryType.TERM);
        query.addSort("location",116.452200, 39.912955);
        Map<String, Object[]> params = new HashMap<>();
        params.put("distance",new Object[]{39.912955,116.452200});
        query.setScriptParam(params);
        System.out.println(JSON.toJSONString(EsUtil.query(query), true));
        System.out.println(String.valueOf(DateUtil.getDayStart(100).getTime()));
    }

    private static void testIndexAdd() {
        Index index = new Index();
        index.setIndex(INDEX_NAME);
        IndexMapping mapping = new IndexMapping();
        mapping.addProperty("itemId", new IndexField(EsFieldType.KEYWORD));
        mapping.addProperty("shopId", new IndexField(EsFieldType.KEYWORD));
        mapping.addProperty("shopName", new IndexField(EsFieldType.TEXT, EsAnalyzerType.FULLTEXT));
        mapping.addProperty("itemName", new IndexField(EsFieldType.TEXT, EsAnalyzerType.FULLTEXT));
        mapping.addProperty("lon", new IndexField(EsFieldType.GEO_POINT));
        mapping.addProperty("itemValue", new IndexField(EsFieldType.KEYWORD));
        mapping.addProperty("cityCode", new IndexField(EsFieldType.KEYWORD));
        mapping.addProperty("shopStatus", new IndexField(EsFieldType.KEYWORD));
        mapping.addProperty("shopLabel", new IndexField(EsFieldType.KEYWORD));
        index.setMapping(mapping);
        EsUtil.indexAdd(index);
    }

    private static void testUpdate() {
        ShopItem item = new ShopItem();
        item.setShopName("测试店铺");
        SourceUpdate<ShopItem> source = new SourceUpdate<>();
        source.setIndex(INDEX_NAME);
        source.setSource(item);
        source.addCondition("itemId", "ffbf36f2263b43508f2a365d2e9c5a93", RelationType.MUST, QueryType.TERM);
        EsUtil.updateSourceByCondition(source);
    }

    public static JSONArray getList(String fileName) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = null;
        StringBuilder s = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            s.append(line);
        }
        return JSON.parseObject(s.toString()).getJSONArray("RECORDS");
    }

    public static ShopItem getShop(JSONObject shop, JSONObject item, String itemValue) {
        ShopItem shopItem = new ShopItem();
        shopItem.setItemId(item.getString("item_id"));
        shopItem.setShopId(shop.getString("shop_id"));
        shopItem.setShopName(shop.getString("shop_name"));
        shopItem.setItemName(item.getString("main_head"));
        shopItem.setItemValue(itemValue);
        String[] shopLng = shop.getString("shop_lng").split(",");
        if (Double.valueOf(shopLng[1]) > 90 || Double.valueOf(shopLng[0]) > 180) return null;
        shopItem.setLon(Double.valueOf(shopLng[1]) + "," + Double.valueOf(shopLng[0]));
        shopItem.setCityCode(shop.getString("area_id"));
        shopItem.setShopStatus(shop.getString("shop_status"));
        shopItem.setShopLabel(shop.getString("shop_label"));
        return shopItem;
    }

    public static void testAddSource(ShopItem shopItem) {
        SourceUpdate source = new SourceUpdate();
        source.setIndex(INDEX_NAME);
        source.setSource(shopItem);
        EsUtil.addSource(source);
    }

    public static void testBathAddSource() throws Exception {

        JSONArray shopList = getList("C:\\Users\\EDZ\\Desktop\\t_shop.json");
        Map<String, JSONObject> shopMap = shopList.stream().collect(Collectors.toMap(shop -> ((JSONObject) shop).getString("shop_id"), shop -> ((JSONObject) shop)));
        JSONArray shopItemList = getList("C:\\Users\\EDZ\\Desktop\\t_shop_item.json");
        JSONArray shopItemNodeList = getList("C:\\Users\\EDZ\\Desktop\\t_item_note.json");
        int num = 0;
        for (int i = 0; i < shopItemList.size(); i++) {
            JSONObject item = shopItemList.getJSONObject(i);
            String itemValue = "";
            for (int j = 0; j < shopItemNodeList.size(); j++) {
                JSONObject itemNode = shopItemNodeList.getJSONObject(j);
                if (item.getString("item_id").equals(itemNode.getString("item_id"))
                        && itemNode.getString("note_value") != null
                        && !itemNode.getString("note_value").equals("null")) {

                    itemValue += itemNode.getString("note_value") + ";";
                }
            }
            JSONObject shop = shopMap.get(item.getString("shop_id"));
            if (shop == null) continue;
            shop.put("num", shop.get("num") == null ? 1 : shop.getIntValue("num") + 1);
            ShopItem data = getShop(shop, item, itemValue);
            if (data == null) continue;
            testAddSource(data);
            System.out.println(data);
            num++;
        }
        System.out.println(num);
    }
}
