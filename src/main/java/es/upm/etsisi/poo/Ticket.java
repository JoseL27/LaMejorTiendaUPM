package es.upm.etsisi.poo;

import es.upm.etsisi.poo.exceptions.DuplicateItemException;
import es.upm.etsisi.poo.exceptions.FullCollectionException;

import java.time.DateTimeException;
import java.util.Iterator;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;

/**
 * Ticket class to manage an application ticket which consists of a product list with amounts.
 *
 * @see Product
 */
public abstract class Ticket implements Comparable<Ticket> {
	public static final int MAX_PRODUCTS = 100;
	
    /**
     * Date format in which the start and ending dates appear in the string representation of the id
     */
	public static final DateTimeFormatter ID_DATE_FORMAT = DateTimeFormatter.ofPattern("YY-MM-dd-HH:mm");
	
    /**
     * Decimal format to use in summaryString()
     */
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0###");
	
	protected ArrayList<ProductInfo> productInfos;
	
    /**
     * The total summed amount of all productInfo's amount
     */
	protected int totalAmount;
	
    /**
     * The number part of the ticket. 5 digit number (between 0 and 99999)
     */
	private int id;
	
    private LocalDateTime dateOpened;
	
    private LocalDateTime dateClosed;
	
    private boolean isOpen;
	
	
    /**
     * Constructor with the id.
     * To create a ticket with a "random id" use randomId() function
     * then check if the id is unique in the store and create it using this constructor.
     */
    public Ticket(int id) throws IllegalArgumentException {
        if (id < 0) throw new IllegalArgumentException("Ticket id should be a not negative number");
        resetProductInfos();
        this.id = id;
        this.isOpen = true;
		
        this.dateOpened = App.now();
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
	
    public boolean isOpen() {
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
	
    public void close() throws DateTimeException {
		if (this.isOpen) {
            for (ProductInfo productInfo : productInfos) {
				Product product = productInfo.getProduct(); //Get the product
				if (product instanceof TimedProduct timedProduct) {
					if (App.now().isAfter(timedProduct.getExpirationDate())) {
						throw new DateTimeException(String.format("Product %s is past its expiration date", product));
					}
				}
			}
			this.productInfos = finalProductInfo();
			this.isOpen = false;
            this.dateClosed = App.now();
        }
    }
	
	public abstract void addProduct(Product product, int amount, String[] personalizations) throws DuplicateItemException, FullCollectionException;
	
    /**
     * Search and remove the product with the corresponding id.
     * <p>
     * Lookup is standard linear search and actual removal is O(1) due to
     * unordered removal (replaces element to remove with the last element).
     *
     * @param id the id of the product to attempt to remove
     * @return the removed product if it was found or null if it wasn't
     */
    public boolean removeProduct(int id) {
        boolean result = false;
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
			
            if (removed instanceof TimedProduct) {
                this.totalAmount--;
            } else {
                this.totalAmount -= foundProductInfo.getAmount();
            }
			
            result = productInfos.remove(foundProductInfo);
        }
		
        return result;
    }
	
	
	/**
     * Get a summary string of the current ticket products associated with an amount and discount.
     * Sorts the productInfos array on product name string comparison (alfabetically).
*/
	public abstract String summaryString();
	
    /**
     * Searches for a duplicate product info to the product info parameter.
     * A duplicate product info constitutes one that has the same 'representation'. Meaning:
     * - A BaseProduct is duplicate of another BaseProduct if it has the same id AND the same personalizations
     * - A TimedProduct is duplicate of another TimedProduct if it has the same id. Meaning you can only add a
     * TimedProduct once to a ticket.
     *
     * @param productInfo the product info to search with
     * @return the duplicate product info found or null
     */
	protected ProductInfo findDuplicateProductInfo(ProductInfo productInfo) {
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
     * @param productInfo the productInfo to add at the end
     * @return false if 'count == productInfos.length'
     */
	protected void appendProductInfo(ProductInfo productInfo) throws FullCollectionException{
        Product product = productInfo.getProduct();
        if (product instanceof TimedProduct) {
            if ((this.totalAmount + 1) > MAX_PRODUCTS) throw new FullCollectionException("Ticket is already full");
            productInfos.add(productInfo);
        } else {
            if ((productInfo.getAmount() + totalAmount) > MAX_PRODUCTS) throw new FullCollectionException("Ticket is already full");
            productInfos.add(productInfo);
        }
    }
	
    public int compareTo(Ticket ticket) {
        return this.id - ticket.id;
    }
	
    /**
     * This function made a new ProductInfo with new instances when the ticket is close
     * to prevent the modification of the objects by refereen
     * @return the new array
     */
    private ArrayList<ProductInfo> finalProductInfo() {
		ArrayList<ProductInfo> aux=new ArrayList<>(this.productInfos.size());
		for (ProductInfo productInfo : this.productInfos) {
			// New array with the personalizations
			String[] persAux=new String[productInfo.getPersonalizations().length];
			for (int i = 0; i < persAux.length; i++) { //Copy
				persAux[i]=productInfo.getPersonalizations()[i];
			}
			
			// New instance of every single object
			Product oldProduct = productInfo.getProduct();
			if(oldProduct instanceof TimedProduct oldProductAux){
				// New TimedObject
				TimedProduct timedProductAux = new TimedProduct(oldProduct.getId(),oldProduct.getName(),oldProduct.getPrice(),
																oldProductAux.getMaxParticipants(),oldProductAux.getType().toString(),oldProductAux.getExpirationDate());
				//New instance
				ProductInfo info= new ProductInfo(timedProductAux,productInfo.getAmount(),persAux);
				aux.add(info);
			}else{
				BaseProduct oldProductAux = (BaseProduct) oldProduct;
				// New TimedObject
				BaseProduct BaseProductAux = new BaseProduct(oldProduct.getId(),oldProduct.getName(),oldProduct.getPrice(),
															 oldProductAux.getCategory().toString(),oldProductAux.getMaxPersonalizations(),oldProductAux.getPersonalized());
				//New instance
				ProductInfo info= new ProductInfo(BaseProductAux,productInfo.getAmount(),persAux);
				aux.add(info);
			}
		}
		return aux;
	}
}
