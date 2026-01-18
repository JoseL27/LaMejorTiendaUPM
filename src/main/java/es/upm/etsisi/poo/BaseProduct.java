package es.upm.etsisi.poo;

import es.upm.etsisi.poo.exceptions.InvalidDataException;
import java.util.Arrays;
import java.time.DateTimeException;

class BaseTicketItem extends TicketItem implements Comparable<TicketItem> {
	private BaseProduct baseProduct;
	private String[] personalizations;
	
	public BaseTicketItem(BaseProduct product, int amount, String[] personalizations) {
		super(product, amount);
		this.baseProduct = product;
		this.personalizations = personalizations;
	}
	
	@Override
		public double getPrice() {
		float persExtra = BaseProduct.PERSONALIZATION_EXTRA_PERCENT * this.personalizations.length;
		return this.baseProduct.getPrice()* (1.0f + persExtra);
	}
	
	@Override
		public double getItemDiscount(int[] categoriesCount) {
		int categoryIndex = (int)this.baseProduct.getCategory().ordinal();
		if (categoriesCount[categoryIndex] > 1) {
			return this.baseProduct.getCategory().discountPercent * this.getPrice();
		}
		return 0;
	}
	
	@Override
		public int compareTo(TicketItem other) {
		int result = -1;
		if (other != null && (other.getItem() instanceof Product otherProduct)) {
			result = this.baseProduct.getName().compareTo(otherProduct.getName());
		}
		return result;
	}
	
	@Override
		public void validate() throws InvalidDataException {
		// NOTE(erb): do nothing at validation
	}
	
	@Override
		public TicketItem copy() {
		return new BaseTicketItem((BaseProduct)this.baseProduct.copy(), super.amount, this.personalizations);
	}
	
	@Override
		public String toString() {
		return this.baseProduct.toString(personalizations);
	}
	
	@Override
		public boolean equals(TicketItem other) {
		if (other != null && this.item.getId() == other.item.getId()) {
			if (other instanceof BaseTicketItem baseItem) {
				return Arrays.equals(this.personalizations, baseItem.personalizations);
			} 
		}
		return false;
	}
}

public class BaseProduct extends Product {
	/**
	 * The percent cost added to the price of an item per personalization’
	 */
	public static final float PERSONALIZATION_EXTRA_PERCENT = 0.1f;
	
    public enum Category {
        MERCH	   (0.00f, 3),
        STATIONERY  (0.05f, 0),
        CLOTHES	 (0.07f, 5),
        BOOK	    (0.10f, 0),
        ELECTRONICS (0.03f, 2);
        
        public final float discountPercent;
        public final int maxPersonalizations;
        
        private Category(float discountPercent, int maxPersonalizations) {
            this.discountPercent = discountPercent;
            this.maxPersonalizations = maxPersonalizations;
        }
    }
    
    private Category category;
	private int maxPersonalizations;
	private boolean personalized;
    
    public BaseProduct(int id, String name, double price, String categoryStr, int maxPersonalizations, boolean personalized) throws InvalidDataException {
        super(id, name, price);
        
		try {
			this.category = Category.valueOf(categoryStr);
		} catch (IllegalArgumentException e) {
			throw new InvalidDataException(String.format("Product category must be one of %s", Arrays.toString(Category.values())));
		}
		
		if (maxPersonalizations < 0 || maxPersonalizations > this.category.maxPersonalizations)
            throw new InvalidDataException("Expected a number between 0 and " + this.category.maxPersonalizations + " for max personalizations, got " + maxPersonalizations);
        
        this.maxPersonalizations = maxPersonalizations;
		this.personalized = personalized;
    }
	
    public BaseProduct(int id, String name, double price, Category category, int maxPersonalizations, boolean personalized) throws InvalidDataException {
        super(id, name, price);
		
        if (maxPersonalizations < 0 || maxPersonalizations > category.maxPersonalizations)
            throw new InvalidDataException("Expected a number between 0 and " + category.maxPersonalizations + " for max personalizations, got " + maxPersonalizations);
        
        this.category = category;
        this.maxPersonalizations = maxPersonalizations;
		this.personalized = personalized;
    }
    
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(String categoryStr) throws InvalidDataException{
		
		Category categoryUpd = null;
		
		try {
			categoryUpd = Category.valueOf(categoryStr);
		} catch (IllegalArgumentException e) {
			throw new InvalidDataException(String.format("Product category must be one of %s", Arrays.toString(Category.values())));
		}
        
		if (maxPersonalizations > categoryUpd.maxPersonalizations) {
            throw new InvalidDataException(String.format("Max personalizations is set to allow more personalizations (%d) than this category allows (%d)", 
														 this.maxPersonalizations, categoryUpd.maxPersonalizations));
		}
		
		this.category = categoryUpd;
	}
	
	public boolean getPersonalized() {
		return this.personalized;
	}
	
	
	@Override
		public TicketItem  getTicketItem(int amount, String[] personalizations) {
		return new BaseTicketItem(this, amount, personalizations);
	}
	
	@Override
		public InventoryItemId getInventoryId() {
		return new InventoryItemId(id, true);
	}
	
	@Override
		public boolean isInstanceUnique() {
		return false; 
	}
	
	@Override
		public InventoryItem copy() {
		try {
			return new BaseProduct(super.id, super.name, super.price, category, maxPersonalizations, personalized);
		} catch (InvalidDataException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@Override
		public String toString() {
		return toString(null);
	}
	
	public String toString(String[] personalizations) {
		StringBuilder sb = new StringBuilder();
		
		String className = "Product";
		double effectivePrice = super.price;
		
		if (this.personalized) {
			className = "ProductPersonalized";
			if (personalizations != null) { 
				effectivePrice += price * personalizations.length * PERSONALIZATION_EXTRA_PERCENT;
			}
		}
		
		sb.append(String.format("{class:%s, id:%d, name:'%s', category:%s, price:%.1f",
								className, super.id, super.name, this.category, effectivePrice));
		
		if (this.personalized) {
			sb.append(String.format(", maxPersonal:%d", this.maxPersonalizations));
			if (personalizations != null && personalizations.length > 0) {
				sb.append(", personalizationList:");
				sb.append(Arrays.toString(personalizations));
			}
		}
		
		sb.append("}");
		return sb.toString();
	}
	
}