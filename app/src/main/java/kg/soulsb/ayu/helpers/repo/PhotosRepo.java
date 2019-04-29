package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.DailyPhoto;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class PhotosRepo {

    public DailyPhoto dailyPhoto;
    Cursor cursor;
    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
    public PhotosRepo() {
        dailyPhoto = new DailyPhoto();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + DailyPhoto.TABLE  + " ("
                + DailyPhoto.KEY_PhotoId  + "   PRIMARY KEY    ,"
                + DailyPhoto.KEY_clientGuid  + "   TEXT    ,"
                + DailyPhoto.KEY_photoBytes  + "   blob);";
    }

    public int insert(DailyPhoto dailyPhoto) {
        int dailyPhotoId;
        db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(DailyPhoto.KEY_clientGuid, dailyPhoto.getGuid());
        values.put(DailyPhoto.KEY_photoBytes, dailyPhoto.getPhotoBytes());

        // Inserting Row
        if (db.isOpen()) {
            dailyPhotoId=(int)db.insert(DailyPhoto.TABLE, null, values);
        }
        else
        {
            db = DatabaseManager.getInstance().openDatabase();
            dailyPhotoId=(int)db.insert(DailyPhoto.TABLE, null, values);
        }

        DatabaseManager.getInstance().closeDatabase();

        return dailyPhotoId;
    }

    public void deleteDailyPhoto(byte[] photoBytes)
    {
        db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        db.execSQL("DELETE FROM "+DailyPhoto.TABLE+ " WHERE "+DailyPhoto.KEY_photoBytes + " = ?",new Object[]{ photoBytes});
        DatabaseManager.getInstance().closeDatabase();
    }

    public void deleteAllDailyPhoto()
    {
        db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        db.execSQL("DELETE FROM "+DailyPhoto.TABLE);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<DailyPhoto> getPhotosByClientGuid(String clientGuid) {
        ArrayList<DailyPhoto> dailyPhotos = new ArrayList<>();
        DailyPhoto dailyPhoto = new DailyPhoto();
        db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + DailyPhoto.KEY_clientGuid
                + ", "+DailyPhoto.KEY_photoBytes
                + " FROM " + DailyPhoto.TABLE
                + " WHERE "+DailyPhoto.KEY_clientGuid + " = '"+clientGuid + "'";

        if (db.isOpen()) {
            cursor = db.rawQuery(selectQuery, null);
        }
        else
        {
            db = DatabaseManager.getInstance().openDatabase();
            cursor = db.rawQuery(selectQuery, null);
        }
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                DailyPhoto dailyPhoto1 = new DailyPhoto();
                dailyPhoto1.setGuid(cursor.getString(cursor.getColumnIndexOrThrow(DailyPhoto.KEY_clientGuid)));
                dailyPhoto1.setPhotoBytes(cursor.getBlob(cursor.getColumnIndexOrThrow(DailyPhoto.KEY_photoBytes)));
                dailyPhotos.add(dailyPhoto1);

            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed())
        {
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();

        return dailyPhotos;
    }


    public void deleteTable() {
        db = DatabaseManager.getInstance().openDatabase();
        db.delete(DailyPhoto.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }
}
