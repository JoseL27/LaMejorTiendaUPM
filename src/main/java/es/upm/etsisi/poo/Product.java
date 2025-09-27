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

	public enum Field {
		NAME,
		CATEGORY,
		PRICE;
		
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
	
	private int id;
	private String name;
	private Category category;
	private double price;

	// constructor
	// getters y setters
}
