package kg.soulsb.ayu.models;
import java.io.Serializable;
import java.util.ArrayList;

import kg.soulsb.ayu.helpers.repo.ClientsRepo;

/**
 * Created by Sultanbek Baibagyshev on 1/30/17.
 */

public class Order implements Serializable {


    public static final String TABLE  = "SavedOrders";
    public static final String TABLE_ITEM  = "SavedItems";

    public static String KEY_OrderID   = "OrderId";
    public static String KEY_BAZA   = "baza";
    public static String KEY_clientGUID     = "clientGUID";
    public static String KEY_Doctype     = "Doctype";
    public static String KEY_pricetype     = "pricetype";
    public static String KEY_warehouse     = "warehouse";
    public static String KEY_dogovor     = "dogovor";
    public static String KEY_date     = "date";
    public static String KEY_dateSend     = "dateSend";
    public static String KEY_comment     = "comment";
    public static String KEY_isDelivered     = "isDelivered";
    public static String KEY_totalSum     = "totalSum";
    public static String KEY_Organization = "Organization";

    private String orderID;
    private String priceType;
    private String warehouse;
    private String dogovor;
    private String client;
    private ArrayList<Item> arraylistTovar;
    private String date;
    private String comment;
    private String dateSend;
    private String doctype;
    private String organization;
    private double totalSum;
    private boolean isDelivered;
    private Baza baza;

    public Order()
    {

    }
    public Order(String date, String client, String dogovor, String warehouse, String priceType, ArrayList<Item> arraylistTovar)
    {
        this.date = date;
        this.client = client;
        this.dogovor = dogovor;
        this.warehouse = warehouse;
        this.priceType = priceType;
        this.arraylistTovar = arraylistTovar;
    }


    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getDogovor() {
        return dogovor;
    }

    public void setDogovor(String dogovor) {
        this.dogovor = dogovor;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public ArrayList<Item> getArraylistTovar() {
        return arraylistTovar;
    }

    public void setArraylistTovar(ArrayList<Item> arraylistTovar) {
        this.arraylistTovar = arraylistTovar;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateSend() {
        return dateSend;
    }

    public void setDateSend(String dateSend) {
        this.dateSend = dateSend;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public double getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(double totalSum) {
        this.totalSum = totalSum;
    }

    public String getBaza() {
        return baza.getName();
    }

    public void setBaza(Baza baza) {
        this.baza = baza;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
