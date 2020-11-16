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

import java.util.ArrayList;
import java.util.Objects;

import eii.ulpgc.es.serverorders.Models.Customer;
import eii.ulpgc.es.serverorders.Models.Order;


public class AddCustomerActivity extends AppCompatActivity {

    private static final String URL_TO_INSERT = "http://tip.dis.ulpgc.es/ventas/server.php?InsertCustomer";
    private static final String URL_TO_UPDATE = "http://tip.dis.ulpgc.es/ventas/server.php?UpdateCustomer";
    private static final String URL_TO_DELETE = "http://tip.dis.ulpgc.es/ventas/server.php?DeleteCustomer";
    private static final String URL_TO_GET_ORDERS = "http://tip.dis.ulpgc.es/ventas/server.php?QueryOrders";
    private static final String URL_TO_GET_CUSTOMERS = "http://tip.dis.ulpgc.es/ventas/server.php?QueryCustomers";

    private EditText customerNameField;
    private EditText customerAddresField;
    private Boolean update;
    private String customerID;
    private ArrayList<Order> orders;
    private ArrayList<Customer> customers;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.add_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.add_customer_actionbar_title);
        }

        customerNameField = (EditText) findViewById(R.id.customer_name_field);
        customerAddresField = (EditText) findViewById(R.id.customer_address_field);

        orders = new ArrayList<>();
        customers = new ArrayList<>();

        Intent intent = getIntent();
        customerNameField.setText((String) intent.getSerializableExtra("name"));
        customerAddresField.setText((String) intent.getSerializableExtra("address"));
        customerID = (String) intent.getSerializableExtra("id");

        if (Objects.equals(customerNameField.getText().toString(), "")) {
            update = false;
        } else {
            update = true;
            getOrders();
        }

        getCustomers();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if(update){
            getMenuInflater().inflate(R.menu.update_customer_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.add_customer_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_customer:
                addCustomer();
                return true;
            case R.id.action_delete_customer:
                deleteCustomer();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Responsible method to add a customer to the database
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addCustomer() {
        if (update) {
            updateCustomer();
        } else {
            final String name = customerNameField.getText().toString();
            final String address = customerAddresField.getText().toString();

            if (testFields(name, address)) {
                if (testName(name)){

                    new AlertDialog.Builder(AddCustomerActivity.this)
                            .setTitle(R.string.dialog_title)
                            .setMessage(R.string.add_customer_message)
                            .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int wich){
                                    addCustomerAtDB(name, address);
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
                    Toast.makeText(this, R.string.toast_name_exists, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.toast_fields_empty, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Method that checks if the fields that the user must fill contain the necessary data
     *
     * @param name    Value entered by the user in the name field
     * @param address Value entered by the user in the address field
     * @return True if the fields contain information. False if the fields are empty
     */
    private boolean testFields(String name, String address) {
        if (name.length() == 0) {
            return false;
        }

        if (address.length() == 0) {
            return false;
        }

        return true;
    }

    /**
     * Method that checks if there is already an element with the name passed by parameter in the
     * database
     *
     * @param name Customer name
     * @return True if there is no customer with that name. False if it exists
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean testName(String name){
        int count = 0;
        for (Customer customer : customers){
            if (Objects.equals(customer.getName(), name)) {
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
     * Method that updates the information of an existing client in the database
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateCustomer() {
        final String name = customerNameField.getText().toString();
        final String address = customerAddresField.getText().toString();

        if (testFields(name, address)) {

            new AlertDialog.Builder(AddCustomerActivity.this)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.update_customer_message)
                    .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int wich) {
                            updateCustomerAtDB(name, address);
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
     * Method that deletes a customer from the database
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void deleteCustomer() {
        String name = customerNameField.getText().toString();
        String address = customerAddresField.getText().toString();

        if (testFields(name, address)) {
            if (canDeleteCustomer(customerID)) {

                new AlertDialog.Builder(AddCustomerActivity.this)
                        .setTitle(R.string.dialog_title)
                        .setMessage(R.string.delete_customer_message)
                        .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int wich){
                                deleteCustomerAtDB(customerID);
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
                Toast.makeText(this, R.string.toast_customer_associated_order, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, R.string.toast_fields_empty, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method that checks if it is safe to delete the customer
     *
     * @param customerToDelete Customer ID to be deleted
     * @return True if the customer does not have associated orders.
     * False if he has no associated commands
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean canDeleteCustomer(String customerToDelete) {
        int count = 0;

        for (Order order : orders) {
            if (Objects.equals(order.getIdCustomer(), customerToDelete)) {
                count++;
                break;
            }
        }

        if (count != 0) {
            return false;
        }

        return true;
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
     * Method that collects information from customers stored in the database to fill the
     * customers Array
     */
    private void getCustomers() {
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, URL_TO_GET_CUSTOMERS,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray jsonArray;
                    try {
                        jsonArray = response.getJSONArray("data");
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), ": ERROR: Empty reply", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {

                        try {
                            JSONObject customerReply = jsonArray.getJSONObject(i);

                            String id = customerReply.getString("IDCustomer");
                            String name = customerReply.getString("name");
                            String address = customerReply.getString("address");

                            Customer newCustomer = new Customer(id, name, address);

                            customers.add(newCustomer);

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
     * Method that performs the insertion of a customer in the database
     *
     * @param name Customer name
     * @param address Customer address
     */
    public void addCustomerAtDB(String name, String address){
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("address", address);
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

                            customerID = String.valueOf(reply.getInt("IDCustomer"));

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
     * Method that performs the updating of client data in the database
     *
     * @param name Customer name
     * @param address Customer address
     */
    public void updateCustomerAtDB(String name, String address){
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("IDCustomer", Integer.valueOf(customerID));
            jsonObject.put("name", name);
            jsonObject.put("address", address);

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
     * Method that deletes the customer in the database
     *
     * @param customerToDelete Customer id
     */
    public void deleteCustomerAtDB(String customerToDelete){
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("IDCustomer", Integer.valueOf(customerToDelete));
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

        savedInstanceState.putString("customerName", customerNameField.getText().toString());
        savedInstanceState.putString("customerAddress", customerAddresField.getText().toString());
        savedInstanceState.putString("customerID", customerID);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        customerNameField.setText(savedInstanceState.getString("customerName"));
        customerAddresField.setText(savedInstanceState.getString("customerAddress"));
        customerID = savedInstanceState.getString("customerID");
    }
}