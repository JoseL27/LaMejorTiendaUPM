package es.upm.etsisi.poo;

public class TimedProduct extends Product {
    // timeForPreparing are de ms minimun to prepare  the activity.
    public enum TimedType {
        MEETING(43200000), //12h
        FOOD(259200000); //72h

        private int  timeForPreparing;

        private TimedType(int timeForPreparing) {
            this.timeForPreparing = timeForPreparing;

        }
    }

    private static final int TIMED_PRODUCT_MAX_PEOPLE = 100;
	
    private TimedType type;
    private int amount;
    private int maxParticipants;

    // It is assumed that all the parameters are valid, this should be handled before creating the object
    public TimedProduct(int id, String name, double individualPrice, int maxParticipants, TimedType type) {
        super(id, name, individualPrice);
        this.type = type;
		this.amount = maxParticipants;
        this.maxParticipants = maxParticipants;
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
        return String.format("{id:%d, name:'%s', type:%s, price:%.1f}",
							 this.id, super.getName(), this.type, super.getPrice());
	}
}
