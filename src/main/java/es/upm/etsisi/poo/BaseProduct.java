package es.upm.etsisi.poo;

public class BaseProduct extends Product {
    private Category category;

    public enum Category {
        MERCH	   	(0.00f,3 ),
        STATIONERY 	(0.05f, 0),
        CLOTHES	    (0.07f, 5),
        BOOK	   	(0.10f, 0),
        ELECTRONICS	(0.03f, 2);

        private final float discountPercent;
        private final int maxCustomizations;

        private Category(float discountPercent, int maxCustomizations) {
            this.discountPercent = discountPercent;
            this.maxCustomizations = maxCustomizations;
        }

        public float getDiscountPercent() {
            return this.discountPercent;
        }
        public int getMaxCustomizations() {return this.maxCustomizations;}

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
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }

    public BaseProduct(int id, String name, double price, Category category) {
        super(id, name, price);
        this.category = category;
    }
    @Override
    public String toString() {
        return String.format("{class:Product, id:%d, name:'%s', category:%s, price:%.1f}",
                this.id, super.getName(), this.category, super.getPrice());
    }
}
