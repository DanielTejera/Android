package eii.ulpgc.es.orders.Fragments;

import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import eii.ulpgc.es.orders.Adapters.SelectCustomerAdapter;
import eii.ulpgc.es.orders.AddOrderActivity;
import eii.ulpgc.es.orders.DataBase.OrdersDB;
import eii.ulpgc.es.orders.Models.Customer;
import eii.ulpgc.es.orders.R;

/**
 * Created by Daniel on 11/04/2017.
 */

public class SelectCustomerFragmentList extends ListFragment {

    ArrayList<Customer> customers;
    ListView listView;
    private SelectCustomerAdapter adapter;
    private String code;
    private String productName;
    private Boolean update;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select, container, false);

        code = getArguments().getString("code");
        productName = getArguments().getString("productName");
        update = getArguments().getBoolean("update");

        listView = (ListView) view.findViewById(android.R.id.list);

        customers = new ArrayList<>();

        getCustomers();
        adapter= new SelectCustomerAdapter(customers,getContext());

        listView.setAdapter(adapter);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    /**
     * Method that collects information from customers stored in the database to fill the list
     */
    private void getCustomers() {
        OrdersDB admin = new OrdersDB(getContext());

        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor result = bd.rawQuery("select name,address from customers order by name", null);

        result.moveToFirst();
        while(!result.isAfterLast())
        {
            customers.add(new Customer(result.getString(0), result.getString(1)));
            result.moveToNext();
        }

        result.close();

        bd.close();

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

        startActivity(intent);

    }
}
