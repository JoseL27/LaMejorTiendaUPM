package es.upm.etsisi.poo;

import java.util.Arrays;

/**
 * Ticket class to manage an application ticket which consists of a product list with amounts.
 * 
 * @author Enrique Rocha - 03/10
 * @see Product
 */
public class Ticket {

	/**
	 * ProductInfo struct-like holder as a Product-amount pair.
	 * Has basic constructors and getters.
	 * Implements 'Comparable' class to alfabetically order products as in the requirement document.
	 */
	private class ProductInfo implements Comparable<ProductInfo> {
		private Product product;
		private int amount;

		public ProductInfo(Product product, int amount) {
			this.product = product;
			this.amount = amount;
		}

		public Product getProduct() { 
			return this.product;
		}
		
		public int getAmount() { 
			return this.amount;
		}

		public void setProduct(Product product) { 
			this.product = product;
		}
		
		public void incrementAmount(int increment) { 
			this.amount += increment;
		}

		/**
		 * Compares this to another ProductInfo based on name only
		 * @param other the ProductInfo to be compared.
		 * @return A value less than 0 if this is lesser, 0 if they are equal, and a value greater than if this is greater
		 */
		@Override
		public int compareTo(ProductInfo other) {
			return this.product.getName().compareTo(other.product.getName());
		}

		/**
		 * Checks if this is equal to another object, that has to be a ProductInfo based on the amount and Product.equals()
		 * @param obj Object to be compared to
		 * @return true, if both objects are equal under this criteria, false in other case
		 */
		@Override
		public boolean equals(Object obj){
			boolean result = false;

			if (obj != null && obj.getClass() == this.getClass()){
				ProductInfo otherProduct = (ProductInfo) obj;
				result = otherProduct.product.equals(this.product)
						&& otherProduct.amount == this.amount;
			}

			return result;
		}
	}

	/**
	 * Max amount of products allowed in the Ticket, as the requirement documents specifies.
	 */
	public static final int TICKET_MAX_PRODUCTS = 100;

	/**
	 * The current products the ticket holds. static array of products-amount pairs.
	 * Has a fixed size of 'TICKET_MAX_PRODUCTS' and holds 'count' product infos.
	 */
	private ProductInfo[] productInfos;

	/**
	 * The count of product infos to manage the static array.
	 */
	private int count;
    /**
     * The total number of items in the Ticket
     */
    private int numTotal;
	/**
	 * Basic constructor
	 */
	public Ticket() {
		reset();
	}

	/**
	 * Resets the Ticket resources
	 */
	public void reset() {
		productInfos = new ProductInfo[TICKET_MAX_PRODUCTS];
		count = 0;
        numTotal = 0;
	}
	

	/**
	 * Adds a product asociated to an amount to the ticket. Too things may happen:
	 * Checks if the product allready exists 
	 *  - If the product does not exist it will be added by appendProductInfo
	 *    which will check if the current 'count' is less than the 'TICKET_MAX_PRODUCTS'.
	 *  - If the product exists (equality is checked by id) its amount is incremented
	 *
	 * @param product  the product to add or increment
	 * @param amount   the amount to increment
	 * @return         false if the product was attempted to be added and TICKET_MAX_PRODUCTS was hit.
	 *
	 * @see findProductInfo
	 * @see appendProductInfo
	 */
	public boolean addProduct(Product product, int amount) {
		boolean result = false;

		ProductInfo foundProductInfo = findProductInfo(product.getId());
			if(numTotal+amount<=TICKET_MAX_PRODUCTS){
                if (foundProductInfo != null) {
                    foundProductInfo.incrementAmount(amount);
                    result = true;

                } else {
                    result = appendProductInfo(new ProductInfo(product, amount));
                }
                if(result) numTotal+=amount;
            }
		return result;
	}

	/**
	 * Search and remove the product with the corresponding id. 
	 * 
	 * Lookup is standard linear search and actual removal is O(1) due to
	 * unordered removal (replaces element to remove with the last element).
	 * 
	 * @param id  the id of the product to attempt to remove
	 * @return    the removed product if it was found or null if it wasn't
	 *
	 * @note Lookup could be optimized after calling 'printList' which
	 * sorts the products array
	 */
	public Product removeProduct(int id) {
		int foundIndex = productInfoIndex(id);
		
		if (foundIndex != -1) {
			Product removed = productInfos[foundIndex].getProduct();
            numTotal -= productInfos[foundIndex].getAmount();
			productInfos[foundIndex] = productInfos[count - 1];
			productInfos[count - 1]	= null;
			count--;
			return removed;
		}
		
		return null;
	}


	/**
	 * Get a summary string of the current ticket products associated with an amount and discount.
	 * Sorts the productInfos array on product name string comparison (alfabetically).
	 * 
	 * @return a summary string according to the requirements document
	 *
	 * Example:
	 * $ ticket add 1 2
	 * {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
	 * {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
	 * Total price: 60.0
	 * Total discount: 6.0
	 * Final Price: 54.0
	*/
	public String summaryString()
	{
		StringBuilder sb = new StringBuilder();

		double totalPrice = 0;
		double totalDiscount = 0;
		
		if (count > 0) {
			Arrays.sort(productInfos, 0, count);
		}

		for (int productInfoIndex = 0; productInfoIndex < count; productInfoIndex++) {
			ProductInfo productInfo = productInfos[productInfoIndex];
			Product product = productInfo.getProduct();
			
			totalPrice += productInfo.getAmount() * product.getPrice();

			boolean hasDiscount = getOccurrences(product.getCategory()) > 1;

			for (int productCounter = 0; productCounter < productInfo.getAmount(); productCounter++) {
				sb.append(String.format("{class:Product, id:%d, name:'%s', category:%s, price:%.1f}", 
										product.getId(), product.getName(), product.getCategory(), product.getPrice()));
				
				if (hasDiscount) {
					double productDiscount = product.getPrice() * product.getCategory().getDiscountPercent();
					totalDiscount += productDiscount;
					sb.append(String.format(" **discount -%.1f", productDiscount));
				}
				sb.append("\n");
			}
		}
		
		sb.append(String.format("Total price: %.1f\n", (float)totalPrice));
		sb.append(String.format("Total discount: %.1f\n", (float)totalDiscount));
		sb.append(String.format("Final Price: %.1f", (float)(totalPrice - totalDiscount)));
		return sb.toString();
	}

	/**
	 * Counts all the occurrences of a category in the ticket.
	 * A productInfo is considered to have productInfo.getAmount() occurrences
	 * @param category Category of which the occurrences will be counted
	 * @return Number of occurrences of the category, if it does not appear, 0 is returned
	 */
	private int getOccurrences(Product.Category category){
		int result = 0;

		for (int i = 0; i < count; i++) {
			if (category.equals(productInfos[i].getProduct().getCategory())){
				result += productInfos[i].getAmount();
			}
		}
		return result;
	}

	/**
	 * Helper to find the index of a product info in the array.
	 * Standard linear search.
	 *
	 * @param id   the id of the product to search for
	 * @return     the index of the product info in the array or -1 if it wasn't found
	 */
	private int productInfoIndex(int id) {
		int result = -1;
		int index = 0;
		while (result == -1 && index < count) {
			ProductInfo currentProductInfo = productInfos[index];
			if (currentProductInfo.getProduct().getId() == id) {
				result = index;
			}
			index++;
		}
		return result;
	}

	/**
	 * Helper to get a productInfo in the product info array.
	 *
	 * @param id   the id of the product to search for
	 * @return     the product info in the array or null if it wasn't found
	 * @see findProductInfo
	 */
	private ProductInfo findProductInfo(int id) {
		int index = productInfoIndex(id);
		return (index != -1) ? productInfos[index] : null;
	}

	/**
	 * Helper to get a append a productInfo in the product info array.
	 * Standard array append checking the max length
	 *
	 * @param productInfo   the productInfo to add at the end
	 * @return              false if 'count == productInfos.length'
	 */
	private boolean appendProductInfo(ProductInfo productInfo) {
		if (count + 1 < productInfos.length) {
			productInfos[count] = productInfo;
			count++;
			return true;
		}
		return false;
	}

	/**
	 * Checks if this is equal to another object, that has to be a Ticket, based on count and all the productInfos
	 * @param obj Object to be compared to
	 * @return true, if both objects are equal under this criteria, false in other case
	 */
	@Override
	public boolean equals(Object obj){
		boolean result = false;

		if (obj != null && obj.getClass() == this.getClass()){
			Ticket otherTicket = (Ticket) obj;
			result = otherTicket.count == this.count
					&& Arrays.equals(otherTicket.productInfos, this.productInfos);
		}

		return result;
	}
}
