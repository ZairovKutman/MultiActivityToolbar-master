package kg.soulsb.ayu.models;

import java.io.Serializable;

/**
 * Created by soulsb on 4/27/17.
 */

public class Unit  implements Serializable {


    public static final String TABLE  = "units";

    public static String KEY_UnitId   = "UnitId";
    public static String KEY_unitGuid     = "UnitGuid";
    public static String KEY_ItemGuid     = "ItemGuid";
    public static String KEY_coefficient     = "Coefficient";
    public static String KEY_name     = "Name";
    public static String KEY_Base     = "Base";
    public static String KEY_Default     = "DefaultUnit";



    private String unitId;
    private String itemGuid;
    private String unitGuid;
    private double coefficient;
    private String name;
    private String Base;
    private boolean aDefault;

    public Unit()
    {}

    public Unit(String unitGuid, String itemGuid, double coefficient, String name, boolean isDefault) {
        this.itemGuid = itemGuid;
        this.unitGuid = unitGuid;
        this.coefficient = coefficient;
        this.name = name;
        this.aDefault = isDefault;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getItemGuid() {
        return itemGuid;
    }

    public void setItemGuid(String itemGuid) {
        this.itemGuid = itemGuid;
    }

    public String getUnitGuid() {
        return unitGuid;
    }

    public void setUnitGuid(String unitGuid) {
        this.unitGuid = unitGuid;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBase() {
        return Base;
    }

    public void setBase(String base) {
        Base = base;
    }

    @Override
    public String toString() {
    return getName()+".";
    }

    public boolean isDefault() {
        return aDefault;
    }

    public void setDefault(String aDefault) {
        if (aDefault.equals("1"))
            this.aDefault = true;
        else
            this.aDefault = false;

    }
}
