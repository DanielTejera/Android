package eii.ulpgc.es.serverorders.Models;

/**
 * Created by Daniel on 15/04/2017.
 */

public class Product {

    private String id;
    private String name;
    private String description;
    private String price;

    public Product(String id, String name, String price, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
