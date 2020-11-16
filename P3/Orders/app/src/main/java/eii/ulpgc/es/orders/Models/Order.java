package eii.ulpgc.es.orders.Models;

import java.util.Date;

/**
 * Created by Daniel on 15/04/2017.
 */

public class Order {

    private String code;
    private int quantity;
    private String customerName;
    private String productName;

    public Order(String code, int quantity, String customerName, String productName) {
        this.code = code;
        this.quantity = quantity;
        this.customerName = customerName;
        this.productName = productName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
