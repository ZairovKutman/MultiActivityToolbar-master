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
}