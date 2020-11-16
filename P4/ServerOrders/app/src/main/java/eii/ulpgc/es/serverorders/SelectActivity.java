package eii.ulpgc.es.serverorders;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import eii.ulpgc.es.serverorders.Fragments.SelectCustomerFragmentList;
import eii.ulpgc.es.serverorders.Fragments.SelectProductFragmentList;

public class SelectActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        Intent intent = getIntent();
        boolean willSelectCustomers = intent.getBooleanExtra("customer", false);
        boolean willSelectProducts = intent.getBooleanExtra("product", false);
        String code = intent.getStringExtra("code");
        String productName = intent.getStringExtra("productName");
        String customerName = intent.getStringExtra("customerName");
        String orderID = intent.getStringExtra("orderID");
        String date = intent.getStringExtra("date");
        String price = intent.getStringExtra("price");
        String quantity = intent.getStringExtra("quantity");
        String IDCustomer = intent.getStringExtra("IDCustomer");
        String IDProduct = intent.getStringExtra("IDProduct");
        Boolean update = intent.getBooleanExtra("update", false);

        if (willSelectCustomers) {
            SelectCustomerFragmentList selectCustomerFragmentList = new SelectCustomerFragmentList();

            Bundle bundle = new Bundle();
            bundle.putString("code", code);
            bundle.putString("productName", productName);
            bundle.putBoolean("update", update);
            bundle.putString("orderID", orderID);
            bundle.putString("date", date);
            bundle.putString("price", price);
            bundle.putString("quantity", quantity);
            bundle.putString("IDCustomer", IDCustomer);
            bundle.putString("IDProduct", IDProduct);
            selectCustomerFragmentList.setArguments(bundle);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, selectCustomerFragmentList);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
        }

        if (willSelectProducts) {
            SelectProductFragmentList selectProductFragmentList = new SelectProductFragmentList();

            Bundle bundle = new Bundle();
            bundle.putString("code", code);
            bundle.putString("customerName", customerName);
            bundle.putBoolean("update", update);
            bundle.putString("orderID", orderID);
            bundle.putString("date", date);
            bundle.putString("price", price);
            bundle.putString("quantity", quantity);
            bundle.putString("IDCustomer", IDCustomer);
            bundle.putString("IDProduct", IDProduct);
            selectProductFragmentList.setArguments(bundle);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, selectProductFragmentList);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
    }
}