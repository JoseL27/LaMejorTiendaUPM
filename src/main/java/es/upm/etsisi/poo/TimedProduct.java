package es.upm.etsisi.poo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.DateTimeException;

import java.util.Arrays;

import es.upm.etsisi.poo.exceptions.*;

class TimedTicketItem extends TicketItem {
	private TimedProduct timedProduct;
	private int peopleAmount;
	
	public TimedTicketItem(TimedProduct product, int peopleAmount) {
		super(product, 1);
		this.timedProduct = product;
		this.peopleAmount = peopleAmount;
	}
	
	@Override
		public double getPrice() {
		return this.timedProduct.getPrice() * this.peopleAmount;
	}
	
	@Override
		public void validate() throws DateTimeException {
		if (this.timedProduct.getExpirationDate().isBefore(App.now())) {
			throw new DateTimeException(String.format("Product %s is past its expiration date", this.timedProduct.toString()));
		}
	}
	
	@Override
		public int compareTo(TicketItem other) {
		int result = -1;
		if (other != null && (other.getItem() instanceof Product otherProduct)) {
			result = this.timedProduct.getName().compareTo(otherProduct.getName());
		}
		return result;
	}
	
	@Override
		public TicketItem copy() {
		return new TimedTicketItem((TimedProduct)this.timedProduct.copy(), this.peopleAmount);
	}
	
	@Override 
		public String toString() {
		return this.timedProduct.toString(this.peopleAmount);
	}
}


public class TimedProduct extends Product {
	public static final DateTimeFormatter EXPIRATION_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // The times for preparing should be compared by using
    // App.now().plusHours(productYouAreChecking.getType().getHoursForPreparing()).compareTo(productYouAreChecking.getExpirationDate())
    // Refer to the java documentation for further instructions
    public enum TimedType {
        MEETING(12), //12h
        FOOD(72); //72h
        
        
		public final int hoursForPreparing;
		
        private TimedType(int hoursForPreparing) {
            this.hoursForPreparing = hoursForPreparing;
        }
    }
    
    public static final int TIMED_PRODUCT_MAX_PEOPLE = 100;
    
    private TimedType type;
    private int maxParticipants;
    private LocalDateTime expirationDate;
	
	
	public TimedProduct(int id, String name, double individualPrice, int maxParticipants, String typeString, LocalDateTime expirationDate) throws InvalidDataException { 
		super(id, name, individualPrice);
		
		if (maxParticipants < 0 || maxParticipants > TIMED_PRODUCT_MAX_PEOPLE)
			throw new InvalidDataException("Max participants for a timed product should be between 0 and " + TIMED_PRODUCT_MAX_PEOPLE);
        
		try {
			TimedType type = TimedType.valueOf(typeString);
			this.type = type;
		} catch (IllegalArgumentException e) {
			throw new InvalidDataException(String.format("Timed type is one of %s", Arrays.toString(TimedType.values())));
		}
		
		this.maxParticipants = maxParticipants;
		this.expirationDate = expirationDate;
	}
    
    // It is assumed that all the parameters are valid, this should be handled before creating the object
    public TimedProduct(int id, String name, double individualPrice, int maxParticipants, TimedType type, LocalDateTime expirationDate) throws InvalidDataException {
        super(id, name, individualPrice);
        if (maxParticipants < 0 || maxParticipants > TIMED_PRODUCT_MAX_PEOPLE)
            throw new InvalidDataException("Max participants for a timed product should be between 0 and " + TIMED_PRODUCT_MAX_PEOPLE);
        this.type = type;
        this.maxParticipants = maxParticipants;
        this.expirationDate = expirationDate;
    }
	
    public TimedType getType() {
        return this.type;
    }
    
    public int getMaxParticipants() {
        return this.maxParticipants;
    }
    
    public LocalDateTime getExpirationDate() {
        return this.expirationDate.minusHours(type.hoursForPreparing);
    }
	
	
	@Override
		public TicketItem getTicketItem(int amount, String[] personalizations) throws InvalidDataException {
		if (personalizations != null && personalizations.length != 0) {
			throw new InvalidDataException("Timed products can't be personalized");
		}
		if (amount >= maxParticipants) {
			throw new InvalidDataException ("Timed products amount can't be greater than the max participants");
		}
		return new TimedTicketItem(this, amount);
	}
	
	@Override
		public InventoryItemId getInventoryId() {
		return new InventoryItemId(id, true);
	}
	
	@Override
		public boolean isInstanceUnique() {
		return true; 
	}
	
	@Override
		public InventoryItem copy() {
		try {
			return new TimedProduct(super.id, super.name, super.price, maxParticipants, type, expirationDate);
		} catch (InvalidDataException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
    @Override
        public String toString() {
        return toString(0);
    }
    
    public String toString(int amount) {
        String typeWord = this.type.toString().charAt(0) + this.type.toString().substring(1).toLowerCase();
        // NOTE(enrique): expected output indicates that this should the price being payed.
        // So for example, when printing it as a product listing, it should be 0;
        double effectivePrice = super.price;
        if (amount > 0) {
            effectivePrice = effectivePrice * amount;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("{class:%s, id:%d, name:'%s', price:%.1f, date of Event:%s, max people allowed:%d",
                                typeWord, super.id, super.name, effectivePrice,
                                this.expirationDate.format(EXPIRATION_DATE_FORMAT), this.getMaxParticipants()));
        if (amount > 0) {
            sb.append(String.format(", actual people in event:%d", amount));
        }
        
        sb.append("}");
        return sb.toString();
    }
}