package kg.soulsb.ayu.models;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class MyLocation {

    public static final String TABLE  = "myLocations";

    public static String KEY_MyLocationId   = "MyLocationId";
    public static String KEY_latitude     = "latitude";
    public static String KEY_longitude     = "longitude";
    public static String KEY_agent     = "agent";
    public static String KEY_formattedDate     = "formattedDate";
    public static String KEY_speed = "speed";


    private String myLocationId;
    private String latitude;
    private String longitude;
    private String agent;
    private String formattedDate;
    private String speed;

    public MyLocation()
    {
    }
    public MyLocation(String latitude, String longitude, String agent, String formattedDate, String speed) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.agent = agent;
        this.formattedDate = formattedDate;
        this.speed = speed;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
}
