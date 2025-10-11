package es.upm.etsisi.poo;

public class Product {
	public enum Category {
		MERCH	   	(0.00f),
		STATIONERY 	(0.05f),
		CLOTHES	    (0.07f),
		BOOK	   	(0.10f),
		ELECTRONICS	(0.03f);

		private final float discountPercent;

		private Category(float discountPercent) {
			this.discountPercent = discountPercent;
		}

		public float getDiscountPercent() {
			return this.discountPercent;
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
	
	private final int id;
	private String name;
	private Category category;
	private double price;

	// constructor
	public Product(int id, String name, Category category, double price) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.price = price;
	}

	@Override
	public String toString() {
		return String.format("{class:Product, id:%d, name:'%s', category:%s, price:%.1f}",
							 this.id, this.name, this.category, this.price);
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

	/**
	 * Checks if this is equal to another object, that has to be a Product, based on id, name, category and price
	 * @param obj Object to be compared to
	 * @return true, if the objects are equals under this criteria, false in other case
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getClass() != obj.getClass()) return false;

		Product otherProd = (Product)obj;
		return this.id == otherProd.id
			&& Utils.nullOrEquals(this.name, otherProd.name)
			&& Utils.nullOrEquals(this.category, otherProd.category)
			&& this.price == otherProd.price;
	}
}
