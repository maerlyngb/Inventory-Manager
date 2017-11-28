package io.maerlyn.inventorymanager.model;

/**
 * Model class for books
 *
 * @author Maerlyn Broadbent
 */
public class Book {
    private long id;
    private Supplier supplier;
    private String name;
    private int price;
    private int quantity;
    private int imageResourceId;

    public Book(Supplier supplier, String name, int price, int quantity, int imageResourceId) {
        this.supplier = supplier;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageResourceId = imageResourceId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPriceString() {
        final int centsInDollar = 100;
        double dollarPrice = (double) this.price / centsInDollar;
        return "$" + String.valueOf(dollarPrice);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
