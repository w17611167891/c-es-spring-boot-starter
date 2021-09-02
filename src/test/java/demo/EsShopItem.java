package demo;

import com.c.framework.elasticsearch.annotation.Index.EsIndex;
import com.c.framework.elasticsearch.annotation.Index.EsIndexMappingField;
import com.c.framework.elasticsearch.annotation.Index.EsScriptField;
import com.c.framework.elasticsearch.annotation.query.EsQueryField;
import com.c.framework.elasticsearch.constants.EsAnalyzerType;
import com.c.framework.elasticsearch.constants.EsFieldType;
import com.c.framework.elasticsearch.utils.es.BaseSource;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@EsIndex(name = "shop_item_3")
public class EsShopItem extends BaseSource {

    @EsQueryField
    @EsIndexMappingField
    private String itemId;

    @EsQueryField
    @EsIndexMappingField
    private String shopId;

    @EsIndexMappingField(type = EsFieldType.TEXT,analyzer = EsAnalyzerType.FULLTEXT)
    private String shopName;

    @EsIndexMappingField
    private String shopPic;

    @EsIndexMappingField(type = EsFieldType.TEXT,analyzer = EsAnalyzerType.FULLTEXT)
    private String shopAddress;

    @EsIndexMappingField(type = EsFieldType.GEO_POINT)
    private String location;

    @EsIndexMappingField
    private String cityCode;

    @EsIndexMappingField(type = EsFieldType.DATE)
    private Date shopCreateTime;

    @EsIndexMappingField
    private String shopStatus;

    @EsIndexMappingField
    private String linkShop;

    @EsIndexMappingField
    private String shopLabel;

    @EsIndexMappingField(type = EsFieldType.TEXT,analyzer = EsAnalyzerType.FULLTEXT)
    private String itemMainHead;

    @EsIndexMappingField(type = EsFieldType.TEXT,analyzer = EsAnalyzerType.FULLTEXT)
    private String itemSubHead;

    @EsIndexMappingField
    private String itemNoteValue;

    @EsIndexMappingField
    private String itemPic;

    @EsIndexMappingField
    private String itemStatus;

    @EsIndexMappingField(type = EsFieldType.DATE)
    private Date itemStartDate;

    @EsIndexMappingField(type = EsFieldType.DATE)
    private Date itemEndDate;

    @EsIndexMappingField
    private String itemLabel;

    @EsIndexMappingField
    private String leagueFlag;

    @EsIndexMappingField(type = EsFieldType.DATE)
    private Date leagueStartDate;

    @EsIndexMappingField(type = EsFieldType.DATE)
    private Date leagueEndDate;

    @EsIndexMappingField
    private String leagueStatus;

    @EsScriptField(byField = "location",script = "doc['location'].arcDistance(%f, %f)")
    private String distance;

}
