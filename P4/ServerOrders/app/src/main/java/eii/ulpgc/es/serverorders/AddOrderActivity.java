package eii.ulpgc.es.serverorders;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import eii.ulpgc.es.serverorders.Models.Order;

public class AddOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String URL_TO_GET_ORDERS = "http://tip.dis.ulpgc.es/ventas/server.php?QueryOrders";
    private static final String URL_TO_INSERT = "http://tip.dis.ulpgc.es/ventas/server.php?InsertOrder";
    private static final String URL_TO_UPDATE = "http://tip.dis.ulpgc.es/ventas/server.php?UpdateOrder";
    private static final String URL_TO_DELETE = "http://tip.dis.ulpgc.es/ventas/server.php?DeleteOrder";

    private EditText orderCodeField;
    private DatePicker orderDate;
    private EditText selectCustomerField;
    private EditText selectProductField;
    private EditText quantityField;
    private EditText totalPriceField;
    private String productPrice;
    private Boolean update;
    private String orderID;
    private String customerID;
    private String productID;
    private Boolean comeFromSelect;
    private ArrayList<Order> orders;

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
        orderDate = (DatePicker) findViewById(R.id.code_date);
        selectCustomerField = (EditText) findViewById(R.id.select_customer_field);
        selectProductField = (EditText) findViewById(R.id.select_product_field);
        quantityField = (EditText) findViewById(R.id.quantity_field);
        totalPriceField = (EditText) findViewById(R.id.total_price_field);

        selectCustomerField.setFocusable(false);
        selectProductField.setFocusable(false);
        quantityField.setFocusable(false);
        totalPriceField.setFocusable(false);

        orders = new ArrayList<>();

        Intent intent = getIntent();
        orderCodeField.setText(intent.getStringExtra("code"));
        orderID = intent.getStringExtra("IDOrder");
        customerID = intent.getStringExtra("IDCustomer");
        productID = intent.getStringExtra("IDProduct");
        selectCustomerField.setText(intent.getStringExtra("customerName"));
        selectProductField.setText(intent.getStringExtra("productName"));
        productPrice =  intent.getStringExtra("price");
        quantityField.setText(intent.getStringExtra("quantity"));

        comeFromSelect = intent.getBooleanExtra("comeFromSelect", false);
        update = intent.getBooleanExtra("update", false);

        if (Objects.equals(orderCodeField.getText().toString(), "")) {
            quantityField.setText("0");
            customerID = "";
            productID = "";
        } else {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(df.parse(intent.getStringExtra("date")));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            orderDate.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));

            totalPriceField.setText(String.valueOf(calculateTotalPrice()));

        }

        getOrders();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        if(update){
            getMenuInflater().inflate(R.menu.update_order_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.add_order_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

                    int day = orderDate.getDayOfMonth();
                    int month = orderDate.getMonth();
                    int year = orderDate.getYear();

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String formatedDate = sdf.format(calendar.getTime());


                    selectCustomerIntent.putExtra("customer", true);
                    selectCustomerIntent.putExtra("product", false);
                    selectCustomerIntent.putExtra("code", orderCodeField.getText().toString());
                    selectCustomerIntent.putExtra("date", formatedDate);
                    selectCustomerIntent.putExtra("orderID", orderID);
                    selectCustomerIntent.putExtra("IDCustomer", customerID);
                    selectCustomerIntent.putExtra("IDProduct", productID);
                    selectCustomerIntent.putExtra("productName", selectProductField.getText().toString());
                    selectCustomerIntent.putExtra("customerName", "");
                    selectCustomerIntent.putExtra("price","0");
                    selectCustomerIntent.putExtra("quantity", quantityField.getText().toString());
                    startActivity(selectCustomerIntent);
                    break;
                case R.id.select_product_button:
                    Intent selectProductIntent = new Intent(this, SelectActivity.class);

                    int d = orderDate.getDayOfMonth();
                    int m = orderDate.getMonth();
                    int y = orderDate.getYear();

                    Calendar calendarProduct = Calendar.getInstance();
                    calendarProduct.set(y, m, d);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String formDate = simpleDateFormat.format(calendarProduct.getTime());

                    selectProductIntent.putExtra("customer", false);
                    selectProductIntent.putExtra("product", true);
                    selectProductIntent.putExtra("code", orderCodeField.getText().toString());
                    selectProductIntent.putExtra("date", formDate);
                    selectProductIntent.putExtra("orderID",orderID);
                    selectProductIntent.putExtra("IDCustomer", customerID);
                    selectProductIntent.putExtra("IDProduct", productID);
                    selectProductIntent.putExtra("productName", "");
                    selectProductIntent.putExtra("customerName", selectCustomerField.getText().toString());
                    selectProductIntent.putExtra("price","0");
                    selectProductIntent.putExtra("quantity", quantityField.getText().toString());
                    startActivity(selectProductIntent);
                    break;
                case R.id.minus_button:
                    if (Objects.equals(selectProductField.getText().toString(), "")) {
                        Toast.makeText(this, R.string.toast_customer_field_empty, Toast.LENGTH_SHORT).show();
                    } else {
                        if (Integer.valueOf(quantityField.getText().toString()) > 0) {
                            subtractQuantity();
                        }
                    }
                    break;
                case R.id.plus_button:
                    if (Objects.equals(selectProductField.getText().toString(), "")) {
                        Toast.makeText(this, R.string.toast_product_field_empty, Toast.LENGTH_SHORT).show();
                    } else {
                        addQuantity();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Responsible method to add a order to the database
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addOrder() throws ParseException {
        if (update) {
            updateOrder();
        } else {
            final String code = orderCodeField.getText().toString();
            final String customerName = selectCustomerField.getText().toString();
            final String productName = selectProductField.getText().toString();
            final String quantity = quantityField.getText().toString();

            if (testFields(code, customerName, productName, quantity)) {
                if (testCode(code)){

                    new AlertDialog.Builder(AddOrderActivity.this)
                            .setTitle(R.string.dialog_title)
                            .setMessage(R.string.add_order_message)
                            .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int wich){
                                    addOrderAtDB(code, quantity);
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
                    Toast.makeText(this, R.string.toast_code_field_empty, Toast.LENGTH_SHORT).show();
                }
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
     * Method that checks if there is an order in the database with the code passed by parameter
     *
     * @param code Order Code
     * @return True if the order does not exist. False if it exists
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean testCode(String code){
        int count = 0;
        for (Order order : orders){
            if (Objects.equals(order.getCode(), code)) {
                count++;
                break;
            }
        }

        if (count != 0){
            return false;
        }
        return true;
    }

    /**
     * Method that updates the information of an existing order in the database
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateOrder() {

        final String code = orderCodeField.getText().toString();
        final String customerName = selectCustomerField.getText().toString();
        final String productName = selectProductField.getText().toString();
        final String quantity = quantityField.getText().toString();

        if (testFields(code, customerName, productName, quantity)) {
            new AlertDialog.Builder(AddOrderActivity.this)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.update_order_message)
                    .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int wich) {
                            updateOrderAtDB(code, quantity);
                        }
                    })
                    .setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int wich) {
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
     * Method that deletes a order from the database
     */
    private void deleteOrder() {
        String code = orderCodeField.getText().toString();
        String customerName = selectCustomerField.getText().toString();
        String productName = selectProductField.getText().toString();
        String quantity = quantityField.getText().toString();

        if (testFields(code, customerName, productName, quantity)) {

            new AlertDialog.Builder(AddOrderActivity.this)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.delete_order_message)
                    .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int wich){
                            deleteOrderAtDB(orderID);
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
     * Method that calculates the price by multiplying the quantity of product and the price fixed
     * for that product
     *
     * @return The total price of the order
     */
    private double calculateTotalPrice() {
        return Double.valueOf(productPrice) * Double.valueOf(quantityField.getText().toString());
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
     * Method that collects information from orders stored in the database to fill the orders Array
     */
    private void getOrders() {
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, URL_TO_GET_ORDERS,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray jsonArray;
                    Log.d("Orders Response ->", response.toString());
                    try {
                        jsonArray = response.getJSONArray("data");
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), ": ERROR: Empty reply", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {

                        try {
                            JSONObject orderReply = jsonArray.getJSONObject(i);

                            String idOrder = orderReply.getString("IDOrder");
                            String idCustomer = orderReply.getString("IDCustomer");
                            String idProduct = orderReply.getString("IDProduct");
                            String customerName = orderReply.getString("customerName");
                            String productName = orderReply.getString("productName");
                            String code = orderReply.getString("code");
                            String price = orderReply.getString("price");
                            String quantity = orderReply.getString("quantity");
                            String date = orderReply.getString("date");


                            Order newOrder = new Order(idOrder, idCustomer, idProduct,
                                    customerName, productName, code, price, quantity, date);

                            orders.add(newOrder);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        switch (response.statusCode) {
                            case 500:
                                Toast.makeText(getApplicationContext(),
                                        R.string.error_500,
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }
            });
            queue.add(jsObjRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that performs the insertion of a order in the database
     *
     * @param code Order code
     * @param quantity Product quantity
     */
    public void addOrderAtDB(String code, String quantity){
        int day = orderDate.getDayOfMonth();
        int month = orderDate.getMonth();
        int year = orderDate.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formatedDate = sdf.format(calendar.getTime());

        Integer IDcustomer = Integer.valueOf(customerID);
        Integer IDproduct = Integer.valueOf(productID);

        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
            jsonObject.put("date", formatedDate);
            jsonObject.put("IDCustomer", IDcustomer);
            jsonObject.put("IDProduct", IDproduct);
            jsonObject.put("quantity", Integer.valueOf(quantity));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, URL_TO_INSERT,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray jsonArray;
                    Log.d("Insert Response ->", response.toString());
                    try {
                        jsonArray = response.getJSONArray("data");
                    } catch (JSONException e) {
                        return;
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {

                        try {
                            JSONObject reply = jsonArray.getJSONObject(i);

                            orderID = String.valueOf(reply.getInt("IDProduct"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        switch (response.statusCode) {
                            case 500:
                                Toast.makeText(getApplicationContext(),
                                        R.string.error_500,
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }
            });
            queue.add(jsObjRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formatedDate = sdf.format(calendar.getTime());

        Integer IDcustomer = Integer.valueOf(customerID);
        Integer IDproduct = Integer.valueOf(productID);

        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("IDOrder", Integer.valueOf(orderID));
            jsonObject.put("IDCustomer", IDcustomer);
            jsonObject.put("IDProduct", IDproduct);
            jsonObject.put("code", code);
            jsonObject.put("date", formatedDate);
            jsonObject.put("quantity", Integer.valueOf(quantity));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, URL_TO_UPDATE,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Update Response ->", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        switch (response.statusCode) {
                            case 500:
                                Toast.makeText(getApplicationContext(),
                                        R.string.toast_name_exists,
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }
            });
            queue.add(jsObjRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Method that deletes the order in the database
     *
     * @param orderToDelete Order id
     */
    public void deleteOrderAtDB(String orderToDelete){
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("IDOrder", Integer.valueOf(orderToDelete));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, URL_TO_DELETE,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Delete Response ->", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        switch (response.statusCode) {
                            case 500:
                                Toast.makeText(getApplicationContext(),
                                        R.string.error_500,
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }
            });
            queue.add(jsObjRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString("orderCode", orderCodeField.getText().toString());
        savedInstanceState.putString("orderDate", orderDate.toString());
        savedInstanceState.putString("orderCustomerName", selectCustomerField.getText().toString());
        savedInstanceState.putString("orderProductName", selectProductField.getText().toString());
        savedInstanceState.putString("orderQuantity", quantityField.getText().toString());
        savedInstanceState.putString("orderPrice", totalPriceField.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        orderCodeField.setText(savedInstanceState.getString("orderCode"));
        orderDate.setMinDate(Long.getLong(savedInstanceState.getString("orderDate")));
        selectCustomerField.setText(savedInstanceState.getString("orderCustomerName"));
        selectProductField.setText(savedInstanceState.getString("orderProductName"));
        quantityField.setText(savedInstanceState.getString("orderQuantity"));
        totalPriceField.setText(savedInstanceState.getString("orderPrice"));
    }
}