package es.upm.etsisi.poo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimedProduct extends Product {

	public static final DateTimeFormatter EXPIRATION_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // The times for preparing should be compared by using
    // LocalDateTime.now().plusHours(productYouAreChecking.getType().getHoursForPreparing()).compareTo(productYouAreChecking.getExpirationDate())
    // Refer to the java documentation for further instructions
    public enum TimedType {
        MEETING(12), //12h
        FOOD(72); //72h

        private int hoursForPreparing;

        private TimedType(int hoursForPreparing) {
            this.hoursForPreparing = hoursForPreparing;
        }

        public static TimedType fromLabel(String label){
            TimedType result = null;
            try{
                result = TimedType.valueOf(label.toUpperCase());
            }catch (Exception e){
            }finally{
                return result;
            }
        }

        public int getHoursForPreparing(){
            return hoursForPreparing;
        }
        }

    public static final int TIMED_PRODUCT_MAX_PEOPLE = 100;
	
    private TimedType type;
    private int maxParticipants;
    private LocalDateTime expirationDate;

    // It is assumed that all the parameters are valid, this should be handled before creating the object
    public TimedProduct(int id, String name, double individualPrice, int maxParticipants, TimedType type, LocalDateTime expirationDate) {
        super(id, name, individualPrice);
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
	
	@Override
	public boolean duplicateOf(Product product) {
		return (product != null) 
			&& product.getClass() == this.getClass()
			&& product.getId() != this.getId();
	}

	@Override
	public String toString() {
		return toString(0);
	}

	public String toString(int amount) {
		String typeWord = this.type.toString().charAt(0) + this.type.toString().substring(1).toLowerCase();

		// NOTE(enrique): expected output indicates that this should the price being payed.
		// So for example, when printing it as a product listing, it should be 0;
		double effectivePrice = super.getPrice();
        if (amount > 0){
             effectivePrice = effectivePrice * amount;
        }

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("{class:%s, id:%d, name:'%s', price:%.1f, date of Event:%s, max people allowed:%d",
								typeWord, super.getId(), super.getName(), effectivePrice, 
								this.expirationDate.format(EXPIRATION_DATE_FORMAT), this.getMaxParticipants()));
		if (amount > 0) {
			sb.append(String.format(", actual people in event:%d", amount));
		}
		
		sb.append("}");
        return sb.toString();
	}
			
}
