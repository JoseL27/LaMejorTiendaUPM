package es.upm.etsisi.poo;

import java.io.Serializable;
import java.util.Arrays;
import java.time.DateTimeException;
import es.upm.etsisi.poo.exceptions.*;

public abstract class TicketItem implements Comparable<TicketItem>, Serializable {
	/**
	 * ProductInfo struct-like holder as a Product pair.
	 * Has basic constructors and getters.
	 * Implements 'Comparable' class to alfabetically order products as in the requirement document.
	 */
	
	protected InventoryItem item;
	protected int amount;
	
	protected TicketItem(InventoryItem item, int amount) {
		assert item != null : "Ticket item's item can't be null";
		assert amount >= 0 : "Ticket item amount has to be positive";
        
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
				this.item.getClass().equals(other.item.getClass()) &&
				this.item.getInventoryId().equals(other.getItem().getInventoryId()));
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
	
	public abstract void validate() throws InvalidDataException;
	public abstract String toString();
	public abstract double getPrice();
	public abstract TicketItem copy();
}
