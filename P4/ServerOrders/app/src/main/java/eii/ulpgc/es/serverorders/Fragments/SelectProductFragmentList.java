package eii.ulpgc.es.serverorders.Fragments;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

import eii.ulpgc.es.serverorders.Adapters.SelectProductAdapter;
import eii.ulpgc.es.serverorders.AddOrderActivity;
import eii.ulpgc.es.serverorders.Models.Product;
import eii.ulpgc.es.serverorders.R;

/**
 * Created by Daniel on 13/04/2017.
 */

public class SelectProductFragmentList extends ListFragment {

    private static final String URL_TO_GET_PRODUCTS = "http://tip.dis.ulpgc.es/ventas/server.php?QueryProducts";

    private ArrayList<Product> products;
    private ListView listView;
    private SelectProductAdapter adapter;

    private String code;
    private String customerName;
    private Boolean update;
    private String orderID;
    private String date;
    private String price;
    private String quantity;
    private String IDCustomer;
    private String IDProduct;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select, container, false);

        code = getArguments().getString("code");
        customerName = getArguments().getString("customerName");
        update = getArguments().getBoolean("update");
        orderID = getArguments().getString("orderID");
        date = getArguments().getString("date");
        price = getArguments().getString("price");
        quantity = getArguments().getString("quantity");
        IDCustomer = getArguments().getString("IDCustomer");
        IDProduct = getArguments().getString("IDProduct");

        listView = (ListView) view.findViewById(android.R.id.list);

        products = new ArrayList<>();

        getProducts();

        return view;
    }


    /**
     * Method that collects information from products stored in the database to fill the list
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getProducts() {
        final RequestQueue queue = Volley.newRequestQueue(getContext());

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
                        Toast.makeText(getContext(), ": ERROR: Empty reply", Toast.LENGTH_SHORT).show();
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
                    adapter = new SelectProductAdapter(products, getContext());

                    listView.setAdapter(adapter);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        switch (response.statusCode) {
                            case 500:
                                Toast.makeText(getContext(), R.string.error_500, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Product selectedProduct = (Product) getListView().getItemAtPosition(position);

        Intent intent = new Intent(getContext(), AddOrderActivity.class);
        intent.putExtra("code", code);
        intent.putExtra("customerName", customerName);
        intent.putExtra("productName", selectedProduct.getName());
        intent.putExtra("comeFromSelect", update);
        intent.putExtra("orderID", orderID);
        intent.putExtra("date", date);
        intent.putExtra("price", selectedProduct.getPrice());
        intent.putExtra("quantity", quantity);
        intent.putExtra("IDCustomer", IDCustomer);
        intent.putExtra("IDProduct", selectedProduct.getId());

        startActivity(intent);

    }
}