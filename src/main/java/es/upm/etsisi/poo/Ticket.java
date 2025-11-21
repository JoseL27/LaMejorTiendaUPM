package es.upm.etsisi.poo;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Ticket class to manage an application ticket which consists of a product list with amounts.
 * @see Product
 */
public class Ticket implements Comparable<Ticket> {
	/**
	 * Max amount of products allowed in the Ticket, as the requirement documents specifies.
	 */
	public static final int MAX_PRODUCTS = 100;

	/**
	 * The percent cost added to the price of an item per personalization’
	 */
	public static final float PERSONALIZATION_EXTRA_PERCENT = 0.1f;

	/**
	 * Date format in which the start and ending dates appear in the string representation of the id
	 */
	private static final SimpleDateFormat ID_DATE_FORMAT = new SimpleDateFormat("YY-MM-dd-HH:mm");

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
	private Date dateOpened;

	/**
	 * The date in which the close() function was called.
	 */
	private Date dateClosed;

	private boolean isOpen;


	/**
	 * Constructor with the id.
	 * To create a ticket with a "random id" use randomId() function
	 * then check if the id is unique in the store and create it using this constructor.
	 */
	public Ticket(int id) {
		resetProductInfos();
		this.id = id;
		this.isOpen = false;
		this.dateOpened = new Date();
		this.dateClosed = null;
	}

	public int getId() {
		return this.id;
	}
	
	public Date getDateOpened() {
		return this.dateOpened;
	}
	
	public Date getDateClosed() {
		return this.dateClosed;
	}

	public String getComposedId() {
		String dateOpenedString = ID_DATE_FORMAT.format(this.dateOpened);
		String idString = String.format("%s-%d", dateOpenedString, this.id);

		if (dateClosed != null) {
			String dateClosedString = ID_DATE_FORMAT.format(this.dateClosed);
			idString += String.format("-%s", dateClosedString);
		}
		return idString;
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
			this.dateClosed = new Date();
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
					if (result) { 
						timedProduct.setAmount(amount);
					}
				}
			}
			result = appendProductInfo(newProductInfo);
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
				categoriesProductCount[categoryIndex]++;
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

		double totalPrice = 0;
		double totalDiscount = 0;
		double totalPersExtra = 0;

		productInfos.sort(null);

		int[] categoriesCount = categoriesProductCount();

		for (ProductInfo productInfo : productInfos) {
			Product product = productInfo.getProduct();

			double multipliedPrice = 0;

			sb.append(product.toString());


			if (product instanceof BaseProduct) {
				BaseProduct baseProduct = (BaseProduct)product;

				int personalizationCount = productInfo.getPersonalizations() != null 
					? productInfo.getPersonalizations().length : 0;
				double persExtra = multipliedPrice * PERSONALIZATION_EXTRA_PERCENT * personalizationCount;
				boolean hasPersExtra = (int)persExtra != 0;
				totalPersExtra += persExtra;

				multipliedPrice = baseProduct.getPrice();
				int categoryIndex = (int)baseProduct.getCategory().ordinal();
				boolean hasDiscount = (categoriesCount[categoryIndex]) > 1;
				double discountPercent = baseProduct.getCategory().getDiscountPercent(); 
				double discount = discountPercent * multipliedPrice;
				totalDiscount += discount;

				for (int i = 0; i < productInfo.getAmount(); i++) {
					sb.append("  ");
					sb.append(baseProduct);
					if (hasDiscount) {
						sb.append(String.format(" **discount -%.1f", (float)discount));
					}
					if (hasPersExtra) { 
						sb.append(String.format(" **pers extra -%.1f", (float)persExtra));
					}
					sb.append('\n');
				}
				
			} else if (product instanceof TimedProduct) {
				TimedProduct timedProduct = (TimedProduct)product;
				multipliedPrice = timedProduct.getPrice() * timedProduct.getAmount();
				sb.append("  ");
				sb.append(timedProduct);
				sb.append('\n');
			}

			totalPrice += multipliedPrice;
		}

		double finalPrice = (totalPrice - totalDiscount + totalPersExtra);
		
		sb.append(String.format("  Total price: %.1f\n", totalPrice));
		sb.append(String.format("  Total discount: %.1f\n", totalDiscount));
		sb.append(String.format("  Final Price: %.1f", finalPrice));
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
			if (productInfo.getProduct().getId() == currentProductInfo.getProduct().getId()) {
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
		return this.getComposedId().compareTo(ticket.getComposedId());
	}
	
	public static void main(String[] args) {
		Ticket ticket = new Ticket(0);

		BaseProduct book = new BaseProduct(0, "book", 20, BaseProduct.Category.BOOK, 0);
		int amount = 2;
		ticket.addProduct(book, amount, null);
		ticket.addProduct(book, amount, null);
		ticket.addProduct(book, amount, null);
		ticket.addProduct(book, amount, null);
		ticket.addProduct(book, amount, null);


		TimedProduct food = new TimedProduct(1, "food", 10, 30, TimedProduct.TimedType.FOOD, LocalDateTime.now().plusDays(3));
		amount = 20;
		ticket.addProduct(food, amount, null);
		ticket.addProduct(food, amount, null);
		ticket.addProduct(food, amount, null);
		ticket.addProduct(food, amount, null);

		System.out.println(ticket.summaryString());
	}
}
