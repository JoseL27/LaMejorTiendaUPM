/* date = December 31st 2025 6:34 am */
package es.upm.etsisi.poo;

import es.upm.etsisi.poo.exceptions.DuplicateItemException;
import es.upm.etsisi.poo.exceptions.FullCollectionException;

import java.time.DateTimeException;
import java.util.Iterator;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;

public class ProductTicket extends Ticket {
	
	public ProductTicket(int id) {
		super(id);
	}
    /**
     * Adds a product asociated to an amount to the ticket. Too things may happen:
     * - If the product does not exist it will be added (as long as there is room)
     * - If the product exists (equality is checked by id) its amount is incremented
     * <p>
     * Search happens with findDuplicateProductInfo which is not exactly a regular look up. Please take a look.
     *
     * @param product          the product to add or increment
     * @param amount           the amount to increment
     * @param personalizations the personalizations of the product
     * @return false if the product was attempted to be added and MAX_PRODUCTS was hit.
     * @see findDuplicateProductInfo
     * @see appendProductInfo
     */
    public void addProduct(Product product, int amount, String[] personalizations) throws DuplicateItemException, FullCollectionException{
        ProductInfo newProductInfo = new ProductInfo(product, amount, personalizations);
		
        ProductInfo duplicate = findDuplicateProductInfo(newProductInfo);
        if (duplicate == null) {
            if (product instanceof TimedProduct timedProduct) {
                // First time adding TimedProduct, a TimedProduct counts as 1 item regardless of participant amount
                if (amount <= timedProduct.getMaxParticipants()) {
                    appendProductInfo(newProductInfo);
                    this.totalAmount++;
                }
            } else { // First time adding BaseProduct
                appendProductInfo(newProductInfo);
                this.totalAmount += newProductInfo.getAmount();
            }
        } else {
            if (product instanceof TimedProduct timedProduct) // Adding the same TimedProduct again should fail
                throw new DuplicateItemException("This timed product already exists in ticket");
            else {
                if ((this.totalAmount + amount) <= MAX_PRODUCTS) { // Adding the same BaseProduct should increment amount
                    duplicate.addAmount(amount);
                    this.totalAmount += amount;
                }
            }
        }
    }
	
	
    /**
     * @return array table of the amount of products in each.
     * <p>
     * To check the amount of some catagory use category.ordinal() to index the arary.
     * E.g. categoriesProductCount[(int)baseProduct.getCategory().ordinal()]
     */
    private int[] categoriesProductCount() {
        int categoriesCount = BaseProduct.Category.values().length;
        int[] categoriesProductCount = new int[categoriesCount];
		
        for (ProductInfo info : productInfos) {
            Product product = info.getProduct();
            // NOTE(enrique): Think of a more OOP way to do this.
            if (product instanceof BaseProduct baseProduct) {
                int categoryIndex = (int) baseProduct.getCategory().ordinal();
                categoriesProductCount[categoryIndex] += info.getAmount();
            }
        }
		
        return categoriesProductCount;
    }
	
    /**
* Product only specific summaryString
     * Example:
     * $ ticket add 1 2
     * {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
     * {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
     * Total price: 60.0
     * Total discount: 6.0
     * Final Price: 54.0
     */
    public String summaryString() {
        StringBuilder sb = new StringBuilder();
		
        sb.append("Ticket : ")
			.append(getComposedId())
			.append("\n");
		
        double totalPrice = 0;
        double totalDiscount = 0;
		
        productInfos.sort(null);
		
        int[] categoriesCount = categoriesProductCount();
		
        for (ProductInfo productInfo : productInfos) {
            Product product = productInfo.getProduct();
			
            double effectivePrice = product.getPrice();
			
            if (product instanceof BaseProduct baseProduct) {
				
                String[] pers = productInfo.getPersonalizations();
                if (pers != null && pers.length > 0) {
                    double persExtra = product.getPrice() * BaseProduct.PERSONALIZATION_EXTRA_PERCENT * pers.length;
                    effectivePrice += persExtra;
                }
				
                int categoryIndex = (int) baseProduct.getCategory().ordinal();
                boolean hasDiscount = (categoriesCount[categoryIndex]) > 1;
                double discount = 0.0;
				
                if (hasDiscount) {
                    discount = baseProduct.getCategory().getDiscountPercent() * effectivePrice;
                }
				
                for (int i = 0; i < productInfo.getAmount(); i++) {
                    sb.append("  ").append(baseProduct.toString(productInfo.getPersonalizations()));
                    if (hasDiscount) {
                        sb.append(" **discount -").append(DECIMAL_FORMAT.format(discount));
                    }
                    sb.append('\n');
                }
				
                if (hasDiscount) {
                    totalDiscount += discount * productInfo.getAmount();
                }
				
            } else if (product instanceof TimedProduct timedProduct) {
                sb.append("  ")
					.append(timedProduct.toString(productInfo.getAmount()))
					.append('\n');
            }
			
            totalPrice += effectivePrice * productInfo.getAmount();
        }
		
        double finalPrice = (totalPrice - totalDiscount);
		
        sb.append("  Total price: ").append(DECIMAL_FORMAT.format(totalPrice)).append("\n");
        sb.append("  Total discount: ").append(DECIMAL_FORMAT.format(totalDiscount)).append("\n");
        sb.append("  Final Price: ").append(DECIMAL_FORMAT.format(finalPrice)).append("\n");
        return sb.toString();
    }
	
}
