package kg.soulsb.ayu.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sultanbek Baibagyshev on 1/17/17.
 */

public class Baza {

    public static final String TABLE  = "bazas";

    public static String KEY_BazaId   = "bazaId";
    public static String KEY_Host     = "host";
    public static String KEY_Name     = "Name";
    public static String KEY_port     = "port";
    public static String KEY_Agent     = "agent";

    private String host;
    private int port;
    private String name;
    private boolean isDefault;
    private String agent;
    private String bazaId;

    public Baza() {
        isDefault = false;
    }

    public Baza(String host, int port, String name, String agent, String bazaId)
    {
        this.host = host;
        this.port = port;
        this.name = name;
        this.agent = agent;
        this.bazaId = bazaId;
        isDefault = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    public void setPort(String port) {this.port = Integer.parseInt(port);}

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getBazaId() {
        return bazaId;
    }

    public void setBazaId(String bazaId) {
        this.bazaId = bazaId;
    }
}
