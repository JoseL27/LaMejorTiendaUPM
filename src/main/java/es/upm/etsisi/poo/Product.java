package es.upm.etsisi.poo;

public abstract class Product {

    public static final int PRODUCT_MAX_NAME_LENGTH = 100; // E1: product name contains no more than 100 characters

    public final int id;
    private String name;
    private double price;

    // constructor
    protected Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {return id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public double getPrice() {return price;}

    public void setPrice(double price) {this.price = price;}

	public abstract boolean duplicateOf(Product product);

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
    public  static boolean isValidName(String nameToCheck) {
        return nameToCheck.length() < Product.PRODUCT_MAX_NAME_LENGTH;
    }
	
    /**
     * Check if given price is valid (priceToCheck > 0)
     * @param priceToCheck Price double to check
     * @return true if valid, otherwise return false
     */
    public static boolean isValidPrice(double priceToCheck) {
        return priceToCheck > 0;
    }

	public static String validName(String name) throws Exception {
		if (!isValidName(name)) {
			throw new Exception("%s is not a valid name, too long");
		}
		return name;
	}

	public static int parseId(String strId) throws Exception {
		Integer productId = Integer.parseInt(strId);
		if (!isValidId(productId)) {
			throw new Exception("expected id greater or equal than zero");
		}
		return productId;
	}
}
