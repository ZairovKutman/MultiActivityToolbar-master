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
    public static String KEY_deviceId = "deviceId";
    public static String KEY_accuracy = "accuracy";

    private Double latitude;
    private Double longitude;
    private String agent;
    private String formattedDate;
    private Float speed;
    private String deviceID;
    private Float accuracy;

    public MyLocation()
    {
    }
    public MyLocation(Double latitude, Double longitude, String agent, String formattedDate, Float speed, Float accuracy, String deviceID) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.agent = agent;
        this.formattedDate = formattedDate;
        this.speed = speed;
        this.deviceID = deviceID;
        this.accuracy = accuracy;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
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

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public float getAccuracy() {
        return accuracy;
    }
    public void setAccuracy(Float accuracy){ this.accuracy = accuracy; }
}
