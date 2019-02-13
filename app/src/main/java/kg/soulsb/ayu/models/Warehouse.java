package kg.soulsb.ayu.models;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class Warehouse {

    public static final String TABLE  = "warehouses";

    public static String KEY_WarehouseId   = "WarehouseId";
    public static String KEY_Guid     = "Guid";
    public static String KEY_Name     = "Name";
    public static String KEY_Base     = "Base";

    private String warehouseId;
    private String guid;
    private String name;
    private String base;

    public Warehouse()
    {

    }
    public Warehouse(String guid, String name) {
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

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
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
