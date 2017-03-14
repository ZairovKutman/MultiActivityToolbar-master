package kg.soulsb.ayu.singletons;

import android.app.Activity;
import android.content.Intent;

import kg.soulsb.ayu.services.MyService;

/**
 * Created by Sultanbek Baibagyshev on 2/7/17.
 */

public class MyServiceActivatorClass {
    private static MyServiceActivatorClass dataObject = null;
    private static final String TAG = "MyService";
    private Activity activity;
    private Intent mIntent = null;

    private MyServiceActivatorClass(Activity activity) {
        this.activity = activity;
    }

    public static MyServiceActivatorClass getInstance(Activity activity) {
        if (dataObject == null)
            dataObject = new MyServiceActivatorClass(activity);
        return dataObject;
    }

    public void start()
    {
        if (mIntent == null) {
            mIntent = new Intent(activity, MyService.class);
            activity.startService(mIntent);
        }
        System.out.println("Started service ++++++++++++++++");
    }

    public void stop()
    {
        if (mIntent!=null) {
            activity.stopService(mIntent);
            mIntent = null;
            System.out.println("real stop");
        }
        System.out.println("STOPPED service ++++++++++++++++");
    }
}
