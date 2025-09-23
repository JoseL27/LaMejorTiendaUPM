package es.upm.etsisi.poo;

public class Product {
	
	public enum Category {
		MERCH		(0.00f),
		PAPELERIA	(0.05f),
		ROPA		(0.07f),
		LIBRO		(0.01f),
		ELECTRONICA	(0.03f);

		private final float discountPercent;

		Category(float discountPercent) {
			this.discountPercent = discountPercent;
		}

		private float getDiscountPercent() {
			return discountPercent;
		}
	}
	
	private int id;
	private String name;
	private Category category;
	private double price;

	// constructor
	// getters y setters
}
