/* date = December 28th 2025 9:53 pm */

package es.upm.etsisi.poo;
import es.upm.etsisi.poo.exceptions.*;
import java.time.LocalDateTime;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;

class ServiceTicketItem extends TicketItem implements Comparable<TicketItem> {
	
	private ServiceProduct serviceProduct;
	
	public ServiceTicketItem(ServiceProduct product) throws IllegalArgumentException {
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

    @Override
    public int compareTo(TicketItem ticketItem){
       int result = -1;
       if (ticketItem != null && ticketItem.getItem() instanceof ServiceProduct){
           result = this.item.getId() - ticketItem.getItem().getId();
       }
       return result;
    }
}



public class ServiceProduct extends InventoryItem {
    
    public static final float SERVICE_DISCOUNT = 0.15f;
    public static final DateTimeFormatter EXPIRATION_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE LLL HH:mm:ss zzz yyyy");
    
    public enum ServiceCategory {
        TRANSPORT, SHOW, INSURANCE,
    }
    
    private ServiceCategory category;
    private LocalDateTime expirationDate;
    
    
    public ServiceProduct(int id, ServiceCategory category, LocalDateTime expirationDate) throws IllegalArgumentException {
        super(id);
        
        if (id <= 0) throw new IllegalArgumentException("Service id must be greater or equal to one, got " + id);
        if (category == null) throw new IllegalArgumentException("Category can not be null");
        if (expirationDate == null) throw new IllegalArgumentException("Expiration can not be null");
        if (expirationDate.isBefore(App.now())) 
            throw new IllegalArgumentException("Expiration can not be in the past, got " + expirationDate.format(EXPIRATION_DATE_FORMAT));
        
        this.category = category;
        this.expirationDate = expirationDate;
    }
    
    
    public static ServiceProduct newFromId(String id, ServiceCategory category, LocalDateTime expirationDate) throws IllegalArgumentException {
        int idNum = 0;
		
		if (isIdString(id)) {
			try {
				idNum = Integer.valueOf(id.substring(1));
			} catch (NumberFormatException e) {
				idNum = 0;
			}
		}
        
        if (idNum == 0) {
            throw new IllegalArgumentException("Expected id to be greater than 0 and have an S at the end, got " + id);
        }
        
        return new ServiceProduct(idNum, category, expirationDate);
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
		return new ServiceProduct(super.id, category, expirationDate);
	}
	
    @Override
        public String toString() {
        return String.format("{class:ProductService, id:%d, category:%s, expiration:%s}",
                             this.id, category.toString(), this.expirationDate.format(EXPIRATION_DATE_FORMAT));
    }
	
	@Override
		public TicketItem getTicketItem(int amount, String[] personalization) throws IllegalArgumentException {
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
