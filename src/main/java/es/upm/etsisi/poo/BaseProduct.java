package es.upm.etsisi.poo;

public class BaseProduct extends Product {
    private Category category;

    public enum Category {
        MERCH	   	(0.00f,true ),
        STATIONERY 	(0.05f, false),
        CLOTHES	    (0.07f, true),
        BOOK	   	(0.10f, false),
        ELECTRONICS	(0.03f, true);

        private final float discountPercent;
        private final boolean customizable;

        private Category(float discountPercent, boolean customizable) {
            this.discountPercent = discountPercent;
            this.customizable = customizable;
        }

        public float getDiscountPercent() {
            return this.discountPercent;
        }
        public boolean getCustomizable() {return this.customizable;}

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

    public Category category() {
        return this.category;
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
