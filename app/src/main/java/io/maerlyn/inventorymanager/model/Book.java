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
    private int price = 0;
    private int quantity = 0;
    private byte[] image;

    public Book() {

    }

    public Book(long id, Supplier supplier, String name, int price, int quantity, byte[] image) {
        this.id = id;
        this.supplier = supplier;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
    }

    public Book(Supplier supplier, String name, int price, int quantity, byte[] image) {
        this.supplier = supplier;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
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
        return String.valueOf(dollarPrice);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setImage(byte[] image){
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }

    /**
     * reduce quantity by one if we're not already at zero
     *
     * @return false if we're already at zero
     */
    public boolean sellBook() {
        if (this.quantity > 0) {
            this.quantity--;
            return true;
        }
        return false;
    }

    // increase quantity by 1
    public void orderBook() {
        this.quantity++;
    }
}
