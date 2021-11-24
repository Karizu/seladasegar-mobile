package com.selada.seladasegar.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLDatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "CARTS";
    public static final String TABLE_FAVOURITE = "FAVOURITE";
    public static final String TABLE_HISTORY = "HISTORY";

    // Table columns
    public static final String _ID = "_id";
    public static final String ITEM_ID = "item_id";
    public static final String ITEM_NAME = "item_name";
    public static final String ITEM_PRICE = "item_price";
    public static final String ITEM_IMAGE = "item_image";
    public static final String ITEM_QTY = "item_qty";
    public static final String ITEM_STOK = "item_stok";
    public static final String ITEM_TOTAL_PRICE = "item_total_price";
    public static final String ITEM_CATEGORY_ID = "item_category_id";

    public static final String ORDER_ID = "order_id";
    public static final String ORDER_EMAIL = "order_email";
    public static final String ORDER_SHIPPING = "order_shipping";
    public static final String ORDER_SHIPPING_ADDRESS = "order_shipping_address";
    public static final String ORDER_SHIPPING_LINES = "order_shipping_lines";
    public static final String ORDER_TOTAL = "order_total";
    public static final String ORDER_STATUS = "order_status";
    public static final String ORDER_CREATED_AT = "order_created_at";
    public static final String ORDER_UNIQUE_CODE = "order_unique_code";
    public static final String ORDER_PAYMENT_METHOD = "payment_method_title";
    public static final String ORDER_CUSTOMER_NOTE = "order_customer_note";

    // Database Information
    static final String DB_NAME = "SELADASEGAR_ANDROID.DB";

    // database version
    static final int DB_VERSION = 6;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ITEM_ID + " TEXT NOT NULL, "
            + ITEM_NAME + " TEXT, "
            + ITEM_IMAGE + " TEXT, "
            + ITEM_PRICE + " TEXT, "
            + ITEM_TOTAL_PRICE + " TEXT, "
            + ITEM_CATEGORY_ID + " TEXT, "
            + ITEM_QTY + " TEXT, "
            + ITEM_STOK + " TEXT);";

    private static final String CREATE_TABLE_FAVOURITE = "create table " + TABLE_FAVOURITE + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ITEM_ID + " TEXT NOT NULL, "
            + ITEM_NAME + " TEXT, "
            + ITEM_PRICE + " TEXT, "
            + ITEM_IMAGE + " TEXT, "
            + ITEM_QTY + " TEXT);";

    private static final String CREATE_TABLE_HISTORY = "create table " + TABLE_HISTORY + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ORDER_ID + " TEXT NOT NULL, "
            + ORDER_EMAIL + " TEXT, "
            + ORDER_SHIPPING + " TEXT, "
            + ORDER_SHIPPING_ADDRESS + " TEXT, "
            + ORDER_SHIPPING_LINES + " TEXT, "
            + ORDER_PAYMENT_METHOD + " TEXT, "
            + ORDER_STATUS + " TEXT, "
            + ORDER_TOTAL + " TEXT, "
            + ORDER_CREATED_AT + " TEXT, "
            + ORDER_CUSTOMER_NOTE + " TEXT, "
            + ORDER_UNIQUE_CODE + " TEXT);";

    public SQLDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_FAVOURITE);
        db.execSQL(CREATE_TABLE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

}
