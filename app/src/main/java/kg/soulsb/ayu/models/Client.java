package kg.soulsb.ayu.models;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class Client {
    public static final String TABLE = "clients";

    public static String KEY_ClientId = "ClientId";
    public static String KEY_Guid = "Guid";
    public static String KEY_Name = "Name";
    public static String KEY_Address = "Address";
    public static String KEY_Phone= "Phone";
    public static String KEY_Latitude = "Latitude";
    public static String KEY_Longitude= "Longitude";
    public static String KEY_Debt= "Debt";
    public static String KEY_Base= "Base";

    private String clientId;
    private String guid;
    private String name;
    private String address;
    private String phone;
    private String latitude;
    private String longitude;
    private String base;
    private double debt;

    public Client(String guid, String name, String address, String phone, String latitude, String longitude, double debt) {
        this.guid = guid;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.debt = debt;
    }

    public Client() {
    }

    public String getName() {
        return name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return getName().toLowerCase();
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public double getDebt() {
        return debt;
    }

    public void setDebt(double debt) {
        this.debt = debt;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }
}
