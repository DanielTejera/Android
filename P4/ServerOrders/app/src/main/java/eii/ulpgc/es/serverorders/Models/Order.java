package eii.ulpgc.es.serverorders.Models;

/**
 * Created by Daniel on 15/04/2017.
 */

public class Order {

    private String idOrder;
    private String idCustomer;
    private String idProduct;
    private String customerName;
    private String productName;
    private String code;
    private String price;
    private String quantity;
    private String date;

    public Order(String idOrder, String idCustomer, String idProduct, String customerName, String productName, String code, String price, String quantity, String date) {
        this.idOrder = idOrder;
        this.idCustomer = idCustomer;
        this.idProduct = idProduct;
        this.customerName = customerName;
        this.productName = productName;
        this.code = code;
        this.price = price;
        this.quantity = quantity;
        this.date = date;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
