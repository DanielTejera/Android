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

import eii.ulpgc.es.orders.Adapters.SelectProductAdapter;
import eii.ulpgc.es.orders.AddOrderActivity;
import eii.ulpgc.es.orders.DataBase.OrdersDB;
import eii.ulpgc.es.orders.Models.Product;
import eii.ulpgc.es.orders.R;

/**
 * Created by Daniel on 13/04/2017.
 */

public class SelectProductFragmentList extends ListFragment {
    private ArrayList<Product> products;
    private ListView listView;
    private SelectProductAdapter adapter;

    private String code;
    private String customerName;
    private Boolean update;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select, container, false);

        code = getArguments().getString("code");
        customerName = getArguments().getString("customerName");
        update = getArguments().getBoolean("update");

        listView = (ListView) view.findViewById(android.R.id.list);

        products = new ArrayList<>();

        getProducts();
        adapter = new SelectProductAdapter(products, getContext());

        listView.setAdapter(adapter);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    /**
     * Method that collects information from products stored in the database to fill the list
     */
    private void getProducts() {
        OrdersDB admin = new OrdersDB(getContext());

        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor result = bd.rawQuery("select name,description,price from products order by name", null);

        result.moveToFirst();
        while (!result.isAfterLast()) {
            products.add(new Product(result.getString(0), result.getString(1), result.getDouble(2)));
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

        Product selectedProduct = (Product) getListView().getItemAtPosition(position);

        Intent intent = new Intent(getContext(), AddOrderActivity.class);
        intent.putExtra("code", code);
        intent.putExtra("customerName", customerName);
        intent.putExtra("productName", selectedProduct.getName());
        intent.putExtra("comeFromSelect", update);

        startActivity(intent);

    }
}
