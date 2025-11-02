package es.upm.etsisi.poo;

public class Product {

    public final int id;
    private String name;
    private double price;

    // constructor
    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    /**
     * Checks if this is the same as other product, based on id only
     *
     * @param p product to be compared to
     * @return true, if the products have the same id, false in other case
     */
    public boolean equals(Product p) {
        return this.id == p.id;
    }

    public double price(int amount) {
        return this.price * amount;
    }

    @Override
    public String toString() {
        return String.format("{class:Product, id:%d, name:'%s', category:%s, price:%.1f}",
                this.id, this.name, this.category, this.price);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Checks if this is equal to another object, that has to be a Product, based on id, name, category and price
     *
     * @param obj Object to be compared to
     * @return true, if the objects are equals under this criteria, false in other case
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) return false;

        Product otherProd = (Product) obj;
        return this.id == otherProd.id
                && Utils.nullOrEquals(this.name, otherProd.name)
                //	&& Utils.nullOrEquals(this.category, otherProd.category)
                && this.price == otherProd.price;
    }
}
