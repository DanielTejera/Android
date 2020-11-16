package eii.ulpgc.es.orders;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import eii.ulpgc.es.orders.DataBase.Contract.StructureDB;
import eii.ulpgc.es.orders.DataBase.OrdersDB;

public class AddOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText orderCodeField;
    private DatePicker orderDate;
    private EditText selectCustomerField;
    private EditText selectProductField;
    private EditText quantityField;
    private EditText totalPriceField;
    private Boolean update;
    private Integer orderID;
    private Integer customerID;
    private Integer productID;
    private Boolean comeFromSelect;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.add_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.add_order_actionbar_title);
        }

        orderCodeField = (EditText) findViewById(R.id.order_code_field);
        orderDate = (DatePicker) findViewById(R.id.order_date);
        selectCustomerField = (EditText) findViewById(R.id.select_customer_field);
        selectProductField = (EditText) findViewById(R.id.select_product_field);
        quantityField = (EditText) findViewById(R.id.quantity_field);
        totalPriceField = (EditText) findViewById(R.id.total_price_field);

        selectCustomerField.setFocusable(false);
        selectProductField.setFocusable(false);
        quantityField.setFocusable(false);
        totalPriceField.setFocusable(false);

        Intent intent = getIntent();
        orderCodeField.setText(intent.getStringExtra("code"));
        comeFromSelect = intent.getBooleanExtra("comeFromSelect", false);
        update = intent.getBooleanExtra("update", false);

        if (Objects.equals(orderCodeField.getText().toString(), "")) {
            quantityField.setText("0");
        } else {

            setOrderData(intent, savedInstanceState);
        }
    }

    /**
     * Method that fills the data of the view according to the data passed from the previous activity
     *
     * @param intent
     */
    private void setOrderData(Intent intent, Bundle savedInstanceState) {

        selectCustomerField.setText(intent.getStringExtra("customerName"));

        selectProductField.setText(intent.getStringExtra("productName"));

        orderID = getOrderID(orderCodeField.getText().toString(), comeFromSelect);
        customerID = getCustomerID(selectCustomerField.getText().toString());
        productID = getProductID(selectProductField.getText().toString());

        int quantity = getQuantity(orderID);
        quantityField.setText(String.valueOf(quantity));

        double totalPrice = calculateTotalPrice();
        totalPriceField.setText(String.valueOf(totalPrice));

        if (orderID != 0) {
            if (savedInstanceState == null) {
                setOrderDate(orderID);
            }
        }
    }

    /**
     * Method that extracts the date associated with the order to be edited, placing it
     * appropriately in the datepicker
     *
     * @param id Order ID
     */
    private void setOrderDate(int id){
        String date = "";
        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor result = bd.rawQuery("select date from orders where _id=?",
                new String[]{String.valueOf(id)});

        if ((result != null) && result.moveToFirst()) {
            date = result.getString(0);
        }

        if (result != null) {
            result.close();
        }

        bd.close();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Calendar cal  = Calendar.getInstance();
            cal.setTime(df.parse(date));

            orderDate.updateDate(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (update){
            getMenuInflater().inflate(R.menu.update_order_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.add_order_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_order:
                try {
                    addOrder();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.action_delete_order:
                deleteOrder();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (Objects.equals(orderCodeField.getText().toString(), "")) {
            Toast.makeText(this, R.string.toast_code_field_empty, Toast.LENGTH_SHORT).show();
        } else {
            switch (id) {
                case R.id.select_customer_button:
                    Intent selectCustomerIntent = new Intent(this, SelectActivity.class);
                    selectCustomerIntent.putExtra("customer", true);
                    selectCustomerIntent.putExtra("product", false);
                    selectCustomerIntent.putExtra("code", orderCodeField.getText().toString());
                    selectCustomerIntent.putExtra("productName", selectProductField.getText().toString());
                    selectCustomerIntent.putExtra("customerName", "");
                    startActivity(selectCustomerIntent);
                    break;
                case R.id.select_product_button:
                    Intent selectProductIntent = new Intent(this, SelectActivity.class);
                    selectProductIntent.putExtra("customer", false);
                    selectProductIntent.putExtra("product", true);
                    selectProductIntent.putExtra("code", orderCodeField.getText().toString());
                    selectProductIntent.putExtra("productName", "");
                    selectProductIntent.putExtra("customerName", selectCustomerField.getText().toString());
                    startActivity(selectProductIntent);
                    break;
                case R.id.minus_button:
                    if (Integer.valueOf(quantityField.getText().toString()) != 0) {
                        subtractQuantity();
                    }
                    break;
                case R.id.plus_button:
                    addQuantity();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Method responsible for making the field checks to add a order,
     * in addition to generating the confirmation dialog
     */
    private void addOrder() throws ParseException {
        if (update) {
            updateOrder();
        } else {
            final String code = orderCodeField.getText().toString();
            final String customerName = selectCustomerField.getText().toString();
            final String productName = selectProductField.getText().toString();
            final String quantity = quantityField.getText().toString();

            if (testFields(code, customerName, productName, quantity)) {

                new AlertDialog.Builder(AddOrderActivity.this)
                        .setTitle(R.string.dialog_title)
                        .setMessage(R.string.add_order_message)
                        .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int wich){
                                addOrderAtDB(code, customerName, productName, quantity);
                            }
                        })
                        .setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int wich){
                                dialog.cancel();
                            }
                        })
                        .setCancelable(false)
                        .show();

            } else {
                Toast.makeText(this, R.string.toast_fields_empty, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Method that checks if the fields that the user must fill contain the necessary data
     *
     * @param code         Order code
     * @param customerName Customer to which the order belongs
     * @param productName  Product to be ordered
     * @param quantity     Product quantity
     * @return True if the fields contain information. False if the fields are empty
     */
    private boolean testFields(String code, String customerName, String productName, String quantity) {
        if (code.length() == 0) {
            return false;
        }

        if (customerName.length() == 0) {
            return false;
        }

        if (productName.length() == 0) {
            return false;
        }

        if (quantity.length() == 0) {
            return false;
        }
        return true;
    }

    /**
     * Method that makes the necessary checks of the fields to perform the update of a order,
     * besides generating the confirmation dialog
     */
    private void updateOrder() {

        final String code = orderCodeField.getText().toString();
        final String customerName = selectCustomerField.getText().toString();
        final String productName = selectProductField.getText().toString();
        final String quantity = quantityField.getText().toString();

        if (testFields(code, customerName, productName, quantity)) {

            new AlertDialog.Builder(AddOrderActivity.this)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.update_order_message)
                    .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int wich){
                            updateOrderAtDB(code, quantity);
                        }
                    })
                    .setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int wich){
                            dialog.cancel();
                        }
                    })
                    .setCancelable(false)
                    .show();

        } else {
            Toast.makeText(this, R.string.toast_fields_empty, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method that searches the database for the id corresponding to the order to
     * which the code entered as parameter belongs
     *
     * @param code Order code
     * @param flag Indicates that you have returned from the selection activity
     * @return Order code if it exists
     */
    private int getOrderID(String code, Boolean flag) {
        Integer id = 0;
        if (flag) {

            return id;
        }
        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor result = bd.rawQuery("select _id from orders where code=?", new String[]{code});

        if ((result != null) && result.moveToFirst()) {
            id = result.getInt(0);
        }

        if (result != null) {
            result.close();
        }

        bd.close();

        return id;
    }

    /**
     * Method that makes the necessary checks of the fields to carry out the deletion of an order,
     * besides generating the confirmation dialog
     */
    private void deleteOrder() {
        final Integer id;

        String code = orderCodeField.getText().toString();
        String customerName = selectCustomerField.getText().toString();
        String productName = selectProductField.getText().toString();
        String quantity = quantityField.getText().toString();

        if (testFields(code, customerName, productName, quantity)) {
            id = getOrderID(code, false);
            if (id == 0) {
                Toast.makeText(this, R.string.toast_failed_delete_order, Toast.LENGTH_SHORT).show();
            } else {

                new AlertDialog.Builder(AddOrderActivity.this)
                        .setTitle(R.string.dialog_title)
                        .setMessage(R.string.delete_order_message)
                        .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int wich){
                                deleteOrderAtDB(id);
                            }
                        })
                        .setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int wich){
                                dialog.cancel();
                            }
                        })
                        .setCancelable(false)
                        .show();

            }
        } else {
            Toast.makeText(this, R.string.toast_fields_empty, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method that calculates the price by multiplying the quantity of product and the price fixed
     * for that product
     *
     * @return The total price of the order
     */
    private double calculateTotalPrice() {
        Double price = 0.0;

        productID = getProductID(selectProductField.getText().toString());

        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor result = bd.rawQuery("select price from products where _id=?",
                new String[]{String.valueOf(productID)});

        if ((result != null) && result.moveToFirst()) {
            price = result.getDouble(0);
        }

        if (result != null) {
            result.close();
        }

        bd.close();

        return price * Double.valueOf(quantityField.getText().toString());
    }

    /**
     * Search in the database for the number stored in the quantity column for the order with the id
     * passed by parameter
     *
     * @param orderID Order id
     * @return The quantity of products associated with the order
     */
    private int getQuantity(Integer orderID) {
        Integer quantity = 0;
        if (orderID == 0) {
            return quantity;
        }
        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor result = bd.rawQuery("select quantity from orders where _id=?",
                new String[]{String.valueOf(orderID)});

        if ((result != null) && result.moveToFirst()) {
            quantity = result.getInt(0);
        }

        if (result != null) {
            result.close();
        }

        bd.close();

        return quantity;
    }

    /**
     * Add one to the desired amount of product
     */
    private void addQuantity() {
        int quantity = Integer.valueOf(quantityField.getText().toString()) + 1;
        quantityField.setText(String.valueOf(quantity));

        totalPriceField.setText(String.valueOf(calculateTotalPrice()));
    }

    /**
     * Subtract one from the desired product quantity
     */
    private void subtractQuantity() {
        int quantity = Integer.valueOf(quantityField.getText().toString()) - 1;
        quantityField.setText(String.valueOf(quantity));

        totalPriceField.setText(String.valueOf(calculateTotalPrice()));
    }

    /**
     * Search in the database the product id associated with the name passed by parameter
     *
     * @param productName Product name
     * @return Product id if exists
     */
    private int getProductID(String productName) {
        Integer id = 0;
        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor result = bd.rawQuery("select _id from products where name=?",
                new String[]{productName});

        if ((result != null) && result.moveToFirst()) {
            id = result.getInt(0);
        }

        if (result != null) {
            result.close();
        }

        bd.close();

        return id;
    }

    /**
     * Search in the database the customer id associated with the name passed by parameter
     *
     * @param customerName Customer name
     * @return Customer id if exists
     */
    private int getCustomerID(String customerName) {
        Integer id = 0;
        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor result = bd.rawQuery("select _id from customers where name=?",
                new String[]{customerName});

        if ((result != null) && result.moveToFirst()) {
            id = result.getInt(0);
        }

        if (result != null) {
            result.close();
        }

        bd.close();

        return id;
    }

    /**
     * Method that performs the insertion of a order in the database
     *
     * @param code Order code
     * @param customerName Customer to whom the order corresponds
     * @param productName Product that forms the order
     * @param quantity Product quantity
     */
    public void addOrderAtDB(String code, String customerName, String productName, String quantity){
        int day = orderDate.getDayOfMonth();
        int month = orderDate.getMonth();
        int year = orderDate.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formatedDate = sdf.format(calendar.getTime());

        Integer customerID = getCustomerID(customerName);
        Integer productID = getProductID(productName);

        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues newOrder = new ContentValues();
        newOrder.put(StructureDB.COLUMN_ORDER_CODE, code);
        newOrder.put(StructureDB.COLUMN_ORDER_DATE, formatedDate);
        newOrder.put(StructureDB.COLUMN_ORDER_QUANTITY, quantity);
        newOrder.put(StructureDB.COLUMN_ID_CUSTOMER, customerID);
        newOrder.put(StructureDB.COLUMN_ID_PRODUCT, productID);

        bd.insert(StructureDB.ORDERS_TABLE_NAME, null, newOrder);

        bd.close();

        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Method that performs the updating of order data in the database
     *
     * @param code Order code
     * @param quantity Product quantity
     */
    public void updateOrderAtDB(String code, String quantity){
        int day = orderDate.getDayOfMonth();
        int month = orderDate.getMonth();
        int year = orderDate.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formatedDate = sdf.format(calendar.getTime());

        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues order = new ContentValues();
        order.put(StructureDB.COLUMN_ORDER_CODE, code);
        order.put(StructureDB.COLUMN_ORDER_DATE, formatedDate);
        order.put(StructureDB.COLUMN_ORDER_QUANTITY, quantity);
        order.put(StructureDB.COLUMN_ID_CUSTOMER, customerID);
        order.put(StructureDB.COLUMN_ID_PRODUCT, productID);

        bd.update(StructureDB.ORDERS_TABLE_NAME, order,
                StructureDB.COLUMN_IDORDER + "=" + orderID, null);

        bd.close();

        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Method that deletes the order in the database
     *
     * @param orderToDelete Order id
     */
    public void deleteOrderAtDB(int orderToDelete){
        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getWritableDatabase();
        bd.delete(StructureDB.ORDERS_TABLE_NAME,
                StructureDB.COLUMN_IDORDER + "=" + orderToDelete + "", null);

        bd.close();

        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        int day = orderDate.getDayOfMonth();
        int month = orderDate.getMonth();
        int year = orderDate.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formatedDate = sdf.format(calendar.getTime());

        savedInstanceState.putString("orderCode", orderCodeField.getText().toString());
        savedInstanceState.putString("orderDate", formatedDate);
        savedInstanceState.putString("orderCustomerName", selectCustomerField.getText().toString());
        savedInstanceState.putString("orderProductName", selectProductField.getText().toString());
        savedInstanceState.putString("orderQuantity", quantityField.getText().toString());
        savedInstanceState.putString("orderPrice", totalPriceField.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        orderCodeField.setText(savedInstanceState.getString("orderCode"));
        selectCustomerField.setText(savedInstanceState.getString("orderCustomerName"));
        selectProductField.setText(savedInstanceState.getString("orderProductName"));
        quantityField.setText(savedInstanceState.getString("orderQuantity"));
        totalPriceField.setText(savedInstanceState.getString("orderPrice"));
    }
}
