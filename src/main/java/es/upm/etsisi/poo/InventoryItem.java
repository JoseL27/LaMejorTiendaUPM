/* date = December 28th 2025 9:38 pm */

package es.upm.etsisi.poo;

public abstract class InventoryItem {
    
    protected final int id;
    
    public InventoryItem(int id) {
        this.id = id;
        if (!isValidId(id)) throw new IllegalArgumentException(id + " is not a valid product id");
    }
    
    public int getId() {
        return this.id;
    }
    
	public abstract boolean duplicateOf(InventoryItem product);
    
    public abstract String toString();
    
    //public abstract InventoryItem clone();
    
    public static boolean isValidId(int idToCheck) {
        return idToCheck >= 0;
    }
    
}
