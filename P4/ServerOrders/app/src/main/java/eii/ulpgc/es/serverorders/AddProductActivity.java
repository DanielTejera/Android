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

import eii.ulpgc.es.serverorders.Models.Order;
import eii.ulpgc.es.serverorders.Models.Product;


public class AddProductActivity extends AppCompatActivity {

    private static final String URL_TO_GET_PRODUCTS = "http://tip.dis.ulpgc.es/ventas/server.php?QueryProducts";
    private static final String URL_TO_GET_ORDERS = "http://tip.dis.ulpgc.es/ventas/server.php?QueryOrders";
    private static final String URL_TO_INSERT = "http://tip.dis.ulpgc.es/ventas/server.php?InsertProduct";
    private static final String URL_TO_UPDATE = "http://tip.dis.ulpgc.es/ventas/server.php?UpdateProduct";
    private static final String URL_TO_DELETE = "http://tip.dis.ulpgc.es/ventas/server.php?DeleteProduct";

    private EditText productNameField;
    private EditText productDescriptionField;
    private EditText productPriceField;
    private boolean update;
    private String productID;
    private ArrayList<Order> orders;
    private ArrayList<Product> products;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.add_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.add_product_actionbar_title);
        }

        productNameField = (EditText) findViewById(R.id.product_name_field);
        productDescriptionField = (EditText) findViewById(R.id.product_description_field);
        productPriceField = (EditText) findViewById(R.id.product_price_field);

        products = new ArrayList<>();
        orders = new ArrayList<>();

        Intent intent = getIntent();
        productNameField.setText((String) intent.getSerializableExtra("name"));
        productDescriptionField.setText((String) intent.getSerializableExtra("description"));
        productID = (String) intent.getSerializableExtra("id");
        productPriceField.setText((String) intent.getSerializableExtra("price"));

        if (Objects.equals(productNameField.getText().toString(), "")) {
            update = false;
        } else {
            update = true;
            getOrders();
        }

        getProducts();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if(update){
            getMenuInflater().inflate(R.menu.update_product_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.add_product_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_product:
                addProduct();
                return true;
            case R.id.action_delete_product:
                deleteProduct();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Responsible method to add a product to the database
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addProduct() {
        if (update) {
            updateProduct();
        } else {
            final String name = productNameField.getText().toString();
            final String description = productDescriptionField.getText().toString();
            final String price = productPriceField.getText().toString();

            if (testFields(name, description, price)) {
                if(testName(name)){

                    new AlertDialog.Builder(AddProductActivity.this)
                            .setTitle(R.string.dialog_title)
                            .setMessage(R.string.add_product_message)
                            .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int wich){
                                    addProductAtDB(name, description, price);
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

                }else{
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
     * @param name        Product name
     * @param description Product description
     * @param price       Product price
     * @return True if the fields contain information. False if the fields are empty
     */
    private boolean testFields(String name, String description, String price) {
        if (name.length() == 0) {
            return false;
        }

        if (description.length() == 0) {
            return false;
        }

        return price.length() != 0;

    }

    /**
     * Method that checks if there is already an element with the name passed by parameter in the
     * database
     *
     * @param name New product name
     * @return True if there is no product with that name. False if it exists
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean testName(String name){
        int count = 0;
        for (Product product : products){
            if (Objects.equals(product.getName(), name)) {
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
     * Method that updates the information of an existing product in the database
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateProduct() {
        final String name = productNameField.getText().toString();
        final String description = productDescriptionField.getText().toString();
        final String price = productPriceField.getText().toString();

        if (testFields(name, description, price)) {

            new AlertDialog.Builder(AddProductActivity.this)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.update_product_message)
                    .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int wich) {
                            updateProductAtDB(name, description, price);
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
     * Method that deletes a product from the database
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void deleteProduct() {

        String name = productNameField.getText().toString();
        String description = productDescriptionField.getText().toString();
        String price = productPriceField.getText().toString();

        if (testFields(name, description, price)) {
            if (canDeleteProduct(name)) {


                new AlertDialog.Builder(AddProductActivity.this)
                        .setTitle(R.string.dialog_title)
                        .setMessage(R.string.delete_product_message)
                        .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int wich){
                                deleteProductAtDB(productID);
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
                Toast.makeText(this, R.string.toast_product_associated_order, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.toast_fields_empty, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method that checks if it is safe to delete the product
     *
     * @param productToDelete Product ID to be deleted
     * @return True if the product does not have associated orders.
     * False if he has no associated commands
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean canDeleteProduct(String productToDelete) {
        int count = 0;

        for (Order order : orders) {
            if (Objects.equals(order.getProductName(), productToDelete)) {
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
                                Toast.makeText(getApplicationContext(), R.string.error_500, Toast.LENGTH_SHORT).show();
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
     * Method that collects information from products stored in the database to fill the
     * products Array
     */
    private void getProducts() {
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, URL_TO_GET_PRODUCTS,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray jsonArray;
                    Log.d("Products Response ->", response.toString());
                    try {
                        jsonArray = response.getJSONArray("data");
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), ": ERROR: Empty reply", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {

                        try {
                            JSONObject productReply = jsonArray.getJSONObject(i);

                            String id = productReply.getString("IDProduct");
                            String name = productReply.getString("name");
                            String price = productReply.getString("price");
                            String description = productReply.getString("description");


                            Product newProduct = new Product(id, name, price, description);

                            products.add(newProduct);

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
                                Toast.makeText(getApplicationContext(), R.string.error_500, Toast.LENGTH_SHORT).show();
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
     * Method that performs the insertion of a product in the database
     *
     * @param name Product name
     * @param description Product description
     * @param price Product price
     */
    public void addProductAtDB(String name, String description, String price){
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("description", description);
            jsonObject.put("price", Float.valueOf(price));
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

                            productID = String.valueOf(reply.getInt("IDProduct"));

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
     * @param name Product name
     * @param description Product description
     * @param price Product price
     */
    public void updateProductAtDB(String name, String description, String price){
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("IDProduct", Integer.valueOf(productID));
            jsonObject.put("name", name);
            jsonObject.put("description", description);
            jsonObject.put("price", Float.valueOf(price));
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
     * Method that deletes the product in the database
     *
     * @param productToDelete Product id
     */
    public void deleteProductAtDB(String productToDelete){
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("IDProduct", Integer.valueOf(productToDelete));
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

        savedInstanceState.putString("productName", productNameField.getText().toString());
        savedInstanceState.putString("productDescription", productDescriptionField.getText().toString());
        savedInstanceState.putString("productPrice", productPriceField.getText().toString());
        savedInstanceState.putString("productID", productID);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        productNameField.setText(savedInstanceState.getString("productName"));
        productDescriptionField.setText(savedInstanceState.getString("productDescription"));
        productPriceField.setText(savedInstanceState.getString("productPrice"));
        productID = savedInstanceState.getString("productID");
    }
}