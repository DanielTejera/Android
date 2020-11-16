package eii.ulpgc.es.serverorders.Models;

/**
 * Created by Daniel on 14/04/2017.
 */

public class Customer {

    private String id;
    private String name;
    private String addres;

    public Customer(String id, String name, String addres) {
        this.id = id;
        this.name = name;
        this.addres = addres;
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

    public String getAddres() {
        return addres;
    }

    public void setAddres(String addres) {
        this.addres = addres;
    }
}
