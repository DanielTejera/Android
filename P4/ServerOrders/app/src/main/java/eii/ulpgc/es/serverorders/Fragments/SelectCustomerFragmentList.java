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
import java.util.Objects;

import eii.ulpgc.es.serverorders.Adapters.SelectCustomerAdapter;
import eii.ulpgc.es.serverorders.AddOrderActivity;
import eii.ulpgc.es.serverorders.Models.Customer;
import eii.ulpgc.es.serverorders.R;

/**
 * Created by Daniel on 11/04/2017.
 */

public class SelectCustomerFragmentList extends ListFragment {

    private static final String URL_TO_GET_CUSTOMERS = "http://tip.dis.ulpgc.es/ventas/server.php?QueryCustomers";

    private ArrayList<Customer> customers;
    private ListView listView;
    private SelectCustomerAdapter adapter;
    private String code;
    private String productName;
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
        productName = getArguments().getString("productName");
        update = getArguments().getBoolean("update");
        orderID = getArguments().getString("orderID");
        date = getArguments().getString("date");
        price = getArguments().getString("price");
        quantity = getArguments().getString("quantity");
        IDCustomer = getArguments().getString("IDCustomer");
        IDProduct = getArguments().getString("IDProduct");

        listView = (ListView) view.findViewById(android.R.id.list);

        customers = new ArrayList<>();

        getCustomers();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    /**
     * Method that collects information from customers stored in the database to fill the list
     */
    private void getCustomers() {
        final RequestQueue queue = Volley.newRequestQueue(getContext());

        JSONObject jsonObject = new JSONObject();
        try {
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, URL_TO_GET_CUSTOMERS,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray jsonArray;
                    Log.d("Customers Response ->", response.toString());
                    try {
                        jsonArray = response.getJSONArray("data");
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), ": ERROR: Empty reply", Toast.LENGTH_SHORT).show();
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
                    adapter= new SelectCustomerAdapter(customers,getContext());

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

        Customer selectedCustomer = (Customer) getListView().getItemAtPosition(position);

        Intent intent = new Intent(getContext(), AddOrderActivity.class);
        intent.putExtra("code", code);
        intent.putExtra("customerName", selectedCustomer.getName());
        intent.putExtra("productName", productName);
        intent.putExtra("comeFromSelect", true);
        intent.putExtra("update",update);
        intent.putExtra("orderID", orderID);
        intent.putExtra("date", date);
        intent.putExtra("price", price);
        intent.putExtra("quantity", quantity);
        intent.putExtra("IDCustomer", selectedCustomer.getId());
        intent.putExtra("IDProduct", IDProduct);

        startActivity(intent);

    }
}