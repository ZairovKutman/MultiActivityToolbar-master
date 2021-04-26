package kg.soulsb.ayu.models;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class DailyPhoto {

    public static final String TABLE  = "photos";

    public static String KEY_PhotoId   = "photoId";
    public static String KEY_clientGuid     = "clientGuid";
    public static String KEY_photoBytes     = "PhotoBytes";
    public static String KEY_agent     = "agent";
    public static String KEY_device_id     = "device_id";
    public static String KEY_dateClosed     = "dateClosed";
    public static String KEY_docGuid     = "docGuid";
    public static String KEY_latitude     = "latitude";
    public static String KEY_longitude    = "longitude";



    private String photoId;
    private String clientGuid;
    private byte[] photoBytes;
    private String agent;
    private String device_id;
    private String dateClosed;
    private String docGuid;
    private double latitude;
    private double longitude;


    public DailyPhoto()
    {

    }
    public DailyPhoto(String clientGuid, byte[] photoBytes, String  agent, String  device_id, String dateClosed, String docGuid, double latitude, double longitude) {
        this.clientGuid = clientGuid;
        this.photoBytes = photoBytes;
        this.agent = agent;
        this.device_id = device_id;
        this.dateClosed = dateClosed;
        this.docGuid = docGuid;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public byte[] getPhotoBytes() {
        return photoBytes;
    }

    public void setPhotoBytes(byte[] photoBytes) {
        this.photoBytes = photoBytes;
    }

    public String getClientGuid() {
        return clientGuid;
    }

    public void setClientGuid(String guid) {
        this.clientGuid = guid;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(String dateClosed) {
        this.dateClosed = dateClosed;
    }

    public String getDocGuid() {
        return docGuid;
    }

    public void setDocGuid(String docGuid) {
        this.docGuid = docGuid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
