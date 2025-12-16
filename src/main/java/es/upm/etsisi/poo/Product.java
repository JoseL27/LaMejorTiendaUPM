package es.upm.etsisi.poo;

public abstract class Product {

    public static final int PRODUCT_MAX_NAME_LENGTH = 100; // E1: product name contains no more than 100 characters

    public final int id;
    private String name;
    private double price;

    // constructor
    protected Product(int id, String name, double price) throws IllegalArgumentException{
        if (!isValidId(id)) throw new IllegalArgumentException(id + " is not a valid product id");
        if (name == null) throw new IllegalArgumentException("Product name can not be null");
        if (!isValidName(name)) throw new IllegalArgumentException(name + " is not a valid product name");
        if (!isValidPrice(price)) throw new IllegalArgumentException(price + " is not a valid product price");
        this.id = id;
        this.name = name;
        this.price = price;
    }

    /**
     * Checks if given ID is valid (idToCheck >= 0)
     * @param idToCheck numeric id to check
     * @return true if valid, otherwise return false
     */
    public static boolean isValidId(int idToCheck) {
        return idToCheck >= 0;
    }

    /**
     * Check if given name is valid (nameToCheck.length < 100)
     * @param nameToCheck Name string to check
     * @return true if valid, otherwise return false
     */
    public static boolean isValidName(String nameToCheck) {
        return nameToCheck.length() < PRODUCT_MAX_NAME_LENGTH;
    }

    /**
     * Check if given price is valid (priceToCheck > 0)
     * @param priceToCheck Price double to check
     * @return true if valid, otherwise return false
     */
    public static boolean isValidPrice(double priceToCheck) {
        return priceToCheck > 0;
    }

    public int getId() {return id;}

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

	public abstract boolean duplicateOf(Product product);
}
