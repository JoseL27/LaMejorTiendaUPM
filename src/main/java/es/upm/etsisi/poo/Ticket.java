package es.upm.etsisi.poo;

import es.upm.etsisi.poo.exceptions.*;

import java.time.DateTimeException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;

/**
 * Ticket class to manage an application ticket which consists of a product list with amounts.
 *
 * @see Product
 */
public abstract class Ticket {
	public static final int MAX_PRODUCTS = 100;
	
    /**
     * Date format in which the start and ending dates appear in the string representation of the id
     */
	public static final DateTimeFormatter ID_DATE_FORMAT = DateTimeFormatter.ofPattern("YY-MM-dd-HH:mm");
	
    /**
     * Decimal format to use in summaryString()
     */
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0###");
	
	protected ArrayList<TicketItem> ticketItems;
	
    /**
     * The total summed amount of all ticketItems
     */
	protected int totalAmount;
	
    /**
     * The number part of the ticket id. 5 digit number (between 0 and 99999)
     */
	private final int id;
	
	/**
     * The full ticket id
     */
	private String composedId;
	
    private LocalDateTime dateOpened;
	
    private LocalDateTime dateClosed;
	
    private boolean isOpen;
	
	
    /**
     * Constructor with the id.
     * To create a ticket with a "random id" use randomId() function
     * then check if the id is unique in the store and create it using this constructor.
     */
    public Ticket(int id, boolean isIdCustom) throws IllegalArgumentException {
        if (id < 0) throw new IllegalArgumentException("Ticket id should be a not negative number");
		this.ticketItems = new ArrayList<TicketItem>();
        this.id = id;
        this.isOpen = true;
        this.dateOpened = App.now();
        this.dateClosed = null;
		
		if (isIdCustom) {
			this.composedId = String.format("%05d", this.id);
		} else {
			this.composedId = String.format("%s-%05d", 
											this.dateOpened.format(ID_DATE_FORMAT), this.id);
		}
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
		return composedId;
    }
	
    public boolean isOpen() {
        return this.isOpen;
    }
	
    public boolean isEmpty() {
        return this.ticketItems.isEmpty();
    }
	
	@Override
		public String toString() {
		
		String status = null;
		
		if (isEmpty()) {
			status = "EMPTY";
		} else if (isOpen){
			status = "OPEN";
		} else {
			status = "CLOSE";
		}
		
		String result = String.format("%s - %s", composedId, status);
		return result;
	}
	
	/**
     * Validate the item kind to add
*/
	public abstract boolean validateItemKind(InventoryItem item);
	
	/**
     * Get a summary string of the current ticket products associated with an amount and discount.
     * Sorts the productInfos array on product name string comparison (alfabetically).
*/
	public abstract String summaryString();
	
    public void close() throws DateTimeException {
		if (this.isOpen) {
			
			ArrayList<TicketItem> aux = new ArrayList<>(this.ticketItems.size());
			for (TicketItem tItem : this.ticketItems) {
				
				// New valid instance of every single object
				tItem.validate();
				aux.add(tItem.copy());
			}
			
			this.ticketItems = aux;
			this.isOpen = false;
            this.dateClosed = App.now();
			
			this.composedId = String.format("%s-%s", composedId, dateClosed.format(ID_DATE_FORMAT));
        }
    }
	
    /**
     * Search and remove the product with the corresponding id.
     * <p>
     * Lookup is standard linear search and actual removal is O(1) due to
     * unordered removal (replaces element to remove with the last element).
     *
     * @param id the id of the product to attempt to remove
     * @return the removed product if it was found or null if it wasn't
     */
    public void removeItem(int id) throws MissingItemException {
		TicketItem foundInfo = null;
		TicketItem currentInfo = null;
		
        Iterator<TicketItem> iterator = ticketItems.iterator();
        while (foundInfo == null && iterator.hasNext()) {
			currentInfo = iterator.next();
            if (id == currentInfo.getItem().getId()) {
                foundInfo = currentInfo;
            }
        }
		
        if (foundInfo != null) {
			iterator.remove();
			this.totalAmount -= foundInfo.getAmount();
            //result = productInfos.remove(foundProductInfo);
        } else {
			throw new MissingItemException("Ticket does not contain item with id " + id);
		}
    }
	
	private TicketItem findDuplicateItem(TicketItem searchItem) {
		TicketItem result = null;
		TicketItem currentInfo = null;
        Iterator<TicketItem> iterator = ticketItems.iterator();
		
        while (result == null && iterator.hasNext()) {
            currentInfo = iterator.next();
			if (currentInfo.equals(searchItem)) {
				result = currentInfo;
			}
        }
        return result;
    }
	
    public void addItem(InventoryItem item, int amount, String[] personalizations) throws DataException, IllegalArgumentException, DateTimeException {
		
		if (!this.validateItemKind(item)) {
			throw new IllegalArgumentException("This Ticket only accepts products");
		}
		
		TicketItem infoToAdd = item.getTicketItem(amount, personalizations);
		infoToAdd.validate();
		TicketItem duplicate = this.findDuplicateItem(infoToAdd);
		
		if (duplicate != null) {
			if (item.isInstanceUnique()) {
				throw new DuplicateItemException("This product already exists in the ticket");
			}
			
			if (this.totalAmount + amount <= MAX_PRODUCTS) {
				duplicate.addAmount(amount);
				this.totalAmount += amount;
			} else {
				throw new FullCollectionException("Ticket is full");
			}
			
		} else {
			if (infoToAdd.getAmount() + totalAmount <= MAX_PRODUCTS) {
				this.ticketItems.add(infoToAdd);
				this.totalAmount += infoToAdd.getAmount();
			} else {
				throw new FullCollectionException("Ticket is full");
			}
		}
    }
	
}
