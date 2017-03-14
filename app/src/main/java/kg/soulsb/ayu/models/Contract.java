package kg.soulsb.ayu.models;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class Contract {

    public static final String TABLE  = "contracts";

    public static String KEY_ContractId   = "ContractId";
    public static String KEY_Guid     = "Guid";
    public static String KEY_Name     = "Name";
    public static String KEY_ClientGuid     = "ClientGuid";
    public static String KEY_Base     = "Base";

    private String contractId;
    private String guid;
    private String name;
    private String clientGuid;
    private String base;

    public Contract()
    {

    }
    public Contract(String guid, String name, String clientGuid) {
        this.guid = guid;
        this.name = name;
        this.clientGuid = clientGuid;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getGuid() {
        return guid;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClientGuid() { return clientGuid;}

    public void setClientGuid(String clientGuid) { this.clientGuid = clientGuid; }

    public void setBase(String base) {
        this.base = base;
    }

    public String getBase() {
        return base;
    }
}
