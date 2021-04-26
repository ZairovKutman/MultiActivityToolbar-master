package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.models.SvodPay;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class OrdersRepo {

    public Order order;
    Cursor cursor;
    public OrdersRepo() {
        order = new Order();
    }
    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + Order.TABLE  + " ("
                + Order.KEY_OrderID  + "   PRIMARY KEY    ,"
                + Order.KEY_comment  + "   TEXT    ,"
                + Order.KEY_checkedBonusTT  + "   TEXT    ,"
                + Order.KEY_dateSend  + "   TEXT    ,"
                + Order.KEY_totalSum  + "   TEXT    ,"
                + Order.KEY_Doctype  + "   TEXT    ,"
                + Order.KEY_dogovor  + "   TEXT    ,"
                + Order.KEY_isDelivered  + "   TEXT    ,"
                + Order.KEY_clientGUID  + "   TEXT    ,"
                + Order.KEY_pricetype  + "   TEXT    ,"
                + Order.KEY_warehouse  + "   TEXT    ,"
                + Order.KEY_BAZA  + "   TEXT    ,"
                + Order.KEY_Organization  + "   TEXT    ,"
                + Order.KEY_isTask  + "   TEXT    ,"
                + Order.KEY_date  + "   TEXT);";

    }

    public static String createItemTable(){

        return "CREATE TABLE IF NOT EXISTS " + Order.TABLE_ITEM  + " ("
                + Item.KEY_ItemId + "   PRIMARY KEY    ,"
                + Item.KEY_Guid  + "   TEXT    ,"
                + Item.KEY_Unit  + "   TEXT    ,"
                + Item.KEY_UnitGUID  + "   TEXT    ,"
                + Item.KEY_isDelivered  + "   TEXT    ,"
                + Item.KEY_Name  + "   TEXT    ,"
                + Item.KEY_Price  + "   TEXT    ,"
                + Item.KEY_Quantity  + "   TEXT    ,"
                + Order.KEY_warehouse  + "   TEXT    ,"
                + Item.KEY_Sum  + "   TEXT    ,"
                + Order.KEY_OrderID  + "   TEXT);";
    }

    public static String createSvodPayTable(){

        return "CREATE TABLE IF NOT EXISTS " + Order.TABLE_SvodPay  + " ("
                + SvodPay.KEY_ID + "   INTEGER PRIMARY KEY AUTOINCREMENT   ,"
                + SvodPay.KEY_Guid_Client + "   TEXT    ,"
                + SvodPay.KEY_Guid_Dogovor  + "   TEXT    ,"
                + SvodPay.KEY_isDelivered  + "   TEXT    ,"
                + SvodPay.KEY_Sum  + "   TEXT    ,"
                + Order.KEY_OrderID  + "   TEXT);";
    }

    public void delete(Order order)
    {
        db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        String whereClause = Order.KEY_OrderID+" = '"+order.getOrderID()+"'";

        db.delete(Order.TABLE,whereClause,null);
        db.delete(Order.TABLE_ITEM,whereClause,null);
        db.delete(Order.TABLE_SvodPay,whereClause,null);

        DatabaseManager.getInstance().closeDatabase();
    }


    public int insert(Order order) {
        int orderId;
        db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Order.KEY_OrderID, order.getOrderID());
        values.put(Order.KEY_BAZA, order.getBaza());
        values.put(Order.KEY_clientGUID, order.getClient());
        values.put(Order.KEY_comment, order.getComment());
        values.put(Order.KEY_checkedBonusTT, order.getCheckedBonusTTString());
        values.put(Order.KEY_date, order.getDate());
        values.put(Order.KEY_dateSend, order.getDateSend());
        values.put(Order.KEY_Doctype, order.getDoctype());
        values.put(Order.KEY_dogovor, order.getDogovor());
        values.put(Order.KEY_isDelivered, Boolean.toString(order.isDelivered()));
        values.put(Order.KEY_pricetype, order.getPriceType());
        values.put(Order.KEY_warehouse,order.getWarehouse());
        values.put(Order.KEY_totalSum,order.getTotalSum());
        values.put(Order.KEY_Organization,order.getOrganization());
        values.put(Order.KEY_isTask,order.isTask());

        // Inserting Row
        orderId = (int) db.insert(Order.TABLE, null, values);

        if (!order.getDoctype().equals("2") && !order.getDoctype().equals("3")) {
            for (Item item : order.getArraylistTovar()) {
                values = new ContentValues();
                values.put(Order.KEY_OrderID, order.getOrderID());
                values.put(Item.KEY_Guid, item.getGuid());
                values.put(Item.KEY_isDelivered, Boolean.toString(order.isDelivered()));
                values.put(Item.KEY_Name, item.getName());
                values.put(Item.KEY_Price, item.getPrice());
                values.put(Item.KEY_Quantity, item.getQuantity());
                values.put(Item.KEY_ItemId, item.getItemId());
                values.put(Item.KEY_Sum, item.getSum());
                values.put(Order.KEY_warehouse,order.getWarehouse());
                values.put(Item.KEY_UnitGUID,item.getMyUnit().getUnitGuid());
                // Inserting Row
                orderId = (int) db.insert(Order.TABLE_ITEM, null, values);
            }

            DatabaseManager.getInstance().closeDatabase();
        }

        if (order.getDoctype().equals("3")) {
            for (SvodPay svodPay : order.getArraylistSvodPay()) {
                values = new ContentValues();
                values.put(Order.KEY_OrderID, order.getOrderID());
                values.put(SvodPay.KEY_Guid_Client, svodPay.getClient());
                values.put(SvodPay.KEY_isDelivered, Boolean.toString(order.isDelivered()));
                values.put(SvodPay.KEY_Guid_Dogovor, svodPay.getDogovor());
                values.put(SvodPay.KEY_Sum, svodPay.getSum());
                // Inserting Row
                orderId = (int) db.insert(Order.TABLE_SvodPay, null, values);
            }

            DatabaseManager.getInstance().closeDatabase();
        }

        return orderId;
    }

    public void deleteTable() {
        db = DatabaseManager.getInstance().openDatabase();
        db.delete(Order.TABLE,null,null);
        db.delete(Order.TABLE_ITEM,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<Order> getOrdersObject(String baza) {
        ArrayList<Order> arrayList = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Order.KEY_OrderID
                + ", "+Order.KEY_BAZA
                + ", "+Order.KEY_clientGUID
                + ", "+Order.KEY_comment
                + ", "+Order.KEY_checkedBonusTT
                + ", "+Order.KEY_date
                + ", "+Order.KEY_dateSend
                + ", "+Order.KEY_Doctype
                + ", "+Order.KEY_dogovor
                + ", "+Order.KEY_isDelivered
                + ", "+Order.KEY_pricetype
                + ", "+Order.KEY_warehouse
                + ", "+Order.KEY_totalSum
                + ", "+Order.KEY_Organization
                + ", "+Order.KEY_isTask
                + " FROM " + Order.TABLE
                + " WHERE "+Order.KEY_BAZA+" = '"+baza+"'";

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
                Order order = new Order();
                order.setOrderID(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_OrderID)));
                order.setOrganization(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_Organization)));
                order.setTotalSum(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_totalSum))));
                order.setClient(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_clientGUID)));
                order.setComment(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_comment)));
                order.setCheckedBonusTT(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_checkedBonusTT)));
                order.setDate(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_date)));
                order.setDateSend(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_dateSend)));
                order.setDoctype(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_Doctype)));
                order.setDogovor(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_dogovor)));
                order.setPriceType(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_pricetype)));
                order.setWarehouse(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_warehouse)));
                order.setTask(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_isTask)));
                if (cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_isDelivered)).equals("true"))
                    order.setDelivered(true);
                else
                    order.setDelivered(false);
                ArrayList<Item> itemArrayList = new ArrayList<>();

                String selectItemQuery =  " SELECT " + Order.KEY_OrderID
                        + ", "+Item.KEY_Guid
                        + ", "+Item.KEY_Name
                        + ", "+Item.KEY_Price
                        + ", "+Item.KEY_Quantity
                        + ", "+Item.KEY_ItemId
                        + ", "+Item.KEY_Sum
                        + ", "+Item.KEY_Unit
                        + ", "+Item.KEY_UnitGUID
                        + " FROM " + Order.TABLE_ITEM
                        + " WHERE "+Order.KEY_OrderID+" = '"+cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_OrderID))+"'";

                Cursor myCursor = db.rawQuery(selectItemQuery, null);

                if (myCursor.moveToFirst()) {
                    do {
                        Item item = new Item();
                        item.setItemId(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_ItemId)));
                        item.setName(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_Name)));
                        item.setGuid(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_Guid)));
                        item.setPrice(Double.parseDouble(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_Price))));
                        item.setSum(Double.parseDouble(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_Sum))));
                        item.setQuantity(Integer.parseInt(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_Quantity))));
                        item.setMyUnitByGuid(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_UnitGUID)));
                        itemArrayList.add(item);
                    } while (myCursor.moveToNext());
                }
                myCursor.close();

                order.setArraylistTovar(itemArrayList);

                ////////

                ArrayList<SvodPay> svodPayArrayList = new ArrayList<>();

                String selectSvodPayQuery =  " SELECT " + Order.KEY_OrderID
                        + ", "+SvodPay.KEY_Guid_Client
                        + ", "+SvodPay.KEY_Guid_Dogovor
                        + ", "+SvodPay.KEY_Sum
                        + " FROM " + Order.TABLE_SvodPay
                        + " WHERE "+Order.KEY_OrderID+" = '"+cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_OrderID))+"'";

                Cursor myCursor1 = db.rawQuery(selectSvodPayQuery, null);

                if (myCursor1.moveToFirst()) {
                    do {
                        SvodPay svodPay = new SvodPay();
                        svodPay.setGuid_client(myCursor1.getString(myCursor1.getColumnIndexOrThrow(SvodPay.KEY_Guid_Client)));
                        svodPay.setGuid_dogovor(myCursor1.getString(myCursor1.getColumnIndexOrThrow(SvodPay.KEY_Guid_Dogovor)));
                        svodPay.setSum(Double.parseDouble(myCursor1.getString(myCursor1.getColumnIndexOrThrow(SvodPay.KEY_Sum))));
                        svodPayArrayList.add(svodPay);
                    } while (myCursor1.moveToNext());
                }
                myCursor1.close();
                order.setArraylistSvodPay(svodPayArrayList);



                arrayList.add(order);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return arrayList;
    }

    public ArrayList<Order> getOrdersObjectByClientGuid(String baza, String clientGuid) {
        ArrayList<Order> arrayList = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Order.KEY_OrderID
                + ", "+Order.KEY_BAZA
                + ", "+Order.KEY_clientGUID
                + ", "+Order.KEY_comment
                + ", "+Order.KEY_checkedBonusTT
                + ", "+Order.KEY_date
                + ", "+Order.KEY_dateSend
                + ", "+Order.KEY_Doctype
                + ", "+Order.KEY_dogovor
                + ", "+Order.KEY_isDelivered
                + ", "+Order.KEY_pricetype
                + ", "+Order.KEY_warehouse
                + ", "+Order.KEY_totalSum
                + ", "+Order.KEY_Organization
                + ", "+Order.KEY_isTask
                + " FROM " + Order.TABLE
                + " WHERE "+Order.KEY_BAZA+" = '"+baza+"' AND "+Order.KEY_clientGUID+" = '"+clientGuid+"' ";

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
                Order order = new Order();
                order.setOrderID(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_OrderID)));
                order.setTask(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_isTask)));
                order.setOrganization(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_Organization)));
                order.setTotalSum(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_totalSum))));
                order.setClient(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_clientGUID)));
                order.setComment(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_comment)));
                order.setCheckedBonusTT(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_checkedBonusTT)));
                order.setDate(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_date)));
                order.setDateSend(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_dateSend)));
                order.setDoctype(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_Doctype)));
                order.setDogovor(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_dogovor)));
                order.setPriceType(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_pricetype)));
                order.setWarehouse(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_warehouse)));
                if (cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_isDelivered)).equals("true"))
                    order.setDelivered(true);
                else
                    order.setDelivered(false);
                ArrayList<Item> itemArrayList = new ArrayList<>();

                String selectItemQuery =  " SELECT " + Order.KEY_OrderID
                        + ", "+Item.KEY_Guid
                        + ", "+Item.KEY_Name
                        + ", "+Item.KEY_Price
                        + ", "+Item.KEY_Quantity
                        + ", "+Item.KEY_ItemId
                        + ", "+Item.KEY_Sum
                        + ", "+Item.KEY_Unit
                        + ", "+Item.KEY_UnitGUID
                        + " FROM " + Order.TABLE_ITEM
                        + " WHERE "+Order.KEY_OrderID+" = '"+cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_OrderID))+"'";

                Cursor myCursor = db.rawQuery(selectItemQuery, null);

                if (myCursor.moveToFirst()) {
                    do {
                        Item item = new Item();
                        item.setItemId(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_ItemId)));
                        item.setName(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_Name)));
                        item.setGuid(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_Guid)));
                        item.setPrice(Double.parseDouble(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_Price))));
                        item.setSum(Double.parseDouble(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_Sum))));
                        item.setQuantity(Integer.parseInt(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_Quantity))));
                        item.setMyUnitByGuid(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_UnitGUID)));
                        itemArrayList.add(item);
                    } while (myCursor.moveToNext());
                }
                myCursor.close();

                order.setArraylistTovar(itemArrayList);

                ////////

                ArrayList<SvodPay> svodPayArrayList = new ArrayList<>();

                String selectSvodPayQuery =  " SELECT " + Order.KEY_OrderID
                        + ", "+SvodPay.KEY_Guid_Client
                        + ", "+SvodPay.KEY_Guid_Dogovor
                        + ", "+SvodPay.KEY_Sum
                        + " FROM " + Order.TABLE_SvodPay
                        + " WHERE "+Order.KEY_OrderID+" = '"+cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_OrderID))+"'";

                Cursor myCursor1 = db.rawQuery(selectSvodPayQuery, null);

                if (myCursor1.moveToFirst()) {
                    do {
                        SvodPay svodPay = new SvodPay();
                        svodPay.setGuid_client(myCursor1.getString(myCursor1.getColumnIndexOrThrow(SvodPay.KEY_Guid_Client)));
                        svodPay.setGuid_dogovor(myCursor1.getString(myCursor1.getColumnIndexOrThrow(SvodPay.KEY_Guid_Dogovor)));
                        svodPay.setSum(Double.parseDouble(myCursor1.getString(myCursor1.getColumnIndexOrThrow(SvodPay.KEY_Sum))));
                        svodPayArrayList.add(svodPay);
                    } while (myCursor1.moveToNext());
                }
                myCursor1.close();
                order.setArraylistSvodPay(svodPayArrayList);



                arrayList.add(order);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return arrayList;
    }

    public void setDocDelivered(String orderID, boolean b) {
            db = DatabaseManager.getInstance().openDatabase();
            ContentValues values = new ContentValues();
            ContentValues values2 = new ContentValues();
            ContentValues values3 = new ContentValues();

            values.put(Order.KEY_isDelivered, Boolean.toString(b));
            values2.put(Item.KEY_isDelivered, Boolean.toString(b));
            values3.put(SvodPay.KEY_isDelivered, Boolean.toString(b));

            String whereClause = Order.KEY_OrderID + " = '"+orderID+"'";
            String whereClause2 = Order.KEY_OrderID + " = '"+orderID+"'";
            String whereClause3 = Order.KEY_OrderID + " = '"+orderID+"'";
            // Inserting Row
            db.update(Order.TABLE, values, whereClause, null);
            db.update(Order.TABLE_ITEM, values2, whereClause2, null);
            db.update(Order.TABLE_SvodPay, values3, whereClause3, null);

            DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<Order> getOrdersObjectNotDelivered(String baza) {
        ArrayList<Order> arrayList = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Order.KEY_OrderID
                + ", "+Order.KEY_BAZA
                + ", "+Order.KEY_clientGUID
                + ", "+Order.KEY_comment
                + ", "+Order.KEY_checkedBonusTT
                + ", "+Order.KEY_date
                + ", "+Order.KEY_dateSend
                + ", "+Order.KEY_Doctype
                + ", "+Order.KEY_dogovor
                + ", "+Order.KEY_isDelivered
                + ", "+Order.KEY_pricetype
                + ", "+Order.KEY_warehouse
                + ", "+Order.KEY_totalSum
                + ", "+Order.KEY_Organization
                + ", "+Order.KEY_isTask
                + " FROM " + Order.TABLE
                + " WHERE "+Order.KEY_BAZA+" = '"+baza+"' AND "+Order.KEY_isDelivered+" = 'false'";

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
                Order order = new Order();
                order.setOrderID(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_OrderID)));
                order.setTask(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_isTask)));
                order.setOrganization(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_Organization)));
                order.setTotalSum(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_totalSum))));
                order.setClient(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_clientGUID)));
                order.setComment(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_comment)));
                order.setCheckedBonusTT(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_checkedBonusTT)));
                order.setDate(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_date)));
                order.setDateSend(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_dateSend)));
                order.setDoctype(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_Doctype)));
                order.setDogovor(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_dogovor)));
                order.setPriceType(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_pricetype)));
                order.setWarehouse(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_warehouse)));
                if (cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_isDelivered)).equals("true"))
                    order.setDelivered(true);
                else
                    order.setDelivered(false);
                ArrayList<Item> itemArrayList = new ArrayList<>();

                String selectItemQuery =  " SELECT " + Order.KEY_OrderID
                        + ", "+Item.KEY_Guid
                        + ", "+Item.KEY_Name
                        + ", "+Item.KEY_Price
                        + ", "+Item.KEY_Quantity
                        + ", "+Item.KEY_ItemId
                        + ", "+Item.KEY_Unit
                        + ", "+Item.KEY_UnitGUID
                        + ", "+Item.KEY_Sum
                        + " FROM " + Order.TABLE_ITEM
                        + " WHERE "+Order.KEY_OrderID+" = '"+cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_OrderID))+"'";

                Cursor myCursor = db.rawQuery(selectItemQuery, null);

                if (myCursor.moveToFirst()) {
                    do {
                        Item item = new Item();
                        item.setItemId(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_ItemId)));
                        item.setName(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_Name)));
                        item.setGuid(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_Guid)));
                        item.setPrice(Double.parseDouble(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_Price))));
                        item.setSum(Double.parseDouble(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_Sum))));
                        item.setQuantity(Integer.parseInt(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_Quantity))));
                        item.setMyUnitByGuid(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_UnitGUID)));
                        itemArrayList.add(item);
                    } while (myCursor.moveToNext());
                }
                myCursor.close();
                order.setArraylistTovar(itemArrayList);
                ////////

                ArrayList<SvodPay> svodPayArrayList = new ArrayList<>();

                String selectSvodPayQuery =  " SELECT " + Order.KEY_OrderID
                        + ", "+SvodPay.KEY_Guid_Client
                        + ", "+SvodPay.KEY_Guid_Dogovor
                        + ", "+SvodPay.KEY_Sum
                        + " FROM " + Order.TABLE_SvodPay
                        + " WHERE "+Order.KEY_OrderID+" = '"+cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_OrderID))+"'";

                Cursor myCursor1 = db.rawQuery(selectSvodPayQuery, null);

                if (myCursor1.moveToFirst()) {
                    do {
                        SvodPay svodPay = new SvodPay();
                        svodPay.setGuid_client(myCursor1.getString(myCursor1.getColumnIndexOrThrow(SvodPay.KEY_Guid_Client)));
                        svodPay.setGuid_dogovor(myCursor1.getString(myCursor1.getColumnIndexOrThrow(SvodPay.KEY_Guid_Dogovor)));
                        svodPay.setSum(Double.parseDouble(myCursor1.getString(myCursor1.getColumnIndexOrThrow(SvodPay.KEY_Sum))));
                        svodPayArrayList.add(svodPay);
                    } while (myCursor1.moveToNext());
                }
                myCursor1.close();
                order.setArraylistSvodPay(svodPayArrayList);

                arrayList.add(order);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return arrayList;
    }

    public void deleteDocDelivered() {
        db = DatabaseManager.getInstance().openDatabase();
        String whereClause = Order.KEY_isDelivered + " = '"+"true"+"' AND "+Order.KEY_BAZA+" = '"+CurrentBaseClass.getInstance().getCurrentBase()+"'";
        String whereClause2 = Item.KEY_isDelivered + " = '"+"true'";
        String whereClause3 = SvodPay.KEY_isDelivered + " = '"+"true'";

        db.delete(Order.TABLE,whereClause,null);
        db.delete(Order.TABLE_ITEM,whereClause2,null);
        db.delete(Order.TABLE_SvodPay,whereClause3,null);

        db.close();
    }
}