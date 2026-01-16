/* date = December 28th 2025 9:53 pm */
package es.upm.etsisi.poo;

import es.upm.etsisi.poo.exceptions.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

class ServiceTicketItem extends TicketItem {
	
	private ServiceProduct serviceProduct;
	
	public ServiceTicketItem(ServiceProduct product) {
		super(product, 1);
		this.serviceProduct = product;
	}
	
	@Override
		public double getPrice() {
		return 0;
	}
	
	@Override
		public double getDiscountPercentOverAll(int[] categoriesCount) {
		return ServiceProduct.SERVICE_DISCOUNT;
	}
	
	@Override
		public void validate() throws DateTimeException {
		if (this.serviceProduct.getExpirationDate().isBefore(App.now())) {
			throw new DateTimeException(String.format("Service %s is past its expiration date", this.serviceProduct.toString()));
		}
	}
	
	@Override
		public TicketItem copy() {
		return new ServiceTicketItem((ServiceProduct)this.item.copy());
	}
	
	@Override
		public String toString() {
		return super.item.toString();
	}
	
}



public class ServiceProduct extends InventoryItem {
    
    public static final float SERVICE_DISCOUNT = 0.15f;
    public static final DateTimeFormatter EXPIRATION_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE LLL dd HH:mm:ss zzz yyyy");
    
    public enum ServiceCategory {
        TRANSPORT, SHOW, INSURANCE,
    }
    
    private ServiceCategory category;
    private LocalDateTime expirationDate;
	
    public ServiceProduct(int id, String categoryStr, LocalDateTime expirationDate) throws InvalidDataException {
        super(id);
		
		assert categoryStr != null : "Category string can't be null";
		assert expirationDate != null : "Expiration date can't be null";
		
		
		try {
			this.category = ServiceCategory.valueOf(categoryStr);
		} catch (IllegalArgumentException e) {
			throw new InvalidDataException(String.format("Service category must be one of %s", Arrays.toString(ServiceCategory.values())));
		}
		
        this.expirationDate = expirationDate;
    }
	
    public ServiceProduct(int id, ServiceCategory category, LocalDateTime expirationDate) throws InvalidDataException {
        super(id);
		
		assert category != null : "Category can't be null";
		assert expirationDate != null : "Expiration date can't be null";
		
        
        this.category = category;
		this.expirationDate = expirationDate;
	}
	
	public ServiceCategory getCategory() {
		return this.category;
	}
	
	public LocalDateTime getExpirationDate() {
		return this.expirationDate;
	}
	
	@Override
		public boolean isInstanceUnique() { 
		return true;
	}
	
	@Override
		public InventoryItem copy() {
		try {
			return new ServiceProduct(super.id, category, expirationDate);
		} catch (InvalidDataException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@Override
		public String toString() {
		String dateStr = this.expirationDate.atZone(ZoneId.systemDefault()).format(EXPIRATION_DATE_FORMAT);
		return String.format("{class:ProductService, id:%d, category:%s, expiration:%s}",
							 this.id, category.toString(), dateStr);
	}
	
	@Override
		public TicketItem getTicketItem(int amount, String[] personalization) {
		return new ServiceTicketItem(this);
	}
	
	@Override
		public InventoryItemId getInventoryId() {
		return new InventoryItemId(id, false);
	}
	
	public static boolean isIdString(String id) {
		if (id != null && id.length() > 0) {
			char last = id.charAt(id.length()-1);
			if (last == 'S' || last == 's') { 
				return true;
			}
		}
		return false;
	}
}