package es.upm.etsisi.poo;

import java.util.Arrays;
import java.time.DateTimeException;
import es.upm.etsisi.poo.exceptions.*;

public abstract class TicketItem implements Comparable<TicketItem> {
	/**
	 * ProductInfo struct-like holder as a Product pair.
	 * Has basic constructors and getters.
	 * Implements 'Comparable' class to alfabetically order products as in the requirement document.
	 */
	
	protected InventoryItem item;
	protected int amount;
	
	protected TicketItem(InventoryItem item, int amount) {
        if (item == null) throw new IllegalArgumentException("Can not create a info from a null item");
        if (amount < 0) throw new IllegalArgumentException("Expected a positive amount, got " + amount);
        
		this.item = item;
		this.amount = amount;
	}
	
	public InventoryItem getItem() { 
		return this.item;
	}
	
	public int getAmount() { 
		return this.amount;
	}
	
	public void addAmount(int more) { 
		this.amount += more; 
	}
	
	public boolean equals(TicketItem other) {
		return (other != null && 
				this.item.getId() == other.getItem().getId());
	}
	
	// NOTE(erb): no discount by default
	public double getItemDiscount(int[] categoriesCount) {
		return 0;
	}
	
	// NOTE(erb): no discount by default
	public double getDiscountPercentOverAll(int[] categoriesCount) {
		return 0;
	}
	
	public int compareTo(TicketItem other) {
		if (other != null) {
			return this.item.getId() - other.item.getId();
		}
		return -1;
	}
	
	public abstract void validate() throws DateTimeException;
	public abstract String toString();
	public abstract double getPrice();
	public abstract TicketItem copy();
}
