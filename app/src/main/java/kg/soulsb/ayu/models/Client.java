package kg.soulsb.ayu.models;

import android.location.Location;

import kg.soulsb.ayu.singletons.CurrentLocationClass;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class Client implements Comparable {
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
    public static String KEY_Oborot= "Oborot";

    private String clientId;
    private String guid;
    private String name;
    private String address;
    private String phone;
    private String latitude;
    private String longitude;
    private String base;
    private double debt;
    private String oborot;
    private Location loc;
    private Location locOfAgent = new Location("locOfAgent");

    public Client(String guid, String name, String address, String phone, String latitude, String longitude, double debt, String oborot) {
        this.guid = guid;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.debt = debt;
        this.loc = new Location("loc");
        this.oborot = oborot;

        if (!latitude.isEmpty())
            this.loc.setLatitude(Double.parseDouble(latitude));
        else this.loc.setLatitude(0);

        if (!longitude.isEmpty()){
            this.loc.setLongitude(Double.parseDouble(longitude));}
        else
            this.loc.setLongitude(0);

    }

    public Client() {
        this.loc = new Location("loc");
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

    public void setLatitudeAgent(String latitude) {
        this.locOfAgent.setLatitude(Double.parseDouble(latitude));
    }

    public void setLongitudeAgent(String longitude) {
        this.locOfAgent.setLongitude(Double.parseDouble(longitude));
    }

    public void setLatitude(String latitude) {

        if (!latitude.isEmpty()) {
            this.loc.setLatitude(Double.parseDouble(latitude));
            this.latitude = latitude;
        }
        else {this.loc.setLatitude(0); this.latitude = "0";}

    }

    public void setLongitude(String longitude) {

        if (!longitude.isEmpty()){
            this.loc.setLongitude(Double.parseDouble(longitude));
            this.longitude = longitude;}
        else {
            this.loc.setLongitude(0);
            this.longitude = "0";
        }
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

    public int getDistanceToClient()
    {
        if (loc.getLatitude()!=0)
            return (int)(loc.distanceTo(locOfAgent));
        else return 0;
    }

    @Override
    public int compareTo(Object o) {
        if (locOfAgent.getLatitude() !=0 && locOfAgent.getLongitude() !=0) {
            return Math.abs((int) (this.loc.distanceTo(locOfAgent))) - Math.abs((int) ((Client) o).loc.distanceTo(((Client) o).locOfAgent));
        }
        else return 0;
    }

    public void setLocOfAgent(Double latitude,Double longitude) {
        this.locOfAgent.setLatitude(latitude);
        this.locOfAgent.setLongitude(longitude);
    }

    public String getLocOfAgentText() {
        if (locOfAgent.getLatitude() == 0 ) return "";
        if (!latitude.equals("0") && !longitude.equals("0")){
            loc.setLatitude(Double.parseDouble(latitude));
            loc.setLongitude(Double.parseDouble(longitude));
            locOfAgent.setLatitude(CurrentLocationClass.getInstance().getCurrentLocation().getLatitude());
            locOfAgent.setLongitude(CurrentLocationClass.getInstance().getCurrentLocation().getLongitude());
            return (int)(this.loc.distanceTo(locOfAgent))+"м.";}
        else
            return "нет крднт.";
    }

    public String getOborot() {
        return oborot;
    }

    public void setOborot(String oborot) {
        this.oborot = oborot;
    }
}
