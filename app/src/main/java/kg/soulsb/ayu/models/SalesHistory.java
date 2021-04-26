package kg.soulsb.ayu.models;

public class SalesHistory {

    public static final String TABLE  = "salesHistory";

    public static String KEY_salesHistoryID   = "salesHistoryID";
    public static String KEY_ClientGuid   = "ClientGuid";
    public static String KEY_ItemGuid     = "ItemGuid";
    public static String KEY_Date1     = "Date1";
    public static String KEY_Qty1     = "Qty1";
    public static String KEY_Date2     = "Date2";
    public static String KEY_Qty2     = "Qty2";
    public static String KEY_Date3     = "Date3";
    public static String KEY_Qty3     = "Qty3";
    public static String KEY_Base    = "Base";

    private static final long serialVersionUID = 1L;

    private String clientGuid;
    private String itemGuid;
    private String date1;
    private String date2;
    private String date3;
    private double qty1;
    private double qty2;
    private double qty3;
    private String base;

    public SalesHistory()
    {

    }

    public SalesHistory(String clientGuid, String itemGuid, String date1, double qty1, String date2, double qty2, String date3, double qty3, String base)
    {
        this.clientGuid = clientGuid;
        this.itemGuid = itemGuid;
        this.date1 = date1;
        this.date2 = date2;
        this.date3 = date3;
        this.qty1 = qty1;
        this.qty2 = qty2;
        this.qty3 = qty3;
        this.base = base;
    }

    public String getClientGuid() {
        return clientGuid;
    }

    public void setClientGuid(String clientGuid) {
        this.clientGuid = clientGuid;
    }

    public String getItemGuid() {
        return itemGuid;
    }

    public void setItemGuid(String itemGuid) {
        this.itemGuid = itemGuid;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

    public String getDate3() {
        return date3;
    }

    public void setDate3(String date3) {
        this.date3 = date3;
    }

    public double getQty1() {
        return qty1;
    }

    public void setQty1(double qty1) {
        this.qty1 = qty1;
    }

    public double getQty2() {
        return qty2;
    }

    public void setQty2(double qty2) {
        this.qty2 = qty2;
    }

    public double getQty3() {
        return qty3;
    }

    public void setQty3(double qty3) {
        this.qty3 = qty3;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }
}
