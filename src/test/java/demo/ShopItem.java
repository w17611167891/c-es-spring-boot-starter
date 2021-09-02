package demo;

import com.c.framework.elasticsearch.annotation.Index.EsIndex;
import com.c.framework.elasticsearch.annotation.Index.EsIndexMappingField;
import com.c.framework.elasticsearch.annotation.Index.EsScriptField;
import com.c.framework.elasticsearch.constants.EsAnalyzerType;
import com.c.framework.elasticsearch.utils.es.BaseSource;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@EsIndex(name = "test_1")
public class ShopItem extends BaseSource {

    @EsIndexMappingField
    private String itemId;

    @EsIndexMappingField
    private String shopId;

    @EsIndexMappingField(analyzer = EsAnalyzerType.FULLTEXT)
    private String shopName;

    @EsIndexMappingField(analyzer = EsAnalyzerType.FULLTEXT)
    private String itemName;

    @EsScriptField(byField = "lon",script = "doc['lon'].arcDistance(%f, %f)")
    private String distance;

    @EsIndexMappingField
    private String itemValue;

    @EsIndexMappingField
    private String lon;

    @EsIndexMappingField
    private String cityCode;

    @EsIndexMappingField
    private String shopStatus;

    @EsIndexMappingField
    private String shopLabel;

}
