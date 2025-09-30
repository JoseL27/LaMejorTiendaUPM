package es.upm.etsisi.poo;

public class Product {
	public enum Category {
		MERCH	   	(0.00f, "MERCH"),
		STATIONERY 	(0.05f, "STATIONERY"),
		CLOTHES	    (0.07f, "CLOTHES"),
		BOOK	   	(0.01f, "BOOK"),
		ELECTRONICA	(0.03f, "ELECTRONICA");

		private final float discountPercent;
		private final String label;

		private Category(float discountPercent, String label) {
			this.discountPercent = discountPercent;
			this.label = label;
		}

		public float getDiscountPercent() {
			return this.discountPercent;
		}
		
		public String getLabel() {
			return this.label;
		}

		public static Category fromLabel(String label) {
			Category category = null;
			try {
				category = Category.valueOf(label.toUpperCase());
			} catch (Exception e) {
			} finally {
				return category;
			}
		}
	}

	/**
	 * Enum to reference a product 'Field', meaning a property of a product.
	 */
	public enum Field {
		NAME,
		CATEGORY,
		PRICE;

		/**
		 * Function to get a Field from a string. Used to facilitate parsing.
		 * Case insesitive match of the enum values name's (Basically Enum.valueOf with .toUpperCase)
		 * @param label  The string to match against
		 * @return       A valid Field if the match was successfull or NULL.
		 */
		public static Field fromLabel(String label) {
			Field field = null;
			try {
				field = Field.valueOf(label.toUpperCase());
			} catch (Exception e) {
			} finally {
				return field;
			}
		}		
	}
	
	public final int id;
	private String name;
	private Category category;
	private double price;

	// constructor
	public Product ( int id, String name, Category category, double price )
	{
		this.id = id;
		this.name = name;
		this.category = category;
		this.price = price;
	}

	public int compareTo ( Product p )
	{
		return this.name.compareTo ( p.name );
	}

	public boolean  equals ( Product p )
	{
		return this.id == p.id;
	}
	// getters y setters, estos no se usan, son shit code

	public Category category ()
	{
		return this.category;
	}

	public double price ( int amount )
	{
		return this.price * amount;
	}

	@Override

	public String toString ()
	{
		return  "{class:Product, id:" + this.id +
				", name:" + this.name + ", category:" + this.category +
				", price:" + this.price + "}";
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
