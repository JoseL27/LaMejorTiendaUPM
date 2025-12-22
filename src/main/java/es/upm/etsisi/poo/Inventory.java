package es.upm.etsisi.poo;

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
     * Checks if given ID is valid (idToCheck >= 0)
     *
     * @param idToCheck numeric id to check
     * @return true if valid, otherwise return false
     */
    public static boolean isValidId(int idToCheck) {
        // Change if 0 is not an allowed ID
        return idToCheck >= 0;
    }

    /**
     * Check if given name is valid (nameToCheck.length < 100)
     *
     * @param nameToCheck Name string to check
     * @return true if valid, otherwise return false
     */
    public static boolean isValidName(String nameToCheck) {
        return nameToCheck.length() < Product.PRODUCT_MAX_NAME_LENGTH;
    }

    /**
     * Check if given price is valid (priceToCheck > 0)
     *
     * @param priceToCheck Price double to check
     * @return true if valid, otherwise return false
     */
    private boolean isValidPrice(double priceToCheck) {
        return priceToCheck > 0;
    }

    /**
     * Attempts to find the product with the same ID, returning its position in the array
     *
     * @param id Product ID
     * @return Product object's index in the array, -1 if not found or ID is invalid (ID < 0)
     */
    public int readProductIndex(int id) {
        if (!isValidId(id)) return -1;
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
     *
     * @param id Product ID
     * @return Product with the specified id, null if not found or ID is invalid (ID < 0)
     */
    public Product readProduct(int id) {
        if (!isValidId(id)) return null;
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
     *
     * @param id       Product ID (must be a positive integer)
     * @param name     Product name (length must be less than 100)
     * @param category Product Category
     * @param price    Product price (must be greater than 0)
     * @return true if the product is created correctly, false in other case
     */
    public BaseProduct createBaseProduct(int id, String name, BaseProduct.Category category, double price, int maxPers, boolean personalized) {
        if (!isValidId(id) || !isValidName(name) || !isValidPrice(price)) return null;

        // Check inventory full
        if (this.productAmount >= MAX_PRODUCTS) return null;

        Product selectedProduct = this.readProduct(id);

        if (selectedProduct == null) {
            BaseProduct prodToAdd = new BaseProduct(id, name, price, category, maxPers, personalized);
            this.inventory[this.productAmount] = prodToAdd;
            this.productAmount++;
            return prodToAdd;
        } else {
            return null;
        }
    }

    /**
     * Tries to create a new timed product with its attributes set to the values of the parameters
     *
     * @return The product that was created, or null if the creation failed
     */
    public TimedProduct createTimedProduct(int id, String name, double price, int people, TimedProduct.TimedType type, LocalDateTime expirationDate) {
        if (!isValidId(id) || !isValidName(name) || !isValidPrice(price)) return null;

        // Check inventory full
        if (this.productAmount >= MAX_PRODUCTS) return null;

        TimedProduct prodToAdd = null;
        //Check if already exists
        Product selectedProduct = this.readProduct(id);

        if (selectedProduct == null) {
            prodToAdd = new TimedProduct(id, name, price, people, type, expirationDate);
            this.inventory[this.productAmount] = prodToAdd;
            this.productAmount++;
        }
        return prodToAdd;
    }

    /**
     * Updates a product's name specifying its product ID
     *
     * @param id   Product ID (must be a positive integer)
     * @param name Product name (length must be less than 100)
     * @return true if the product's name is updated correctly, false in other case
     */
    public Product updateProductName(int id, String name) {
        // Sanity checks: ID >= 0, name.length < 100
        if (!isValidId(id) || !isValidName(name)) return null;

        Product selectedProduct = this.readProduct(id);

        if (selectedProduct != null) {
            selectedProduct.setName(name);
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
    public Product updateProductPrice(int id, double price) {
        // Sanity checks: ID >= 0, price > 0
        if (!isValidId(id) || !isValidPrice(price)) return null;

        Product selectedProduct = this.readProduct(id);

        if (selectedProduct != null) {
            selectedProduct.setPrice(price);
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
    public BaseProduct updateProductCategory(int id, BaseProduct.Category category) {
        // Sanity checks: ID >= 0, category != null
        if (!isValidId(id)) return null;
        // if (category == null) return DataResult.INVALID_CATEGORY;

        BaseProduct selectedProduct;
        try { // stinky hack coming up
            selectedProduct = (BaseProduct) this.readProduct(id);
        } catch (Exception e) {
            selectedProduct = null;
        }

        if (selectedProduct != null) {
            // Update product's category
            selectedProduct.setCategory(category);
        }
        return selectedProduct;
    }

    /**
     * Deletes a product specifying its product ID from the array
     *
     * @param id Product ID (must be a positive integer)
     * @return true if the product is deleted correctly, false in other case
     */
    public boolean deleteProduct(int id) {
        // Sanity checks: ID >= 0
        if (!isValidId(id)) return false;

        int selectedProductIndex = this.readProductIndex(id);

        if (selectedProductIndex != -1) {
            // Remove product from array
            this.productAmount--;
            for (int i = selectedProductIndex; i < this.productAmount; i++) {
                this.inventory[i] = this.inventory[i + 1];
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns an array of all products added. Ordered by first added product to last added product.
     *
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
     *
     * @return a product id, valid and unique
     */
    public int generateUniqueProductId() {
        //Searches for the greatest id in all the products
        int greatestId = 0;
        for (Product product : inventory) {
            if (product != null && product.getId() > greatestId) {
                greatestId = product.getId();
            }
        }

        // Returns the id that is immediately next to the greatest id
        return greatestId + 1;
    }

    public boolean isTimedProduct(Product product) {
        return product instanceof TimedProduct;
    }

}
