package es.upm.etsisi.poo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;

import es.upm.etsisi.poo.exceptions.DataException;
import es.upm.etsisi.poo.exceptions.DuplicateItemException;
import es.upm.etsisi.poo.exceptions.FullCollectionException;
import es.upm.etsisi.poo.exceptions.IdSpaceExhaustedException;
import es.upm.etsisi.poo.exceptions.MissingItemException;

class InventoryItemId implements Serializable {
    public int id;
    public boolean isProduct;
    
    public InventoryItemId(int id, boolean isProduct) {
        this.id = id;
        this.isProduct = isProduct;
    }
    
    @Override
        public int hashCode() {
        if (isProduct) {
            return id;
        } else {
            return -id;
        }
    }
    
    @Override
        public boolean equals(Object obj) {
        return (obj != null && obj.getClass() == this.getClass() && this.hashCode() == obj.hashCode());
    }
}

public class Inventory implements Serializable {
    
    public static final int MAX_PRODUCTS = 200; // E1: no more than 200 products
    
    private HashMap<InventoryItemId, InventoryItem> items;
    private int nextProductId;
    private int nextServiceId;
    
    public static Inventory instance;
    
    public static Inventory getInstance() {
        if (instance == null) {
			instance = (Inventory)Serialize.get("Inventory");
			if (instance == null) {
				instance = new Inventory();
                Serialize.put("Inventory", instance);
			}
        }
        return instance;
    }
    
    private Inventory() {
        this.items = new HashMap<>();
        nextProductId = 0;
        nextServiceId = 1;
    }
    
    /**
     * Creates a product and adds it to the array
     *
     * @param id       Product ID (must be a positive integer)
     * @param name     Product name (length must be less than 100)
     * @param category Product Category
     * @param price    Product price (must be greater than 0)
     * @return true if the product is created correctly, false in other case
     */
    public BaseProduct createBaseProduct(int id, String name, String category, double price, int maxPers, boolean personalized) throws DataException{
        BaseProduct prodToAdd = null;
        try {
            prodToAdd = new BaseProduct(id, name, price, category, maxPers, personalized);
        }catch (Exception ex){
            throw new DataException("Failed to create product " + ex.getMessage());
        }
        return (BaseProduct)addItem(prodToAdd);
    }
    
    /**
     * Tries to create a new timed product with its attributes set to the values of the parameters
     *
     * @return The product that was created, or null if the creation failed
     */
    public TimedProduct createTimedProduct(int id, String name, double price, int people, String typeStr, LocalDateTime expirationDate) throws DataException, IllegalArgumentException {
		if (expirationDate.isBefore(App.now())) {
			throw new IllegalArgumentException("Expiration can not be in the past, got " + 
											   expirationDate.format(TimedProduct.EXPIRATION_DATE_FORMAT));
		}
		
        TimedProduct prodToAdd = new TimedProduct(id, name, price, people, typeStr, expirationDate);
        return (TimedProduct)addItem(prodToAdd);
    }
    
    /**
     * Tries to create a new service product
     *
     * @return The product that was created, or null if the creation failed
     */
    public ServiceProduct createServiceProduct(String categoryStr, LocalDateTime expirationDate) throws DataException, IllegalArgumentException {
		if (expirationDate.isBefore(App.now())) {
			throw new IllegalArgumentException("Expiration can not be in the past, got " + 
											   expirationDate.format(ServiceProduct.EXPIRATION_DATE_FORMAT));
		}
		
		ServiceProduct service = new ServiceProduct(nextServiceId++, categoryStr, expirationDate);
        return (ServiceProduct)addItem(service);
    }
    
    
    /**
     * Updates a product's name specifying its product ID
     *
     * @param id   Product ID (must be a positive integer)
     * @param name Product name (length must be less than 100)
     * @return true if the product's name is updated correctly, false in other case
     */
    public Product updateProductName(int id, String name) throws DataException {
        Product selectedProduct = this.getProduct(id);
		selectedProduct.setName(name);
        return selectedProduct;
    }
    
    /**
     * Updates a product's price specifying its product ID
     *
     * @param id    Product ID (must be a positive integer)
     * @param price Product price (must be greater than 0)
     * @return true if the product's price is updated correctly, false in other case
     */
    public Product updateProductPrice(int id, double price) throws DataException {
        Product selectedProduct = this.getProduct(id);
		selectedProduct.setPrice(price);
        return selectedProduct;
    }
    
    /**
     * Updates a product's category specifying its product ID
     *
     * @param id       Product ID (must be a positive integer)
     * @param category Product Category
     * @return true if the product's category is updated correctly, false in other case
     */
    public BaseProduct updateProductCategory(int id, String category) throws DataException {
        BaseProduct selectedProduct = this.getBaseProduct(id);
		selectedProduct.setCategory(category);
        return selectedProduct;
    }
    
    /**
     * @return the delete item
     */
    public InventoryItem deleteItem(int id) throws MissingItemException{
        InventoryItem deleted = this.items.remove(new InventoryItemId(id, true));
        if (deleted == null) {
            deleted = this.items.remove(new InventoryItemId(id, false));
        }
        if (deleted == null) {
            throw MissingItemException.fromId("Product", id);
        }
        return deleted;
    }
    
    /**
     * Returns an array of all products added.
     */
    public Collection<InventoryItem> getItems() {
        return this.items.values();
    }
    
    public int generateUniqueProductId() throws IdSpaceExhaustedException {
        int idQuery = nextProductId - 1;
		boolean foundId = false;
		while (!foundId && idQuery != Integer.MAX_VALUE) {
			idQuery += 1;
			final InventoryItem prod = this.items.get((new InventoryItemId(idQuery, true/*false*/)));
			foundId = (prod == null);
		}
		
		if (foundId) {
			nextProductId = idQuery;
            //System.out.println("next product id updated to " + nextProductId);
		} else {
			throw new IdSpaceExhaustedException("No more left ids in inventory");
		}
		
		return idQuery;
    }
    
    private InventoryItem addItem(InventoryItem item) throws FullCollectionException, DuplicateItemException {
        if (this.items.size() >= MAX_PRODUCTS) {
            throw new FullCollectionException("Product inventory is full");
        }
        
        InventoryItemId invId = item.getInventoryId();
        
        InventoryItem duplicate = this.items.get(invId);
        if (duplicate != null) { 
            throw DuplicateItemException.fromId("Product", item.getId());
        }
        
        this.items.put(invId, item);
        return item;
    }
    
	public InventoryItem getItemFromStringId(String strId) throws DataException, NumberFormatException {
		
		boolean isProduct = true;
		if (ServiceProduct.isIdString(strId)) {
			strId = strId.substring(0, strId.length() - 1);
			isProduct = false;
		}
		int idNum = Integer.parseInt(strId);
		
		InventoryItemId itemId = new InventoryItemId(idNum, isProduct);
        InventoryItem item = this.items.get(itemId);
		
		if (item == null) {
			throw MissingItemException.fromId("Item", idNum);
		} 
		
		return item;
	}
    
    public ServiceProduct getService(int id) throws DataException {
        ServiceProduct result = null;
        InventoryItemId itemId = new InventoryItemId(id, false);
        InventoryItem service = this.items.get(itemId);
        
        if (service != null) {
            if (service instanceof Product) {
                result = (ServiceProduct)service;
            } else {
                throw new DataException("Product with id " + id + " is not a product");
            }
        } else {
            throw MissingItemException.fromId("Product", id);
        }
        
        return result;
    }
    
    public Product getProduct(int id) throws MissingItemException, DataException {
        Product result = null;
        InventoryItemId itemId = new InventoryItemId(id, true);
        InventoryItem prod = this.items.get(itemId);
        
        if (prod != null) {
            if (prod instanceof Product) {
                result = (Product)prod;
            } else {
                throw new DataException("Product with id " + id + " is not a product");
            }
        } else {
            throw MissingItemException.fromId("Product", id);
        }
        
        return result;
    }
    
    
    public BaseProduct getBaseProduct(int id) throws DataException {
        Product prod = getProduct(id);
        BaseProduct result = null;
        
        if (prod instanceof BaseProduct) {
            result = (BaseProduct)prod;
        } else {
            throw new DataException("Product with id " + id + " is not a personalizable product");
        }
        
        return result;
    }
	
    
}
