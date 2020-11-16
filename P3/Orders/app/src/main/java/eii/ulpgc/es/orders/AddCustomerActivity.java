package eii.ulpgc.es.orders;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

import eii.ulpgc.es.orders.DataBase.Contract.StructureDB;
import eii.ulpgc.es.orders.DataBase.OrdersDB;

public class AddCustomerActivity extends AppCompatActivity {

    private EditText customerNameField;
    private EditText customerAddresField;
    private Boolean update;
    private Integer customerID;

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

        Intent intent = getIntent();
        customerNameField.setText((String) intent.getSerializableExtra("name"));
        customerAddresField.setText((String) intent.getSerializableExtra("address"));

        if (Objects.equals(customerNameField.getText().toString(), "")) {
            update = false;
        } else {
            update = true;
            customerID = getCustomerID(customerNameField.getText().toString(),
                    customerAddresField.getText().toString());
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if(update){
            getMenuInflater().inflate(R.menu.update_customer_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.add_customer_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

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
     * Method responsible for making the field checks to add a customer,
     * in addition to generating the confirmation dialog
     */
    private void addCustomer() {
        if (update) {
            updateCustomer();
        } else {
            final String name = customerNameField.getText().toString();
            final String address = customerAddresField.getText().toString();

            if (testFields(name, address)) {

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
                Toast.makeText(this, R.string.toast_fields_empty, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Method that checks if the fields that the user must fill contain the necessary data
     *
     * @param name    Value entered by the user in the name field
     * @param address Value entered by the user in the addres field
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
     * Method that makes the necessary checks of the fields to perform the update of a customer,
     * besides generating the confirmation dialog
     */
    private void updateCustomer() {
        final String name = customerNameField.getText().toString();
        final String address = customerAddresField.getText().toString();

        if (testFields(name, address)) {

            new AlertDialog.Builder(AddCustomerActivity.this)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.update_customer_message)
                    .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int wich){
                            updateCustomerAtDB(name, address);
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
            Toast.makeText(this, R.string.toast_fields_empty, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method that searches the database for the id corresponding to the customer to
     * which the data entered as parameters belongs
     *
     * @param name    Customer name
     * @param address Customer address
     * @return Customer id if it exists
     */
    private int getCustomerID(String name, String address) {
        Integer id = 0;
        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor result = bd.rawQuery("select _id from customers where name=? and address=?",
                new String[]{name, address});

        if ((result != null) && result.moveToFirst()) {
            id = result.getInt(0);
        }

        if (result != null) {
            result.close();
        }

        bd.close();

        return id;
    }

    /**
     * Method that makes the necessary checks of the fields to carry out the deletion of a customer,
     * besides generating the confirmation dialog
     */
    private void deleteCustomer() {
        final Integer customerToDelete;
        String name = customerNameField.getText().toString();
        String address = customerAddresField.getText().toString();

        if (testFields(name, address)) {
            customerToDelete = getCustomerID(name, address);
            if (customerToDelete == 0) {
                Toast.makeText(this, R.string.toast_failed_delete_customer, Toast.LENGTH_SHORT).show();
            } else {
                if (canDeleteCustomer(customerToDelete)) {

                    new AlertDialog.Builder(AddCustomerActivity.this)
                            .setTitle(R.string.dialog_title)
                            .setMessage(R.string.delete_customer_message)
                            .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int wich){
                                    deleteCustomerAtDB(customerToDelete);
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
    private boolean canDeleteCustomer(int customerToDelete) {
        int count = 0;
        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor result = bd.rawQuery("select count(*) from orders where idcustomer=?",
                new String[]{String.valueOf(customerToDelete)});

        if ((result != null) && result.moveToFirst()) {
            count = result.getInt(0);
        }

        if (result != null) {
            result.close();
        }

        bd.close();

        if (count != 0) {
            return false;
        }
        return true;
    }

    /**
     * Method that performs the insertion of a customer in the database
     *
     * @param name Customer name
     * @param address Customer address
     */
    public void addCustomerAtDB(String name, String address){

        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues newCustomer = new ContentValues();
        newCustomer.put(StructureDB.COLUMN_CUSTOMER_NAME, name);
        newCustomer.put(StructureDB.COLUMN_CUSTOMER_ADDRESS, address);

        bd.insert(StructureDB.CUSTOMERS_TABLE_NAME, null, newCustomer);

        bd.close();

        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Method that performs the updating of client data in the database
     *
     * @param name Customer name
     * @param address Customer address
     */
    public void updateCustomerAtDB(String name, String address){
        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues customer = new ContentValues();
        customer.put(StructureDB.COLUMN_CUSTOMER_NAME, name);
        customer.put(StructureDB.COLUMN_CUSTOMER_ADDRESS, address);

        bd.update(StructureDB.CUSTOMERS_TABLE_NAME, customer,
                StructureDB.COLUMN_IDCUSTOMER + "=" + customerID, null);

        bd.close();

        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Method that deletes the customer in the database
     *
     * @param customerToDelete Customer id
     */
    public void deleteCustomerAtDB(int customerToDelete){
        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getWritableDatabase();
        bd.delete(StructureDB.CUSTOMERS_TABLE_NAME,
                StructureDB.COLUMN_IDCUSTOMER + "=" + customerToDelete + "", null);

        bd.close();

        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString("customerName", customerNameField.getText().toString());
        savedInstanceState.putString("customerAddress", customerAddresField.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        customerNameField.setText(savedInstanceState.getString("customerName"));
        customerAddresField.setText(savedInstanceState.getString("customerAddress"));
    }
}
