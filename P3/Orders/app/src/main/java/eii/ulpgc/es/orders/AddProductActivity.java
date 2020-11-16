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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Objects;

import eii.ulpgc.es.orders.DataBase.Contract.StructureDB;
import eii.ulpgc.es.orders.DataBase.OrdersDB;

public class AddProductActivity extends AppCompatActivity {

    private EditText productNameField;
    private EditText productDescriptionField;
    private EditText productPriceField;
    private boolean update;
    private Integer productID;

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

        Intent intent = getIntent();
        productNameField.setText((String) intent.getSerializableExtra("name"));
        productDescriptionField.setText((String) intent.getSerializableExtra("description"));

        if (Objects.equals(productNameField.getText().toString(), "")) {
            update = false;
        } else {
            update = true;
            productID = getProductID(productNameField.getText().toString(),
                    productDescriptionField.getText().toString());
            DecimalFormat df = new DecimalFormat("0.00##");
            String price = df.format(getProductPrice(productID));
            productPriceField.setText(price);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (update){
            getMenuInflater().inflate(R.menu.update_product_menu, menu);
        }else{
            getMenuInflater().inflate(R.menu.add_product_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

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
     * Method responsible for making the field checks to add a product,
     * in addition to generating the confirmation dialog
     */
    private void addProduct() {
        if (update) {
            updateProduct();
        } else {
            final String name = productNameField.getText().toString();
            final String description = productDescriptionField.getText().toString();
            final String price = productPriceField.getText().toString();

            if (testFields(name, description, price)) {

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

        if (price.length() == 0) {
            return false;
        }

        return true;
    }

    /**
     * Method that makes the necessary checks of the fields to perform the update of a product,
     * besides generating the confirmation dialog
     */
    private void updateProduct() {
        final String name = productNameField.getText().toString();
        final String description = productDescriptionField.getText().toString();
        final String price = productPriceField.getText().toString();

        if (testFields(name, description, price)) {

            new AlertDialog.Builder(AddProductActivity.this)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.update_product_message)
                    .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int wich){
                            updateProductAtDB(name, description, price);
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
     * Method that searches the database for the id corresponding to the product to
     * which the name and the address entered as parameters belongs
     *
     * @param name        Product name
     * @param description Product description
     * @return Product id if exists
     */
    private int getProductID(String name, String description) {
        Integer id = 0;
        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor result = bd.rawQuery("select _id from products where name=? and description=?",
                new String[]{name, description});

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
     * Method that makes the necessary checks of the fields to carry out the deletion of a product,
     * besides generating the confirmation dialog
     */
    private void deleteProduct() {
        final Integer productToDelete;
        String name = productNameField.getText().toString();
        String description = productDescriptionField.getText().toString();
        String price = productPriceField.getText().toString();

        if (testFields(name, description, price)) {
            productToDelete = getProductID(name, description);
            if (productToDelete == 0) {
                Toast.makeText(this, R.string.toast_failed_delete_product, Toast.LENGTH_SHORT).show();
            } else {
                if (canDeleteProduct(productToDelete)) {

                    new AlertDialog.Builder(AddProductActivity.this)
                            .setTitle(R.string.dialog_title)
                            .setMessage(R.string.delete_product_message)
                            .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int wich){
                                    deleteProductAtDB(productToDelete);
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
    private boolean canDeleteProduct(int productToDelete) {
        int count = 0;
        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor result = bd.rawQuery("select count(*) from orders where idproduct=?",
                new String[]{String.valueOf(productToDelete)});

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
     * Search in the database the price associated with the product with the id passed as parameter
     *
     * @param id Product id
     * @return Product price
     */
    private double getProductPrice(int id) {
        double price = 0.0;
        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor result = bd.rawQuery("select price from products where _id=?",
                new String[]{String.valueOf(id)});

        if ((result != null) && result.moveToFirst()) {
            price = result.getDouble(0);
        }

        if (result != null) {
            result.close();
        }

        bd.close();

        return price;
    }

    /**
     * Method that performs the insertion of a product in the database
     *
     * @param name Product name
     * @param description Product description
     * @param price Product price
     */
    public void addProductAtDB(String name, String description, String price){
        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues newProduct = new ContentValues();
        newProduct.put(StructureDB.COLUMN_PRODUCT_NAME, name);
        newProduct.put(StructureDB.COLUMN_PRODUCT_DESCRIPTION, description);
        newProduct.put(StructureDB.COLUMN_PRODUCT_PRICE, Float.parseFloat(price));

        bd.insert(StructureDB.PRODUCTS_TABLE_NAME, null, newProduct);

        bd.close();

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
        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues product = new ContentValues();
        product.put(StructureDB.COLUMN_PRODUCT_NAME, name);
        product.put(StructureDB.COLUMN_PRODUCT_DESCRIPTION, description);
        product.put(StructureDB.COLUMN_PRODUCT_PRICE, Float.parseFloat(price));

        bd.update(StructureDB.PRODUCTS_TABLE_NAME, product,
                StructureDB.COLUMN_IDPRODUCT + "=" + productID, null);

        bd.close();

        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Method that deletes the product in the database
     *
     * @param productToDelete Product id
     */
    public void deleteProductAtDB(int productToDelete){
        OrdersDB admin = new OrdersDB(this);

        SQLiteDatabase bd = admin.getWritableDatabase();
        bd.delete(StructureDB.PRODUCTS_TABLE_NAME,
                StructureDB.COLUMN_IDPRODUCT + "=" + productToDelete + "", null);

        bd.close();

        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString("productName", productNameField.getText().toString());
        savedInstanceState.putString("productDescription", productDescriptionField.getText().toString());
        savedInstanceState.putString("productPrice", productPriceField.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        productNameField.setText(savedInstanceState.getString("productName"));
        productDescriptionField.setText(savedInstanceState.getString("productDescription"));
        productPriceField.setText(savedInstanceState.getString("productPrice"));
    }
}
