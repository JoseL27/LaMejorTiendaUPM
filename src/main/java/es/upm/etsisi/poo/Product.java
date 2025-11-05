package es.upm.etsisi.poo;

public abstract class Product {

    public static final int PRODUCT_MAX_NAME_LENGTH = 100; // E1: product name contains no more than 100 characters

    public final int id;
    private String name;
    private double price;

    // constructor
    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {return id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public double getPrice() {return price;}

    public void setPrice(double price) {this.price = price;}

	public double getMultipliedPrice(int amount) {
		return amount * price;
	}

	public double getAvailableDiscountPercent()	{
		return 0;
	}

	public abstract boolean canDuplicate();

	public abstract boolean duplicateOf(Product product);
}
