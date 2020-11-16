package eii.ulpgc.es.orders;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import eii.ulpgc.es.orders.Fragments.SelectCustomerFragmentList;
import eii.ulpgc.es.orders.Fragments.SelectProductFragmentList;

public class SelectActivity extends AppCompatActivity {

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
        Boolean update = intent.getBooleanExtra("update", false);

        if (willSelectCustomers) {
            SelectCustomerFragmentList selectCustomerFragmentList = new SelectCustomerFragmentList();

            Bundle bundle = new Bundle();
            bundle.putString("code", code);
            bundle.putString("productName", productName);
            bundle.putBoolean("update", update);

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
