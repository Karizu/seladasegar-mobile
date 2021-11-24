package com.selada.seladasegar.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
    private SQLDatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;
    private SQLiteDatabase db;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new SQLDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        db = dbHelper.getReadableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String itemId, String itemName, String itemPrice, String itemTotalPrice, String itemImage, String itemQty, String itemStok, String itemCategoryId) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(SQLDatabaseHelper.ITEM_ID, itemId);
        contentValue.put(SQLDatabaseHelper.ITEM_NAME, itemName);
        contentValue.put(SQLDatabaseHelper.ITEM_PRICE, itemPrice);
        contentValue.put(SQLDatabaseHelper.ITEM_TOTAL_PRICE, itemTotalPrice);
        contentValue.put(SQLDatabaseHelper.ITEM_CATEGORY_ID, itemCategoryId);
        contentValue.put(SQLDatabaseHelper.ITEM_IMAGE, itemImage);
        contentValue.put(SQLDatabaseHelper.ITEM_QTY, itemQty);
        contentValue.put(SQLDatabaseHelper.ITEM_STOK, itemStok);
        database.insert(SQLDatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public void insertFavourite(String itemId, String itemName, String itemPrice, String itemImage, String itemQty) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(SQLDatabaseHelper.ITEM_ID, itemId);
        contentValue.put(SQLDatabaseHelper.ITEM_NAME, itemName);
        contentValue.put(SQLDatabaseHelper.ITEM_PRICE, itemPrice);
        contentValue.put(SQLDatabaseHelper.ITEM_IMAGE, itemImage);
        contentValue.put(SQLDatabaseHelper.ITEM_QTY, itemQty);
        database.insert(SQLDatabaseHelper.TABLE_FAVOURITE, null, contentValue);
    }

    public void insertHistory(String orderId, String orderEmail, String orderShipping, String orderShippingAddress, String orderStatus,
                              String orderTotal, String orderCreatedAt, String orderUniqueCode, String orderShippingLines, String orderPaymentMethod,
                              String orderCustomerNote) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(SQLDatabaseHelper.ORDER_ID, orderId);
        contentValue.put(SQLDatabaseHelper.ORDER_EMAIL, orderEmail);
        contentValue.put(SQLDatabaseHelper.ORDER_SHIPPING, orderShipping);
        contentValue.put(SQLDatabaseHelper.ORDER_SHIPPING_ADDRESS, orderShippingAddress);
        contentValue.put(SQLDatabaseHelper.ORDER_SHIPPING_LINES, orderShippingLines);
        contentValue.put(SQLDatabaseHelper.ORDER_STATUS, orderStatus);
        contentValue.put(SQLDatabaseHelper.ORDER_TOTAL, orderTotal);
        contentValue.put(SQLDatabaseHelper.ORDER_CREATED_AT, orderCreatedAt);
        contentValue.put(SQLDatabaseHelper.ORDER_UNIQUE_CODE, orderUniqueCode);
        contentValue.put(SQLDatabaseHelper.ORDER_PAYMENT_METHOD, orderPaymentMethod);
        contentValue.put(SQLDatabaseHelper.ORDER_CUSTOMER_NOTE, orderCustomerNote);
        database.insert(SQLDatabaseHelper.TABLE_HISTORY, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { SQLDatabaseHelper._ID, SQLDatabaseHelper.ITEM_ID,
                SQLDatabaseHelper.ITEM_NAME, SQLDatabaseHelper.ITEM_PRICE,
                SQLDatabaseHelper.ITEM_TOTAL_PRICE,
                SQLDatabaseHelper.ITEM_IMAGE, SQLDatabaseHelper.ITEM_QTY};
        Cursor cursor = database.query(SQLDatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchFavourite() {
        String[] columns = new String[] { SQLDatabaseHelper._ID, SQLDatabaseHelper.ITEM_ID,
                SQLDatabaseHelper.ITEM_NAME, SQLDatabaseHelper.ITEM_PRICE,
                SQLDatabaseHelper.ITEM_IMAGE, SQLDatabaseHelper.ITEM_QTY};
        Cursor cursor = database.query(SQLDatabaseHelper.TABLE_FAVOURITE, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchHistory() {
        String[] columns = new String[] { SQLDatabaseHelper._ID, SQLDatabaseHelper.ORDER_ID,
                SQLDatabaseHelper.ORDER_EMAIL, SQLDatabaseHelper.ORDER_SHIPPING,
                SQLDatabaseHelper.ORDER_SHIPPING_ADDRESS, SQLDatabaseHelper.ORDER_SHIPPING_LINES,
                SQLDatabaseHelper.ORDER_STATUS, SQLDatabaseHelper.ORDER_TOTAL,
                SQLDatabaseHelper.ORDER_CREATED_AT, SQLDatabaseHelper.ORDER_UNIQUE_CODE};
        Cursor cursor = database.query(SQLDatabaseHelper.TABLE_HISTORY, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor readData(String itemId) {
        String qLog = "select * from CARTS "
                + "where item_id = '"+itemId+"';";
        return db.rawQuery(qLog, null);
    }

    public Cursor readDataFavourite(String itemId) {
        String qLog = "select * from FAVOURITE "
                + "where item_id = '"+itemId+"';";
        return db.rawQuery(qLog, null);
    }

    public Cursor readDataHistory(String orderId) {
        String qLog = "select * from HISTORY "
                + "where order_id = '"+orderId+"';";
        return db.rawQuery(qLog, null);
    }

    public Cursor readAllData() {
        String qLog = "select * from CARTS;";
        return db.rawQuery(qLog, null);
    }

    public Cursor readAllDataFavourite() {
        String qLog = "select * from FAVOURITE;";
        return db.rawQuery(qLog, null);
    }

    public Cursor readAllDataHistory() {
        String qLog = "select * from HISTORY;";
        return db.rawQuery(qLog, null);
    }

    public int update(String itemId, String itemQty, String itemTotalPrice) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLDatabaseHelper.ITEM_QTY, itemQty);
        contentValues.put(SQLDatabaseHelper.ITEM_TOTAL_PRICE, itemTotalPrice);
        return database.update(SQLDatabaseHelper.TABLE_NAME, contentValues, SQLDatabaseHelper.ITEM_ID + " = " + itemId, null);
    }

    public int updateHistory(String orderId, String status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLDatabaseHelper.ORDER_STATUS, status);
        return database.update(SQLDatabaseHelper.TABLE_HISTORY, contentValues, SQLDatabaseHelper.ORDER_ID + " = " + orderId, null);
    }

    public void delete(String itemId) {
        database.delete(SQLDatabaseHelper.TABLE_NAME, SQLDatabaseHelper.ITEM_ID + "=" + itemId, null);
    }

    public void deleteAllRows(String TABLE_NAME) {
        database.delete(TABLE_NAME, null, null);
    }

    public void deleteFavourite(String itemId) {
        database.delete(SQLDatabaseHelper.TABLE_FAVOURITE, SQLDatabaseHelper.ITEM_ID + "=" + itemId, null);
    }
}
