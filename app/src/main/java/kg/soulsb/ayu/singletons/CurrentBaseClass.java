package kg.soulsb.ayu.singletons;


import kg.soulsb.ayu.models.Baza;

/**
 * Created by Sultanbek Baibagyshev on 2/24/17.
 */

public class CurrentBaseClass {
    private static CurrentBaseClass dataObject = null;
    private String currentBase;
    private Baza currentBaseObject;

    private CurrentBaseClass() {
        currentBase = "";
    }

    public static CurrentBaseClass getInstance() {
        if (dataObject == null)
            dataObject = new CurrentBaseClass();

        return dataObject;
    }

    public String getCurrentBase() {
        return currentBase;
    }

    public void setCurrentBase(String currentBase) {
        this.currentBase = currentBase;
    }

    public Baza getCurrentBaseObject() {
        return currentBaseObject;
    }

    public void setCurrentBaseObject(Baza currentBaseObject) {
        this.currentBaseObject = currentBaseObject;
    }
}
