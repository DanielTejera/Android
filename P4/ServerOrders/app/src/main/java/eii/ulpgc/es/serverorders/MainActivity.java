package eii.ulpgc.es.serverorders;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import eii.ulpgc.es.serverorders.Adapters.CustomersAdapter;
import eii.ulpgc.es.serverorders.Adapters.OrdersAdapter;
import eii.ulpgc.es.serverorders.Adapters.ProductsAdapter;
import eii.ulpgc.es.serverorders.Models.Customer;
import eii.ulpgc.es.serverorders.Models.Order;
import eii.ulpgc.es.serverorders.Models.Product;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager mViewPager;
    private DrawerLayout drawer;
    private TabLayout tabLayout;
    public static ActionBar actionBar;

    private static String[] tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tabs = new String[]{getString(R.string.customers_tab_name),
                getString(R.string.products_tab_name), getString(R.string.orders_tab_name)};

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                actionBar.setTitle(tabs[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        actionBar.setTitle(tabs[0]);
    }

    /**
     * Internal class representing the fragment to be displayed under the tabs
     */
    public static class PlaceholderFragment extends ListFragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private int content = 1; // 1 Customers, 2 Products, 3 Orders

        private ListView customersListView;
        private ListView productsListView;
        private ListView ordersListView;

        ArrayList<Customer> customers;
        ArrayList<Product> products;
        ArrayList<Order> orders;

        private static final String URL_TO_GET_CUSTOMERS = "http://tip.dis.ulpgc.es/ventas/server.php?QueryCustomers";
        private static final String URL_TO_GET_PRODUCTS = "http://tip.dis.ulpgc.es/ventas/server.php?QueryProducts";
        private static final String URL_TO_GET_ORDERS = "http://tip.dis.ulpgc.es/ventas/server.php?QueryOrders";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = null;
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_customers_table, container, false);

                    customersListView = (ListView) rootView.findViewById(android.R.id.list);

                    customers = new ArrayList<>();

                    getCustomers();

                    content = 1;
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_products_table, container, false);

                    productsListView = (ListView) rootView.findViewById(android.R.id.list);

                    products = new ArrayList<>();

                    getProducts();

                    content = 2;
                    break;
                case 3:
                    rootView = inflater.inflate(R.layout.fragment_orders_table, container, false);

                    ordersListView = (ListView) rootView.findViewById(android.R.id.list);

                    orders = new ArrayList<>();

                    getOrders();

                    content = 3;
                    break;
            }
            return rootView;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);

            switch (content) {
                case 1:
                    Customer selectedCustomer = (Customer) getListView().getItemAtPosition(position);

                    Intent addCustomerIntent = new Intent(getContext(), AddCustomerActivity.class);
                    addCustomerIntent.putExtra("id", selectedCustomer.getId());
                    addCustomerIntent.putExtra("name", selectedCustomer.getName());
                    addCustomerIntent.putExtra("address", selectedCustomer.getAddres());

                    startActivity(addCustomerIntent);
                    break;
                case 2:
                    Product selectedProduct = (Product) getListView().getItemAtPosition(position);

                    Intent addProductIntent = new Intent(getContext(), AddProductActivity.class);
                    addProductIntent.putExtra("id", selectedProduct.getId());
                    addProductIntent.putExtra("name", selectedProduct.getName());
                    addProductIntent.putExtra("description", selectedProduct.getDescription());
                    addProductIntent.putExtra("price", selectedProduct.getPrice());

                    startActivity(addProductIntent);
                    break;
                case 3:
                    Order selectedOrder = (Order) getListView().getItemAtPosition(position);

                    Intent addOrderIntent = new Intent(getContext(), AddOrderActivity.class);
                    addOrderIntent.putExtra("code", selectedOrder.getCode());
                    addOrderIntent.putExtra("IDOrder", selectedOrder.getIdOrder());
                    addOrderIntent.putExtra("IDCustomer", selectedOrder.getIdCustomer());
                    addOrderIntent.putExtra("IDProduct", selectedOrder.getIdProduct());
                    addOrderIntent.putExtra("customerName", selectedOrder.getCustomerName());
                    addOrderIntent.putExtra("productName", selectedOrder.getProductName());
                    addOrderIntent.putExtra("price", selectedOrder.getPrice());
                    addOrderIntent.putExtra("quantity", selectedOrder.getQuantity());
                    addOrderIntent.putExtra("date", selectedOrder.getDate());
                    addOrderIntent.putExtra("update", true);

                    startActivity(addOrderIntent);
                    break;
                default:
                    break;
            }
        }

        /**
         * Method that collects information from customers stored in the database to fill the list
         * of customers
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

                        CustomersAdapter customersAdapter = new CustomersAdapter(customers, getContext());
                        customersListView.setAdapter(customersAdapter);
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

        /**
         * Method that collects information from products stored in the database to fill the list
         * of products
         */
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

                        ProductsAdapter productsAdapter = new ProductsAdapter(products, getContext());
                        productsListView.setAdapter(productsAdapter);
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

        /**
         * Method that collects information from orders stored in the database to fill the list
         * of orders
         */
        private void getOrders() {
            final RequestQueue queue = Volley.newRequestQueue(getContext());

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
                            Toast.makeText(getContext(), ": ERROR: Empty reply", Toast.LENGTH_SHORT).show();
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

                        OrdersAdapter ordersAdapter = new OrdersAdapter(orders, getContext());
                        ordersListView.setAdapter(ordersAdapter);

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
    }

    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }


        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            switch (tabLayout.getSelectedTabPosition()) {
                case 0:
                    Intent addCustomerIntent = new Intent(this, AddCustomerActivity.class);
                    startActivity(addCustomerIntent);
                    break;
                case 1:
                    Intent addProductIntent = new Intent(this, AddProductActivity.class);
                    startActivity(addProductIntent);
                    break;
                case 2:
                    Intent addOrderIntent = new Intent(this, AddOrderActivity.class);
                    startActivity(addOrderIntent);
                    break;
                default:
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_customers:
                tabLayout.getTabAt(0).select();
                break;
            case R.id.nav_products:
                tabLayout.getTabAt(1).select();
                break;
            default:
                tabLayout.getTabAt(2).select();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
