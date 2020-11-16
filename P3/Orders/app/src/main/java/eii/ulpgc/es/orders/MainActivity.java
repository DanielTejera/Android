package eii.ulpgc.es.orders;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.ArrayList;

import eii.ulpgc.es.orders.Adapters.CustomersAdapter;
import eii.ulpgc.es.orders.Adapters.OrdersAdapter;
import eii.ulpgc.es.orders.Adapters.ProductsAdapter;
import eii.ulpgc.es.orders.DataBase.OrdersDB;
import eii.ulpgc.es.orders.Models.Customer;
import eii.ulpgc.es.orders.Models.Order;
import eii.ulpgc.es.orders.Models.Product;

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
        ArrayList<Customer> customers;
        ArrayList<Product> products;
        ArrayList<Order> orders;

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

                    ListView customersListView = (ListView) rootView.findViewById(android.R.id.list);

                    customers = new ArrayList<>();

                    getCustomers();

                    CustomersAdapter customersAdapter = new CustomersAdapter(customers, getContext());

                    customersListView.setAdapter(customersAdapter);

                    content = 1;
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_products_table, container, false);

                    ListView productsListView = (ListView) rootView.findViewById(android.R.id.list);

                    products = new ArrayList<>();

                    getProducts();

                    ProductsAdapter productsAdapter = new ProductsAdapter(products, getContext());

                    productsListView.setAdapter(productsAdapter);

                    content = 2;
                    break;
                case 3:
                    rootView = inflater.inflate(R.layout.fragment_orders_table, container, false);

                    ListView ordersListView = (ListView) rootView.findViewById(android.R.id.list);

                    orders = new ArrayList<>();

                    getOrders();

                    OrdersAdapter ordersAdapter = new OrdersAdapter(orders, getContext());

                    ordersListView.setAdapter(ordersAdapter);

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
                    addCustomerIntent.putExtra("name", selectedCustomer.getName());
                    addCustomerIntent.putExtra("address", selectedCustomer.getAddres());

                    startActivity(addCustomerIntent);
                    break;
                case 2:
                    Product selectedProduct = (Product) getListView().getItemAtPosition(position);

                    Intent addProductIntent = new Intent(getContext(), AddProductActivity.class);
                    addProductIntent.putExtra("name", selectedProduct.getName());
                    addProductIntent.putExtra("description", selectedProduct.getDescription());

                    startActivity(addProductIntent);
                    break;
                case 3:
                    Order selectedOrder = (Order) getListView().getItemAtPosition(position);

                    Intent addOrderIntent = new Intent(getContext(), AddOrderActivity.class);
                    addOrderIntent.putExtra("code", selectedOrder.getCode());
                    addOrderIntent.putExtra("customerName", selectedOrder.getCustomerName());
                    addOrderIntent.putExtra("productName", selectedOrder.getProductName());
                    addOrderIntent.putExtra("update", true);

                    startActivity(addOrderIntent);
                    break;
                default:
                    break;
            }
        }

        /**
         * Method that collects information from customers stored in the database to fill the list
         */
        private void getCustomers() {
            OrdersDB admin = new OrdersDB(getContext());

            SQLiteDatabase bd = admin.getReadableDatabase();

            Cursor result = bd.rawQuery("select name,address from customers order by name", null);

            result.moveToFirst();
            while (!result.isAfterLast()) {
                customers.add(new Customer(result.getString(0), result.getString(1)));
                result.moveToNext();
            }

            result.close();

            bd.close();

        }

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

        /**
         * Method that collects information from orders stored in the database to fill the list
         */
        private void getOrders() {
            OrdersDB admin = new OrdersDB(getContext());

            SQLiteDatabase bd = admin.getReadableDatabase();

            Cursor result = bd.rawQuery("select code,quantity,idcustomer,idproduct from orders", null);

            result.moveToFirst();
            while (!result.isAfterLast()) {

                int customerID = result.getInt(2);

                Cursor customerName = bd.rawQuery("select name from customers where _id=?",
                        new String[]{String.valueOf(customerID)});

                customerName.moveToFirst();

                int productID = result.getInt(3);

                Cursor productName = bd.rawQuery("select name from products where _id=?",
                        new String[]{String.valueOf(productID)});

                productName.moveToFirst();

                orders.add(new Order(result.getString(0), result.getInt(1),
                        customerName.getString(0), productName.getString(0)));

                customerName.close();
                productName.close();

                result.moveToNext();
            }
            result.close();

            bd.close();
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
