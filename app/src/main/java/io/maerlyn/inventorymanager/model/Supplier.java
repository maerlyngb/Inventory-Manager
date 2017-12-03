package io.maerlyn.inventorymanager.model;

/**
 * Model class for suppliers
 *
 * @author Maerlyn Broadbent
 */
public class Supplier {
    private long id;
    private String name;
    private String email;
    private String phoneNum;

    public Supplier(long id, String name, String email, String phoneNum) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
    }

    public Supplier(String name, String email, String phoneNum) {
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
