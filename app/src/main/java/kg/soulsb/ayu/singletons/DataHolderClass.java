package kg.soulsb.ayu.singletons;

import android.os.Parcelable;

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
    private  boolean serviceRunning = false;
    private Parcelable currentRowTaskActivity = null;

    public boolean isServiceRunning() {
        return serviceRunning;
    }

    public void setServiceRunning(boolean serviceRunning) {
        this.serviceRunning = serviceRunning;
    }

    public void setCurrentRowTaskActivity(Parcelable currentRowTaskActivity)
    {
        this.currentRowTaskActivity = currentRowTaskActivity;
    }

    public Parcelable getCurrentRowTaskActivity() {
        return currentRowTaskActivity;
    }
}