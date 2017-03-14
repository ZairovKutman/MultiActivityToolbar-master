package kg.soulsb.ayu.singletons;

/**
 * Created by Sultanbek Baibagyshev on 2/7/17.
 */

public class DataHolderClass {
    private static DataHolderClass dataObject = null;

    private DataHolderClass() {
        // left blank intentionally
    }

    public static DataHolderClass getInstance() {
        if (dataObject == null)
            dataObject = new DataHolderClass();
        return dataObject;
    }
    private String addOrderComments;
    private String addOrderDateOtgruzki;
    public String getAddOrderComments() {
        return addOrderComments;
    }

    public void setAddOrderComments(String addOrderComments) {
        this.addOrderComments = addOrderComments;
    }

    public String getAddOrderDateOtgruzki() {
        return addOrderDateOtgruzki;
    }

    public void setAddOrderDateOtgruzki(String addOrderDateOtgruzki) {
        this.addOrderDateOtgruzki = addOrderDateOtgruzki;
    }
}