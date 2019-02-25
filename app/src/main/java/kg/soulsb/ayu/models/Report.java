package kg.soulsb.ayu.models;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class Report {

    public static final String TABLE  = "reports";
    public static final String TABLE_SAVED  = "savedreports";

    public static String KEY_ReportId   = "reportId";
    public static String KEY_Guid     = "Guid";
    public static String KEY_Name     = "Name";
    public static String KEY_Datestart     = "Datestart";
    public static String KEY_Dateend     = "Dateend";
    public static String KEY_Contenthtml     = "Contenthtml";
    public static String KEY_Base     = "Base";


    private String reportId;
    private String guid;
    private String name;
    private String dateStart;
    private String dateEnd;
    private String contentHTML;
    private String base;

    public Report()
    {

    }
    public Report(String guid, String name) {
        this.guid = guid;
        this.name = name;

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

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getContentHTML() {
        return contentHTML;
    }

    public void setContentHTML(String contentHTML) {
        this.contentHTML = contentHTML;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getBase() {
        return base;
    }
}
