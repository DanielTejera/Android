package eii.ulpgc.es.orders.Models;

/**
 * Created by Daniel on 14/04/2017.
 */

public class Customer {
    private String name;
    private String addres;

    public Customer(String name, String addres) {
        this.name = name;
        this.addres = addres;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddres() {
        return addres;
    }

    public void setAddres(String addres) {
        this.addres = addres;
    }
}
