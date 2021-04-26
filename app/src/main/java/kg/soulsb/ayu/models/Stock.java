package kg.soulsb.ayu.models;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class Stock {

    public static final String TABLE  = "stocks";

    public static String KEY_StockId   = "StockId";
    public static String KEY_ItemGuid     = "ItemGuid";
    public static String KEY_WarehouseGuid     = "WarehouseGuid";
    public static String KEY_Stock     = "Stock";
    public static String KEY_Base     = "Base";



    private String stockId;
    private String itemGuid;
    private String warehouseGuid;
    private int stock;
    private String base;

    public Stock()
    {

    }
    public Stock(String itemGuid, String warehouseGuid, int stock) {
        this.itemGuid = itemGuid;
        this.warehouseGuid = warehouseGuid;
        this.stock = stock;

    }

    public int getStock() {
        return stock;
    }

    @Override
    public String toString() {
        return ""+getItemGuid()+" "+getStock();
    }

    public String getItemGuid() {
        return itemGuid;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public void setItemGuid(String guid) {
        this.itemGuid = guid;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getWarehouseGuid() {
        return warehouseGuid;
    }

    public void setWarehouseGuid(String warehouseGuid) {
        this.warehouseGuid = warehouseGuid;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getBase() {
        return base;
    }
}
