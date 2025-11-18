package es.upm.etsisi.poo;

import java.time.LocalDateTime;

public class TimedProduct extends Product {

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
    private int amount;
    private int maxParticipants;
    private LocalDateTime expirationDate;

    // It is assumed that all the parameters are valid, this should be handled before creating the object
    public TimedProduct(int id, String name, double individualPrice, int maxParticipants, TimedType type, LocalDateTime expirationDate) {
        super(id, name, individualPrice);
        this.type = type;
		this.amount = maxParticipants;
        this.maxParticipants = maxParticipants;
        this.expirationDate = expirationDate;
    }

    public TimedType getType() {
        return this.type;
    }

	public int getAmount() {
		return this.amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
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
        return String.format("{id:%d, name:'%s', type:%s, price per attendant:%.1f, maximum of people:%d}",
							 super.getId(), super.getName(), this.type, super.getPrice(), this.getMaxParticipants());
	}
}
