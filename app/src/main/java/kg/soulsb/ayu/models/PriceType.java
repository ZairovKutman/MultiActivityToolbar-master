package kg.soulsb.ayu.models;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class PriceType {

    public static final String TABLE  = "pricetypes";

    public static String KEY_PricetypeId   = "pricetypeId";
    public static String KEY_Guid     = "Guid";
    public static String KEY_Name     = "Name";
    public static String KEY_Base     = "Base";


    private String pricetypeId;
    private String guid;
    private String name;
    private String base;

    public PriceType()
    {

    }
    public PriceType(String guid, String name) {
        this.guid = guid;
        this.name = name;

    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getGuid() {
        return guid;
    }

    public String getPriceTypeId() {
        return pricetypeId;
    }

    public void setPriceTypeId(String pricetypeId) {
        this.pricetypeId = pricetypeId;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getBase() {
        return base;
    }
}
