package kg.soulsb.ayu.models;

import android.location.Location;

import java.io.Serializable;

import kg.soulsb.ayu.helpers.repo.ClientsRepo;

public class DailyTask implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String TABLE = "dailytasks";
    public static final String KEY_base = "base";

    public static String KEY_dailyTaskId = "dailytaskid";
    public static String KEY_docGuid = "docguid";
    public static String KEY_clientGuid = "clientguid";
    public static String KEY_priority = "priority";
    public static String KEY_status = "status";
    public static String KEY_docid = "docid";
    public static String KEY_docdate = "docdate";
    public static String KEY_photo = "photo";
    public static String KEY_dateClosed = "dateclosed";
    public static String KEY_latitude = "latitude";
    public static String KEY_longitude = "longitude";
    public static String KEY_agentName = "agentname";

    private String docGuid;
    private String clientGuid;
    private int priority;
    private String status;
    private String docId;
    private String docDate;
    private String photo;
    private String dateClosed;
    private String latitude;
    private String longitude;
    private String agentName;
    private String base;

    public DailyTask()
    {

    }

    public DailyTask(String docGuid, String clientGuid, int priority, String status, String docId, String docDate, String dateClosed, String latitude, String longitude, String agentName, String base)
    {
        this.docGuid = docGuid;
        this.clientGuid = clientGuid;
        this.priority = priority;
        this.status = status;
        this.docId = docId;
        this.docDate = docDate;
        this.dateClosed = dateClosed;
        this.latitude = latitude;
        this.longitude = longitude;
        this.agentName = agentName;
        this.base = base;

    }

    public String getClientName(){
        Client client = new ClientsRepo().getClientObjectByGuid(clientGuid);
        return client.getName();
    }

    public Location getClientLocation(){
        Client client = new ClientsRepo().getClientObjectByGuid(clientGuid);
        Location location = new Location("loc");
        location.setLatitude(Double.parseDouble(client.getLatitude()));
        location.setLongitude(Double.parseDouble(client.getLongitude()));
        return location;
    }
    public String getDocGuid() {
        return docGuid;
    }

    public void setDocGuid(String docGuid) {
        this.docGuid = docGuid;
    }

    public String getClientGuid() {
        return clientGuid;
    }

    public void setClientGuid(String clientGuid) {
        this.clientGuid = clientGuid;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocDate() {
        return docDate;
    }

    public void setDocDate(String docDate) {
        this.docDate = docDate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(String dateClosed) {
        this.dateClosed = dateClosed;
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

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    @Override
    public String toString() {
        return Integer.toString(getPriority())+". Заказ: "+getClientName();
    }
}
