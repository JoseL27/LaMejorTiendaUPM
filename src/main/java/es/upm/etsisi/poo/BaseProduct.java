package es.upm.etsisi.poo;

public class BaseProduct extends Product {
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

        /**
         * Function use in Parse.
         * @param label String receive from the parse
         * @return Category if is aceptable or null if the category does not exit
         */
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

    private Category category;
	private int maxPersonalizations;

    // It is assumed that all the parameters are valid, this should be handled before creating the object
    public BaseProduct(int id, String name, double price, Category category, int maxPersonalizations) {
        super(id, name, price);
        this.category = category;
        this.maxPersonalizations = maxPersonalizations;
    }

    public Category getCategory() {
        return category;
    }
	
    public void setCategory(Category category) {
        this.category = category;
    }
	
    @Override
    public String toString() {
        return String.format("{id:%d, name:'%s', category:%s, price:%.1f}",
                this.id, super.getName(), this.category, super.getPrice());
    }

	// NOTE(enrique): Any BaseProduct is multiple of any product.
	// Meaning there can be many instances of it in a ticket.

	@Override
	public boolean duplicateOf(Product product) {
		return false; 
	}
}
