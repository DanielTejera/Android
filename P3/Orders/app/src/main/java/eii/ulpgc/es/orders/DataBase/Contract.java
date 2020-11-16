package eii.ulpgc.es.orders.DataBase;

/**
 * Created by Daniel on 12/04/2017.
 */

import android.provider.BaseColumns;

public final class Contract {
    public Contract() {}
    public static abstract class StructureDB implements BaseColumns {

        // Customers Table
        public static final String CUSTOMERS_TABLE_NAME = "customers";
        public static final String COLUMN_IDCUSTOMER = "_id";
        public static final String COLUMN_CUSTOMER_NAME = "name";
        public static final String COLUMN_CUSTOMER_ADDRESS = "address";

        // Products Table
        public static final String PRODUCTS_TABLE_NAME = "products";
        public static final String COLUMN_IDPRODUCT = "_id";
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_DESCRIPTION = "description";
        public static final String COLUMN_PRODUCT_PRICE = "price";

        // Orders Table
        public static final String ORDERS_TABLE_NAME = "orders";
        public static final String COLUMN_IDORDER = "_id";
        public static final String COLUMN_ID_CUSTOMER = "idcustomer";
        public static final String COLUMN_ID_PRODUCT = "idproduct";
        public static final String COLUMN_ORDER_CODE = "code";
        public static final String COLUMN_ORDER_DATE = "date";
        public static final String COLUMN_ORDER_QUANTITY = "quantity";
    }
}