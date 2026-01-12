/* date = December 28th 2025 9:38 pm */

package es.upm.etsisi.poo;

import java.io.Serializable;

public abstract class InventoryItem implements Serializable {
    
    protected final int id;
    
    public InventoryItem(int id) {
        this.id = id;
        if (!isValidId(id)) throw new IllegalArgumentException(id + " is not a valid product id");
    }
    
    public int getId() {
        return this.id;
    }
    
	public abstract boolean isInstanceUnique();
	public abstract String toString();
    public abstract InventoryItem copy();
	public abstract TicketItem getTicketItem(int amount, String[] personalization) throws IllegalArgumentException;
	public abstract InventoryItemId getInventoryId();
    
    public static boolean isValidId(int idToCheck) {
        return idToCheck >= 0;
    }
}
