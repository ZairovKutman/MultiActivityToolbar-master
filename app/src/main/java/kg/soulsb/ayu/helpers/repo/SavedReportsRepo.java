package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Report;
import kg.soulsb.ayu.models.Warehouse;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class SavedReportsRepo {

    public Report report;

    public SavedReportsRepo() {
        report = new Report();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + Report.TABLE_SAVED  + " ("
                + Report.KEY_ReportId  + "   PRIMARY KEY    ,"
                + Report.KEY_Guid  + "   TEXT    ,"
                + Report.KEY_Name  + "   TEXT    ,"
                + Report.KEY_Base  + "   TEXT    ,"
                + Report.KEY_Datestart  + "   TEXT    ,"
                + Report.KEY_Dateend  + "   TEXT    ,"
                + Report.KEY_Contenthtml  + "   TEXT);";
    }

    public int insert(Report report) {
        int reportId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Report.KEY_Guid, report.getGuid());
        values.put(Report.KEY_Name, report.getName());
        values.put(Report.KEY_Base, report.getBase());
        values.put(Report.KEY_Datestart, report.getDateStart());
        values.put(Report.KEY_Dateend, report.getDateEnd());
        values.put(Report.KEY_Contenthtml, report.getContentHTML());

        // Inserting Row
        reportId=(int)db.insert(Report.TABLE_SAVED, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return reportId;
    }


    public void deleteTable() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Report.TABLE_SAVED,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public void delete(Report report)
    {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        String whereClause = Report.KEY_Base+" = '"+ CurrentBaseClass.getInstance().getCurrentBase()+"' AND "+Report.KEY_Guid+" = '"+report.getGuid()+"' AND "+Report.KEY_Datestart+" = '"+report.getDateStart()+"' AND "+Report.KEY_Dateend+" = '"+report.getDateEnd()+"'";
        db.delete(Report.TABLE_SAVED,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<Report> getReportsObject() {
        ArrayList<Report> arrayList = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Report.KEY_Name
                + ", "+Report.KEY_ReportId
                + ", "+Report.KEY_Guid
                + ", "+Report.KEY_Base
                + ", "+Report.KEY_Datestart
                + ", "+Report.KEY_Dateend
                + ", "+Report.KEY_Contenthtml
                + " FROM " + Report.TABLE_SAVED
                + " WHERE "+ Report.KEY_Base + " = '"+ CurrentBaseClass.getInstance().getCurrentBase()+"'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Report report = new Report();
                report.setReportId(cursor.getString(cursor.getColumnIndexOrThrow(Report.KEY_ReportId)));
                report.setGuid(cursor.getString(cursor.getColumnIndexOrThrow(Report.KEY_Guid)));
                report.setBase(cursor.getString(cursor.getColumnIndexOrThrow(Report.KEY_Base)));
                report.setName(cursor.getString(cursor.getColumnIndexOrThrow(Report.KEY_Name)));
                report.setDateStart(cursor.getString(cursor.getColumnIndexOrThrow(Report.KEY_Datestart)));
                report.setDateEnd(cursor.getString(cursor.getColumnIndexOrThrow(Report.KEY_Dateend)));
                report.setContentHTML(cursor.getString(cursor.getColumnIndexOrThrow(Report.KEY_Contenthtml)));
                arrayList.add(report);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return arrayList;
    }

    public void deleteByBase(String bazaString)
    {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        String whereClause = Report.KEY_Base+" = '"+bazaString+"'";
        db.delete(Report.TABLE_SAVED,whereClause,null);
        DatabaseManager.getInstance().closeDatabase();
    }
}
