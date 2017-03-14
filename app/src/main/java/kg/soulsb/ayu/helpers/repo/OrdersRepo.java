package kg.soulsb.ayu.helpers.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.models.Report;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

/**
 * Created by Sultanbek Baibagyshev on 1/24/17.
 */

public class OrdersRepo {

    public Order order;

    public OrdersRepo() {
        order = new Order();
    }

    public static String createTable(){
        return "CREATE TABLE IF NOT EXISTS " + Order.TABLE  + " ("
                + Order.KEY_OrderID  + "   PRIMARY KEY    ,"
                + Order.KEY_comment  + "   TEXT    ,"
                + Order.KEY_dateSend  + "   TEXT    ,"
                + Order.KEY_totalSum  + "   TEXT    ,"
                + Order.KEY_Doctype  + "   TEXT    ,"
                + Order.KEY_dogovor  + "   TEXT    ,"
                + Order.KEY_isDelivered  + "   TEXT    ,"
                + Order.KEY_clientGUID  + "   TEXT    ,"
                + Order.KEY_pricetype  + "   TEXT    ,"
                + Order.KEY_warehouse  + "   TEXT    ,"
                + Order.KEY_BAZA  + "   TEXT    ,"
                + Order.KEY_date  + "   TEXT);";
    }

    public static String createItemTable(){

        return"CREATE TABLE IF NOT EXISTS " + Order.TABLE_ITEM  + " ("
                + Item.KEY_ItemId + "   PRIMARY KEY    ,"
                + Item.KEY_Guid  + "   TEXT    ,"
                + Item.KEY_Name  + "   TEXT    ,"
                + Item.KEY_Price  + "   TEXT    ,"
                + Item.KEY_Quantity  + "   TEXT    ,"
                + Order.KEY_OrderID  + "   TEXT);";
    }

    public void delete(Order order)
    {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        // deleting Row
        String whereClause = Order.KEY_OrderID+" = '"+order.getOrderID()+"'";

        db.delete(Order.TABLE,whereClause,null);
        db.delete(Order.TABLE_ITEM,whereClause,null);

        DatabaseManager.getInstance().closeDatabase();
    }


    public int insert(Order order) {
        int orderId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Order.KEY_OrderID, order.getOrderID());
        values.put(Order.KEY_BAZA, CurrentBaseClass.getInstance().getCurrentBase());
        values.put(Order.KEY_clientGUID, order.getClient());
        values.put(Order.KEY_comment, order.getComment());
        values.put(Order.KEY_date, order.getDate());
        values.put(Order.KEY_dateSend, order.getDateSend());
        values.put(Order.KEY_Doctype, order.getDoctype());
        values.put(Order.KEY_dogovor, order.getDogovor());
        values.put(Order.KEY_isDelivered, Boolean.toString(order.isDelivered()));
        values.put(Order.KEY_pricetype, order.getPriceType());
        values.put(Order.KEY_warehouse,order.getWarehouse());
        values.put(Order.KEY_totalSum,order.getTotalSum());

        // Inserting Row
        orderId=(int)db.insert(Order.TABLE, null, values);

        for (Item item: order.getArraylistTovar()) {
            values = new ContentValues();
            values.put(Order.KEY_OrderID, order.getOrderID());
            values.put(Item.KEY_Guid, item.getGuid());
            values.put(Item.KEY_Name, item.getName());
            values.put(Item.KEY_Price, item.getPrice());
            values.put(Item.KEY_Quantity, item.getQuantity());
            values.put(Item.KEY_ItemId, item.getItemId());
            // Inserting Row
            orderId=(int)db.insert(Order.TABLE_ITEM, null, values);
        }
        DatabaseManager.getInstance().closeDatabase();

        return orderId;
    }

    public void deleteTable() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
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
                + ", "+Order.KEY_date
                + ", "+Order.KEY_dateSend
                + ", "+Order.KEY_Doctype
                + ", "+Order.KEY_dogovor
                + ", "+Order.KEY_isDelivered
                + ", "+Order.KEY_pricetype
                + ", "+Order.KEY_warehouse
                + ", "+Order.KEY_totalSum
                + " FROM " + Order.TABLE
                + " WHERE "+Order.KEY_BAZA+" = '"+baza+"'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setOrderID(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_OrderID)));
                order.setTotalSum(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_totalSum))));
                order.setClient(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_clientGUID)));
                order.setComment(cursor.getString(cursor.getColumnIndexOrThrow(Order.KEY_comment)));
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
                        item.setQuantity(Integer.parseInt(myCursor.getString(myCursor.getColumnIndexOrThrow(Item.KEY_Quantity))));
                        itemArrayList.add(item);
                    } while (myCursor.moveToNext());
                }
                myCursor.close();

                order.setArraylistTovar(itemArrayList);
                arrayList.add(order);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return arrayList;
    }
}