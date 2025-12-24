package es.upm.etsisi.poo;

import java.util.Arrays;

public class BaseProduct extends Product {
	/**
	 * The percent cost added to the price of an item per personalization’
	 */
	public static final float PERSONALIZATION_EXTRA_PERCENT = 0.1f;
	
    public enum Category {
        MERCH	   	(0.00f, 3),
        STATIONERY 	(0.05f, 0),
        CLOTHES	    (0.07f, 5),
        BOOK	   	(0.10f, 0),
        ELECTRONICS	(0.03f, 2);

        public final float discountPercent;
        public final int maxPersonalizations;

        private Category(float discountPercent, int maxPersonalizations) {
            this.discountPercent = discountPercent;
            this.maxPersonalizations = maxPersonalizations;
        }

        public float getDiscountPercent() {
            return this.discountPercent;
        }
		
        public int getMaxPersonalizations() {
			return this.maxPersonalizations;
		}
    }

    private Category category;
	private int maxPersonalizations;
	private boolean personalized;

    public BaseProduct(int id, String name, double price, String category, int maxPersonalizations, boolean personalized) throws IllegalArgumentException{
        super(id, name, price);
        Category parsedCategory = Category.valueOf(category);
        if (maxPersonalizations < 0 || maxPersonalizations > parsedCategory.getMaxPersonalizations())
            throw new IllegalArgumentException("Expected a number between 0 and " + parsedCategory.getMaxPersonalizations() + " for max personalizations, got " + maxPersonalizations);

        this.category = parsedCategory;
        this.maxPersonalizations = maxPersonalizations;
		this.personalized = personalized;
    }

	public int getMaxPersonalizations() {
		return this.maxPersonalizations;
	}
    public Category getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = Category.valueOf(category);
    }
    public boolean getPersonalized() {return this.personalized;}

	@Override
    public String toString() {
		return toString(null);
	}
	
    public String toString(String[] personalizations) {
		StringBuilder sb = new StringBuilder();
		
		String className = "Product";
		double effectivePrice = super.getPrice();
		
		if (this.personalized) {
			className = "ProductPersonalized";
			if (personalizations != null) { 
				effectivePrice += super.getPrice() * personalizations.length * PERSONALIZATION_EXTRA_PERCENT;
			}
		}
		
		sb.append(String.format("{class:%s, id:%d, name:'%s', category:%s, price:%.1f",
								className, this.id, super.getName(), this.category, effectivePrice));
		
		if (this.personalized) {
			sb.append(String.format(", maxPersonal:%d", this.maxPersonalizations));
			if (personalizations != null) {
				sb.append(", personalizationList:");
				sb.append(Arrays.toString(personalizations));
			}
		}
		
		sb.append("}");
        return sb.toString();
    }

	// NOTE(enrique): Any BaseProduct is multiple of any product.
	// Meaning there can be many instances of it in a ticket.

	@Override
	public boolean duplicateOf(Product product) {
		return false; 
	}
}
