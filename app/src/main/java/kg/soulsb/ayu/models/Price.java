package kg.soulsb.ayu.models;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class Price {

    public static final String TABLE  = "prices";

    public static String KEY_PriceId   = "PriceId";
    public static String KEY_Guid     = "Guid";
    public static String KEY_PriceTypeGuid     = "PriceTypeGuid";
    public static String KEY_Price     = "Price";
    public static String KEY_Base     = "Base";


    private String priceId;
    private String guid;
    private String priceTypeGuid;
    private double price;
    private String base;

    public Price()
    {

    }
    public Price(String guid, Double price,String priceTypeGuid) {
        this.guid = guid;
        this.priceTypeGuid = priceTypeGuid;
        this.price = price;

    }

    public Double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return getGuid()+" "+Double.toString(getPrice());
    }

    public String getGuid() {
        return guid;
    }

    public String getPriceId() {
        return priceId;
    }

    public void setPriceId(String priceId) {
        this.priceId = priceId;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPriceTypeGuid() {
        return priceTypeGuid;
    }

    public void setPriceTypeGuid(String priceTypeGuid) {
        this.priceTypeGuid = priceTypeGuid;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getBase() {
        return base;
    }
}
