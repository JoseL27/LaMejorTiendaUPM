package es.upm.etsisi.poo;

import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;


/**
 * Ticket class to manage an application ticket which consists of a product list with amounts.
 * @see Product
 */
public class Ticket implements Comparable<Ticket> {

	public static final LocalDateTime TEST_NOW_DATE = LocalDateTime.of(25, 11, 14, 18, 21);
	/**
	 * Max amount of products allowed in the Ticket, as the requirement documents specifies.
	 */
	public static final int MAX_PRODUCTS = 100;

	/**
	 * Date format in which the start and ending dates appear in the string representation of the id
	 */
	private static final DateTimeFormatter ID_DATE_FORMAT = DateTimeFormatter.ofPattern("YY-MM-dd-HH:mm");

	/**
	 * Decimal format to use in summaryString()
	 */
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0###");

	/**
	 * The current products the ticket holds. static array of products-amount pairs.
	 * Has a fixed size of 'MAX_PRODUCTS' and holds 'count' product infos.
	 */
	private ArrayList<ProductInfo> productInfos;

	/**
	 * The total summed amount of all productInfo's amount
	 */
	private int totalAmount;

	/**
	 * The number part of the ticket. 5 digit number (between 0 and 99999)
	 */
	private int id;

	/**
	 * The date in which the Ticket constructor was called.
	 */
	private LocalDateTime dateOpened;

	/**
	 * The date in which the close() function was called.
	 */
	private LocalDateTime dateClosed;

	private boolean isOpen;


	/**
	 * Constructor with the id.
	 * To create a ticket with a "random id" use randomId() function
	 * then check if the id is unique in the store and create it using this constructor.
	 */
	public Ticket(int id) {
		resetProductInfos();
		this.id = id;
		this.isOpen = true;

		// TODO(enrique): Change back! Just for testing
		// this.dateOpened = new Date();
		this.dateOpened = TEST_NOW_DATE;
		this.dateClosed = null;
	}

	public int getId() {
		return this.id;
	}
	
	public LocalDateTime getDateOpened() {
		return this.dateOpened;
	}
	
	public LocalDateTime getDateClosed() {
		return this.dateClosed;
	}

	public String getComposedId() {
		StringBuilder sb = new StringBuilder();

		if (this.isOpen) {
			sb.append(this.dateOpened.format(ID_DATE_FORMAT)).append("-");
		}
		
		sb.append(String.format("%05d", this.id));

		if (!this.isOpen && this.dateClosed != null) {
			sb.append("-").append(this.dateClosed.format(ID_DATE_FORMAT));
		}
		
		return sb.toString();
	}

	public boolean getIsOpen() {
		return this.isOpen;
	}

	public boolean isEmpty() {
		return this.productInfos.isEmpty();
	}

	/**
	 * Resets the Ticket resources
	 */
	public void resetProductInfos() {
		this.productInfos = new ArrayList<ProductInfo>();
	}

	public void close() {
		if (this.isOpen) { 
			this.isOpen = false;

			// TODO(enrique): Remove later! Just for tests
			this.dateClosed = TEST_NOW_DATE;
		}
	}

	public static int randomId() {
		return (int)(Math.random() * 100000);		
	}

	public static boolean isValidId(int id) {
		return id >= 0 && id <= 99999;
	}

	/**
	 * Adds a product asociated to an amount to the ticket. Too things may happen:
	 *  - If the product does not exist it will be added (as long as there is room)
	 *  - If the product exists (equality is checked by id) its amount is incremented
	 *	 
	 *  Search happens with findDuplicateProductInfo which is not exactly a regular look up. Please take a look.
	 *
	 * @param product          the product to add or increment
	 * @param amount           the amount to increment
	 * @param personalizations the personalizations of the product
	 * @return                 false if the product was attempted to be added and MAX_PRODUCTS was hit.
	 *
	 * @see findDuplicateProductInfo
	 * @see appendProductInfo
	 */
	public boolean addProduct(Product product, int amount, String[] personalizations) {
		ProductInfo newProductInfo = new ProductInfo(product, amount, personalizations);
		boolean result = false;
		
		ProductInfo duplicate = findDuplicateProductInfo(newProductInfo);
		if (duplicate == null) {

			if (product instanceof TimedProduct) {
				TimedProduct timedProduct = (TimedProduct)product;
				if (amount <= timedProduct.getMaxParticipants()) {
					result = appendProductInfo(newProductInfo);
				}
			} else { 
				result = appendProductInfo(newProductInfo);
			}
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
	 */
	public Product removeProduct(int id) {
		if (!Ticket.isValidId(id)) return null;
		
		ProductInfo foundProductInfo = null;
		ProductInfo currentProductInfo = null;		
		Iterator<ProductInfo> iterator = productInfos.iterator();
		
		while (foundProductInfo == null && iterator.hasNext()) {
			currentProductInfo = iterator.next();
			if (id == currentProductInfo.getProduct().getId()) {
				foundProductInfo = currentProductInfo;
			}
		}
		
		if (foundProductInfo != null) {
			Product removed = foundProductInfo.getProduct();
			if (productInfos.remove(foundProductInfo)) {
				return removed;
			}
		}
		
		return null;
	}

	/**
	 * @return array table of the amount of products in each.
	 *
	 * To check the amount of some catagory use category.ordinal() to index the arary.
	 * E.g. categoriesProductCount[(int)baseProduct.getCategory().ordinal()]
	 */
	private int[] categoriesProductCount() {
		int categoriesCount = BaseProduct.Category.values().length;
		int[] categoriesProductCount = new int[categoriesCount];

		for (ProductInfo info : productInfos) {
			Product prod = info.getProduct();
			// NOTE(enrique): Think of a more OOP way to do this.
			if (prod instanceof BaseProduct) {
				BaseProduct baseProduct = (BaseProduct)prod;
				int categoryIndex = (int)baseProduct.getCategory().ordinal();
				categoriesProductCount[categoryIndex] += info.getAmount();
			}
		}

		return categoriesProductCount;
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

			if (product instanceof BaseProduct) {
				BaseProduct baseProduct = (BaseProduct)product;

				String[] pers = productInfo.getPersonalizations();
				if (pers != null && pers.length > 0) {
					double persExtra = product.getPrice() * BaseProduct.PERSONALIZATION_EXTRA_PERCENT * pers.length;
					effectivePrice += persExtra;
				}

				int categoryIndex = (int)baseProduct.getCategory().ordinal();
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
				
			} else if (product instanceof TimedProduct) {
				TimedProduct timedProduct = (TimedProduct)product;
				sb.append("  ")
					.append(timedProduct.toString(productInfo.getAmount()))
					.append('\n');
			}

			totalPrice += effectivePrice * productInfo.getAmount();
		}

		double finalPrice = (totalPrice - totalDiscount);
		
		sb.append("  Total price: ")   .append(DECIMAL_FORMAT.format(totalPrice))   .append("\n");
		sb.append("  Total discount: ").append(DECIMAL_FORMAT.format(totalDiscount)).append("\n");
		sb.append("  Final Price: ")   .append(DECIMAL_FORMAT.format(finalPrice))   .append("\n");
		return sb.toString();
	}

	/**
	 * Searches for a duplicate product info to the product info parameter.
	 * A duplicate product info constitutes one that has the same 'representation'. Meaning:
	 *   - A BaseProduct is duplicate of another BaseProduct if it has the same id AND the same personalizations
	 *   - A TimedProduct is duplicate of another TimedProduct if it has the same id. Meaning you can only add a
	 *     TimedProduct once to a ticket.
	 *
	 * @param productInfo   the product info to search with
	 * @return              the duplicate product info found or null
	 */
	private ProductInfo findDuplicateProductInfo(ProductInfo productInfo) {
		ProductInfo result = null;
		ProductInfo currentProductInfo = null;		
		Iterator<ProductInfo> iterator = productInfos.iterator();

		while (result == null && iterator.hasNext()) {
			currentProductInfo = iterator.next();
			if (productInfo.equalProductInfo(currentProductInfo)) {
				result = currentProductInfo;
			}
		}
		return result;
	}

	/**
	 * Helper to get a append a productInfo in the product info array.
	 * Standard array append checking the max length
	 *
	 * @param productInfo   the productInfo to add at the end
	 * @return              false if 'count == productInfos.length'
	 */
	private boolean appendProductInfo(ProductInfo productInfo) {
		boolean result = false;
		if (totalAmount <= MAX_PRODUCTS) {
			productInfos.add(productInfo);
			totalAmount += productInfo.getAmount();
			result = true;
		}
		return result;
	}

	public int compareTo(Ticket ticket) {
		return this.id - ticket.id;
	}
}
