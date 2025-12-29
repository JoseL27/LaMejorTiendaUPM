package es.upm.etsisi.poo;

public abstract class Product extends InventoryItem {
    
    public static final int PRODUCT_MAX_NAME_LENGTH = 100; // E1: product name contains no more than 100 characters
    
    private String name;
    private double price;
    // constructor
    
    protected Product(int id, String name, double price) throws IllegalArgumentException{
        super(id);
        if (name == null) throw new IllegalArgumentException("Product name can not be null");
        if (!isValidName(name)) throw new IllegalArgumentException(name + " is not a valid product name");
        if (!isValidPrice(price)) throw new IllegalArgumentException(price + " is not a valid product price");
        this.name = name;
        this.price = price;
    }
    
    public static boolean isValidName(String nameToCheck) {
        return nameToCheck.length() < PRODUCT_MAX_NAME_LENGTH;
    }
    
    public static boolean isValidPrice(double priceToCheck) {
        return priceToCheck > 0;
    }
    
    public String getName() {return name;}
    
    public void setName(String name) {
        if (!isValidName(name)) throw new IllegalArgumentException(name + " is not a valid product name");
        this.name = name;
    }
    
    public double getPrice() {return price;}
    
    public void setPrice(double price) {
        if (!isValidPrice(price)) throw new IllegalArgumentException(price + " is not a valid product price");
        this.price = price;
    }
    
}
