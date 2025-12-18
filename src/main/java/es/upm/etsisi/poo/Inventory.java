package es.upm.etsisi.poo;

import es.upm.etsisi.poo.exceptions.DataException;
import es.upm.etsisi.poo.exceptions.DuplicateItemException;
import es.upm.etsisi.poo.exceptions.FullCollectionException;
import es.upm.etsisi.poo.exceptions.MissingItemException;

import java.time.LocalDateTime;

/**
 * Manages Create Read Update Delete operations for Products using Array to store the products. Provides input
 * sanity checks for all operations.
 */
public class Inventory {

    public static final int MAX_PRODUCTS = 200; // E1: no more than 200 products

    private Product[] inventory; // List
    private int productAmount;


	private static Inventory instance = new Inventory();

	public static Inventory getInstance() {
		if (instance == null) {
			instance = new Inventory();
		}
		return instance;
	}

    private Inventory() {
        this.inventory = new Product[MAX_PRODUCTS];
        this.productAmount = 0;
    }

    /**
     * Attempts to find the product with the same ID, returning its position in the array
     * @param id Product ID
     * @return Product object's index in the array, -1 if not found or ID is invalid (ID < 0)
     */
    public int readProductIndex(int id) {
        if (!Product.isValidId(id)) return -1;
        int result = -1;
        int i = 0;
        while (result == -1 && i < this.productAmount) {
            if (this.inventory[i].getId() == id)
                result = i;
            i++;
        }
        return result;
    }

    /**
     * Attempts to find the product with the same ID
     * @param id Product ID
     * @return Product with the specified id, null if not found or ID is invalid (ID < 0)
     */
    public Product readProduct(int id) {
        if (!Product.isValidId(id)) return null;
        // Linear search for products with the same ID
        Product result = null;
        int i = 0;
        while (result == null && i < this.productAmount) {
            if (this.inventory[i].getId() == id)
                result = this.inventory[i];
            i++;
        }
        return result;
    }

    /**
     * Creates a product and adds it to the array
     * @param id Product ID (must be a positive integer)
     * @param name Product name (length must be less than 100)
     * @param category Product Category
     * @param price Product price (must be greater than 0)
     * @return true if the product is created correctly, false in other case
     */
    public BaseProduct createBaseProduct(int id, String name, String category, double price, int maxPers, boolean personalized) throws DataException{

        // Check inventory full
        if (this.productAmount >= MAX_PRODUCTS) return null;

        Product selectedProduct = this.readProduct(id);
        if (selectedProduct != null) throw new DuplicateItemException("Product with id " + id + " already exists");

        try {
            BaseProduct prodToAdd = new BaseProduct(id, name, price, category, maxPers, personalized);
            this.inventory[this.productAmount] = prodToAdd;
            this.productAmount++;
            return prodToAdd;
        }catch (IllegalArgumentException ex){
            throw new DataException("Error creating product: " + ex.getMessage());
        }
    }

    /**
     * Tries to create a new timed product with its attributes set to the values of the parameters
     * @return The product that was created, or null if the creation failed
     */
    public TimedProduct createTimedProduct(int id, String name, double price, int people, String type, LocalDateTime expirationDate) throws DataException{
        // Check inventory full
        if (this.productAmount >= MAX_PRODUCTS) throw new FullCollectionException("No space left in inventory");

        //Check if already exists
        Product selectedProduct = this.readProduct(id);
        if (selectedProduct != null) throw new DuplicateItemException("Product with id " + id + " already exists");

        try {
            TimedProduct prodToAdd = new TimedProduct(id, name, price, people, type, expirationDate);
            this.inventory[this.productAmount] = prodToAdd;
            this.productAmount++;
            return prodToAdd;
        }catch (IllegalArgumentException ex){
            throw new DataException("Error creating product: " + ex.getMessage());
        }
    }

    /**
     * Updates a product's name specifying its product ID
     * @param id Product ID (must be a positive integer)
     * @param name Product name (length must be less than 100)
     * @return true if the product's name is updated correctly, false in other case
     */
    public Product updateProductName(int id, String name) throws DataException {
        Product selectedProduct = this.readProduct(id);

        if (selectedProduct == null) throw new MissingItemException("Product with id " + id + "does not exist");
        try {
            selectedProduct.setName(name);
        }catch (IllegalArgumentException ex){
            throw new DataException("Unable to update name: " + ex.getMessage());
        }
		return selectedProduct;
    }

    /**
     * Updates a product's price specifying its product ID
     * @param id Product ID (must be a positive integer)
     * @param price Product price (must be greater than 0)
     * @return true if the product's price is updated correctly, false in other case
     */
    public Product updateProductPrice(int id, double price) throws DataException{
        Product selectedProduct = this.readProduct(id);

        if (selectedProduct == null) throw new MissingItemException("Product with id " + id + "does not exist");
        try{
            selectedProduct.setPrice(price);
        }catch (IllegalArgumentException ex){
            throw new DataException("Unable to update price: " + ex.getMessage());
        }
		return selectedProduct;
    }

    /**
     * Updates a product's category specifying its product ID
     * @param id Product ID (must be a positive integer)
     * @param category Product Category
     * @return true if the product's category is updated correctly, false in other case
     */
    public BaseProduct updateProductCategory(int id, String category) throws DataException{
        BaseProduct selectedProduct;
        try { // stinky hack coming up
             selectedProduct = (BaseProduct) this.readProduct(id);
        } catch (ClassCastException ex) {
            throw new DataException("The product with id " + id + " is not a base product");
        }

        if (selectedProduct == null) throw new MissingItemException("Product with id " + id + "does not exist");
        try {
            selectedProduct.setCategory(category);
        }catch (IllegalArgumentException ex){
            throw new DataException("Unable to update category, " + category + " is not a valid category");
        }
		return selectedProduct;
    }

    /**
     * Deletes a product specifying its product ID from the array
     * @param id Product ID (must be a positive integer)
     * @return true if the product is deleted correctly, false in other case
     */
    public void deleteProduct(int id) throws MissingItemException{
        int selectedProductIndex = this.readProductIndex(id);

        if (selectedProductIndex == -1) throw new MissingItemException("Product with id " + id + " does not exist");
        // Remove product from array
        this.productAmount--;
        for (int i = selectedProductIndex; i < this.productAmount; i++) {
            this.inventory[i] = this.inventory[i + 1];
        }
    }

    /**
     * Returns an array of all products added. Ordered by first added product to last added product.
     * @return Array of products with length of total product amount in the catalogue. Null if the inventory is empty
     */
    public Product[] listProducts() {
        if (this.productAmount == 0) return null;
        Product[] arrayProducts = new Product[this.productAmount];
        for (int i = 0; i < this.productAmount; i++) {
            arrayProducts[i] = this.inventory[i];
        }
        return arrayProducts;
    }

    /**
     * Generates a unique id by finding the greatest id in the inventory and adding 1 to it
     * @return a product id, valid and unique
     */
	public int generateUniqueProductId() {
        //Searches for the greatest id in all the products
        int greatestId = 0;
        for (Product product: inventory){
            if (product != null && product.getId() > greatestId){
                greatestId = product.getId();
            }
        }

        // Returns the id that is immediately next to the greatest id
		return greatestId + 1;
	}
}
