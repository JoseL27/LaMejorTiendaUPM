package es.upm.etsisi.poo;

public class TimedProduct extends Product {
    // timeForPreparing are de ms minimun to prepare  the activity.
    public enum TimedType {
        MEETING(43200000), //12h
        LAUNCH(259200000); //72h

        private int  timeForPreparing;

        private TimedType(int timeForPreparing) {
            this.timeForPreparing = timeForPreparing;

        }
    }

    private static final int TIMED_PRODUCT_MAX_PEOPLE = 100;
	
    private TimedType type;
    private int peopleCount;

    // It is assumed that all the parameters are valid, this should be handled before creating the object
    public TimedProduct(int id, String name, double price, int peopleCount, TimedType type) {
        super(id, name, price * peopleCount);
        this.type = type;
        this.peopleCount = peopleCount;
    }

    public TimedType getType() {
        return this.type;
    }

    public int getPeopleCount() {
        return this.peopleCount;
    }

	@Override
	public double getMultipliedPrice(int amount) {
		return amount * peopleCount * super.getPrice();
	}

	// NOTE(enrique): A TimedProduct is multiple only of other TimedProduct that don't have the same id as it.
	// Meaning you cannot hold two TimedProducts of the same id in a ticket.
	@Override
	public boolean canDuplicate() {
		return false;
	}
	
	@Override
	public boolean duplicateOf(Product product) {
		return (product != null) 
			&& product.getClass() == this.getClass()
			&& product.getId() != this.getId();
	}
}
