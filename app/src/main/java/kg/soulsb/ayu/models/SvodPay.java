package kg.soulsb.ayu.models;

import java.io.Serializable;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class SvodPay implements Serializable {

    public static final String TABLE  = "svodPays";
    public static final String KEY_ID = "keyID";

    public static String KEY_Sum   = "sum";
    public static String KEY_Guid_Client     = "client";
    public static String KEY_Guid_Dogovor     = "dogovor";
    public static String KEY_Base    = "base";
    public static String KEY_isDelivered = "isDelivered";

    private String guid_client;
    private String guid_dogovor;
    private double sum;
    private String base;
    private String client_name;

    public SvodPay()
    {
    }
    public SvodPay(String guid_client, String guid_dogovor, double sum) {
        this.guid_client = guid_client;
        this.guid_dogovor = guid_dogovor;
        this.sum = sum;
    }

    public void setGuid_client(String guid_client)
    {
        this.guid_client = guid_client;
    }

    public void setGuid_dogovor(String guid_dogovor)
    {
        this.guid_dogovor = guid_dogovor;
    }

    public String getClient() {
        return guid_client;
    }

    public String getDogovor() {
        return guid_dogovor;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }


    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

}
