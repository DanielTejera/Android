package eii.ulpgc.es.orders.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import eii.ulpgc.es.orders.DataBase.Contract.StructureDB;

/**
 * Created by Daniel on 12/04/2017.
 */

public class OrdersDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "orders.db";

    private static final String CUSTOMERS_TABLE_CREATE =
            "CREATE TABLE " + StructureDB.CUSTOMERS_TABLE_NAME + "(" +
                    StructureDB.COLUMN_IDCUSTOMER +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    StructureDB.COLUMN_CUSTOMER_NAME + " TEXT, " +
                    StructureDB.COLUMN_CUSTOMER_ADDRESS + " TEXT);";

    private static final String PRODUCTS_TABLE_CREATE =
            "CREATE TABLE " + StructureDB.PRODUCTS_TABLE_NAME + "(" +
                    StructureDB.COLUMN_IDPRODUCT +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    StructureDB.COLUMN_PRODUCT_NAME + " TEXT, " +
                    StructureDB.COLUMN_PRODUCT_DESCRIPTION + " TEXT, " +
                    StructureDB.COLUMN_PRODUCT_PRICE + " REAL);";

    private static final String ORDERS_TABLE_CREATE =
            "CREATE TABLE " + StructureDB.ORDERS_TABLE_NAME + "(" +
                    StructureDB.COLUMN_IDORDER +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    StructureDB.COLUMN_ID_CUSTOMER + " INTEGER, " +
                    StructureDB.COLUMN_ID_PRODUCT + " INTEGER, " +
                    StructureDB.COLUMN_ORDER_CODE + " TEXT, " +
                    StructureDB.COLUMN_ORDER_DATE + " DATE, " +
                    StructureDB.COLUMN_ORDER_QUANTITY + " INTEGER, " +
                    "FOREIGN KEY (" + StructureDB.COLUMN_ID_CUSTOMER +") REFERENCES " +
                    StructureDB.CUSTOMERS_TABLE_NAME + "(" + StructureDB.COLUMN_IDCUSTOMER + "), " +
                    "FOREIGN KEY (" + StructureDB.COLUMN_ID_PRODUCT +") REFERENCES " +
                    StructureDB.PRODUCTS_TABLE_NAME + "(" + StructureDB.COLUMN_IDPRODUCT + "));";

    private static final String CUSTOMERS_TABLE_DROP =
            "DROP TABLE IF EXISTS " + StructureDB.CUSTOMERS_TABLE_NAME;

    private static final String PRODUCTS_TABLE_DROP =
            "DROP TABLE IF EXISTS " + StructureDB.PRODUCTS_TABLE_NAME;

    private static final String ORDERS_TABLE_DROP =
            "DROP TABLE IF EXISTS " + StructureDB.ORDERS_TABLE_NAME;

    public OrdersDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CUSTOMERS_TABLE_CREATE);

        db.execSQL(PRODUCTS_TABLE_CREATE);

        db.execSQL(ORDERS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CUSTOMERS_TABLE_DROP);
        db.execSQL(CUSTOMERS_TABLE_CREATE);

        db.execSQL(PRODUCTS_TABLE_DROP);
        db.execSQL(PRODUCTS_TABLE_CREATE);

        db.execSQL(ORDERS_TABLE_DROP);
        db.execSQL(ORDERS_TABLE_CREATE);
    }
}
