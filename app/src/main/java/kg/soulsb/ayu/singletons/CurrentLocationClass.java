package kg.soulsb.ayu.singletons;

import android.location.Location;

/**
 * Created by Sultanbek Baibagyshev on 2/24/17.
 */

public class CurrentLocationClass {
    private static CurrentLocationClass dataObject = null;

    private CurrentLocationClass() {
        // left blank intentionally
        currentLocation = new Location("");
        currentLocation.setLatitude(0);
        currentLocation.setLongitude(0);
        currentLocation.setAccuracy(999);
    }

    public static CurrentLocationClass getInstance() {
        if (dataObject == null)
            dataObject = new CurrentLocationClass();

        return dataObject;
    }

    private Location currentLocation;


    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}
