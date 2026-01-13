/* date = December 28th 2025 9:38 pm */

package es.upm.etsisi.poo;
import es.upm.etsisi.poo.exceptions.InvalidDataException;
import java.io.Serializable;

public abstract class InventoryItem implements Serializable {
    protected final int id;
    
    public InventoryItem(int id) throws InvalidDataException {
        this.id = id;
        if (!isValidId(id)) throw new InvalidDataException(id + " is not a valid product id");
    }
    
    public int getId() {
        return this.id;
    }
    
	public abstract boolean isInstanceUnique();
	public abstract String toString();
    public abstract InventoryItem copy();
	public abstract TicketItem getTicketItem(int amount, String[] personalization) throws InvalidDataException;
	public abstract InventoryItemId getInventoryId();
    
    public static boolean isValidId(int idToCheck) {
        return idToCheck >= 0;
    }
}
