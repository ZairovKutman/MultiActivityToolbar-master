package kg.soulsb.ayu.models;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class DailyPhoto {

    public static final String TABLE  = "photos";

    public static String KEY_PhotoId   = "photoId";
    public static String KEY_clientGuid     = "Guid";
    public static String KEY_photoBytes     = "PhotoBytes";

    private String photoId;
    private String guid;
    private byte[] photoBytes;

    public DailyPhoto()
    {

    }
    public DailyPhoto(String clientGuid, byte[] photoBytes) {
        this.guid = clientGuid;
        this.photoBytes = photoBytes;

    }

    public byte[] getPhotoBytes() {
        return photoBytes;
    }

    public void setPhotoBytes(byte[] photoBytes) {
        this.photoBytes = photoBytes;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
