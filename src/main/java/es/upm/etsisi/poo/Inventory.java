package es.upm.etsisi.poo;

import es.upm.etsisi.poo.exceptions.DataException;
import es.upm.etsisi.poo.exceptions.DuplicateItemException;
import es.upm.etsisi.poo.exceptions.FullCollectionException;
import es.upm.etsisi.poo.exceptions.MissingItemException;

import java.util.*;
import java.time.LocalDateTime;

class InventoryItemId { 
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

public class Inventory {
    
    public static final int MAX_PRODUCTS = 200; // E1: no more than 200 products
    
    private Map<InventoryItemId, InventoryItem> items;
    private int nextProductId;
    private int nextServiceId;
    
    private static Inventory instance = new Inventory();
    
    public static Inventory getInstance() {
        if (instance == null) {
            instance = new Inventory();
        }
        return instance;
    }
    
    private Inventory() {
        this.items = new HashMap<>();
        nextProductId = 0;
        nextServiceId = 0;
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
    public TimedProduct createTimedProduct(int id, String name, double price, int people, String type, LocalDateTime expirationDate) throws DataException{
        TimedProduct prodToAdd = null;
        try {
            prodToAdd = new TimedProduct(id, name, price, people, type, expirationDate);
        }catch (IllegalArgumentException ex){
            throw new DataException("Failed to create product: " + ex.getMessage());
        }
        return (TimedProduct)addItem(prodToAdd);
    }
    
    
    /**
     * Tries to create a new service product
     *
     * @return The product that was created, or null if the creation failed
     */
    public ServiceProduct createServiceProduct(ServiceProduct.Category category, LocalDateTime expirationDate) throws DataException {
        ServiceProduct service = null;
        try {
            int id = (nextServiceId++) + 1;
            service = new ServiceProduct(id, category, expirationDate);
        } catch (IllegalArgumentException e) {
            nextServiceId--;
            throw new DataException("Failed to create service " + e.getMessage());
        }
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
        try {
            selectedProduct.setName(name);
        } catch (IllegalArgumentException ex){
            throw new DataException("Unable to update name: " + ex.getMessage());
        }
        return selectedProduct;
    }
    
    /**
     * Updates a product's price specifying its product ID
     *
     * @param id    Product ID (must be a positive integer)
     * @param price Product price (must be greater than 0)
     * @return true if the product's price is updated correctly, false in other case
     */
    public Product updateProductPrice(int id, double price) throws DataException{
        Product selectedProduct = this.getProduct(id);
        try{
            selectedProduct.setPrice(price);
        }catch (IllegalArgumentException ex){
            throw new DataException("Unable to update price: " + ex.getMessage());
        }
        return selectedProduct;
    }
    
    /**
     * Updates a product's category specifying its product ID
     *
     * @param id       Product ID (must be a positive integer)
     * @param category Product Category
     * @return true if the product's category is updated correctly, false in other case
     */
    public BaseProduct updateProductCategory(int id, String category) throws DataException{
        BaseProduct selectedProduct = this.getBaseProduct(id);
        try {
            selectedProduct.setCategory(category);
        }catch (IllegalArgumentException ex){
            throw new DataException("Unable to update category, " + category + " is not a valid category");
        }
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
    public Collection<InventoryItem> listItems() {
        return this.items.values();
    }
    
    public int generateUniqueProductId() {
        return (nextProductId++) + 1;
    }
    
    private InventoryItem addItem(InventoryItem item) throws FullCollectionException, DuplicateItemException {
        if (this.items.size() >= MAX_PRODUCTS) {
            throw new FullCollectionException("Product inventory is full");
        }
        
        boolean isProduct = (item instanceof Product);
        InventoryItemId id = new InventoryItemId(item.getId(), isProduct);
        
        InventoryItem duplicate = this.items.get(id);
        if (duplicate != null) { 
            throw DuplicateItemException.fromId("Product", item.getId());
        }
        
        if (isProduct) {
            nextProductId = item.getId()+1;
        } else {
            nextServiceId = item.getId()+1;
        }
        
        this.items.put(id, item);
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
    
    public Product getProduct(int id) throws DataException {
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
