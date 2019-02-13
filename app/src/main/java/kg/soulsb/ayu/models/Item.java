package kg.soulsb.ayu.models;

import java.io.Serializable;

import kg.soulsb.ayu.helpers.repo.UnitsRepo;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class Item implements Serializable {

    public static final String TABLE  = "items";

    public static String KEY_ItemId   = "ItemId";
    public static String KEY_Guid     = "Guid";
    public static String KEY_Name     = "Name";
    public static String KEY_Unit     = "Unit";
    public static String KEY_UnitGUID     = "UnitGUID";
    public static String KEY_Price    = "Price";
    public static String KEY_Quantity    = "quantity";
    public static String KEY_Stock    = "Stock";
    public static String KEY_Base    = "Base";
    public static String KEY_Category    = "Category";
    public static String KEY_Sum    = "Sum";
    public static String KEY_isDelivered = "DeliveredItem";

    private String itemId;
    private String guid;
    private String name;
    private String unit;
    private Unit myUnit = new Unit();
    private String category;
    private double price;
    private double stock;
    private int quantity;
    private double sum;
    private String base;

    public Item()
    {
        myUnit.setName("");

    }
    public Item(String guid, String name, String unit, double price, double stock, String category) {
        this.guid = guid;
        this.name = name;
        this.unit = unit;
        this.price = price;
        this.stock = stock;
        this.category = category;
        myUnit.setName("");
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getGuid() {
        return guid;
    }

    public String getItemId() {
        return itemId;
    }

    public double getStock() {
        return stock;
    }

    public String getUnit() {
        return unit;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    @Override
    public boolean equals(Object o) {
        boolean retVal = false;

        if (o instanceof Item){
            Item ptr = (Item) o;
            retVal = ptr.getGuid().equals(this.getGuid());
        }

        return retVal;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getCategory() {
        if (category.equals("00000000-0000-0000-0000-000000000000"))
                return "";
        else
            return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Unit getMyUnit() {
        return myUnit;
    }

    public void setMyUnit(Unit myUnit) {
        this.myUnit = myUnit;
    }


    public void setMyUnitByGuid(String myUnitByGuid) {
        this.myUnit = new UnitsRepo().getUnitsObjectByItemGuidAndUnitGuid(getGuid(),myUnitByGuid);
    }
}
