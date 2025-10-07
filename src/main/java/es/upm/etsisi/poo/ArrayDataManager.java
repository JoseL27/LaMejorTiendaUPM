package es.upm.etsisi.poo;

/**
 * Manages Create Read Update Delete operations for Products using Array to store the products. Provides input
 * sanity checks for all operations.
 */
public class ArrayDataManager implements DataManager {

    public static final int MAX_CAPACITY = 200; // E1: no more than 200 products
    public static final int MAX_NAME_LENGTH = 100; // E1: product name contains no more than 100 characters

    private Product[] inventory; // List
    private int productAmount;

    /**
     * Creates a new ArrayDataManager with an empty inventory
     */
    public ArrayDataManager() {
        this.inventory = new Product[this.MAX_CAPACITY];
        this.productAmount = 0;
    }

    /**
     * Checks if given ID is valid (idToCheck >= 0)
     * @param idToCheck numeric id to check
     * @return true if valid, otherwise return false
     */
    public static boolean isValidId(int idToCheck) {
        // Change if 0 is not an allowed ID
        return idToCheck >= 0;
    }

    /**
     * Check if given name is valid (nameToCheck.length < 100)
     * @param nameToCheck Name string to check
     * @return true if valid, otherwise return false
     */
    private boolean isValidName(String nameToCheck) {
        return nameToCheck.length() < this.MAX_NAME_LENGTH;
    }

    /**
     * Check if given price is valid (priceToCheck > 0)
     * @param priceToCheck Price double to check
     * @return true if valid, otherwise return false
     */
    private boolean isValidPrice(double priceToCheck) {
        return priceToCheck > 0;
    }

    /**
     * Attempts to find the product with the same ID, returning its position in the array
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
     * @param id Product ID (must be a positive integer)
     * @param name Product name (length must be less than 100)
     * @param category Product Category
     * @param price Product price (must be greater than 0)
     * @return DataResult enum: SUCCESS, INVALID_ID, INVALID_NAME, INVALID_PRICE, INVALID_CATEGORY, INVENTORY_FULL, PRODUCT_ALREADY_EXISTS
     */
    public DataResult createProduct(int id, String name, Product.Category category, double price) {
        // Sanity checks: ID >= 0, name.length < 100, price > 0
        if (!isValidId(id)) return DataResult.INVALID_ID;
        if (!isValidName(name)) return DataResult.INVALID_NAME;
        if (!isValidPrice(price)) return DataResult.INVALID_PRICE;
        // if (category == null) return DataResult.INVALID_CATEGORY;

        // Check inventory full
        if (this.productAmount >= this.MAX_CAPACITY) return DataResult.INVENTORY_FULL;

        Product selectedProduct = this.readProduct(id);

        if (selectedProduct == null) {
            // Create product and add it to the array
            Product prodToAdd = new Product(id, name, category, price);
            this.inventory[this.productAmount] = prodToAdd;
            this.productAmount++;
            return DataResult.SUCCESS;
        } else {
            return DataResult.PRODUCT_ALREADY_EXISTS;
        }
    }

    /**
     * Updates a product's name specifying its product ID
     * @param id Product ID (must be a positive integer)
     * @param name Product name (length must be less than 100)
     * @return DataResult enum: SUCCESS, INVALID_ID, INVALID_NAME, PRODUCT_NOT_FOUND
     */
    public DataResult updateProductName(int id, String name) {
        // Sanity checks: ID >= 0, name.length < 100
        if (!isValidId(id)) return DataResult.INVALID_ID;
        if (!isValidName(name)) return DataResult.INVALID_NAME;

        Product selectedProduct = this.readProduct(id);

        if (selectedProduct != null) {
            // Update product's name
            selectedProduct.setName(name);
            return DataResult.SUCCESS;
        } else {
            return DataResult.PRODUCT_NOT_FOUND;
        }
    }

    /**
     * Updates a product's price specifying its product ID
     * @param id Product ID (must be a positive integer)
     * @param price Product price (must be greater than 0)
     * @return DataResult enum: SUCCESS, INVALID_ID, INVALID_PRICE, PRODUCT_NOT_FOUND
     */
    public DataResult updateProductPrice(int id, double price) {
        // Sanity checks: ID >= 0, price > 0
        if (!isValidId(id)) return DataResult.INVALID_ID;
        if (!isValidPrice(price)) return DataResult.INVALID_PRICE;

        Product selectedProduct = this.readProduct(id);

        if (selectedProduct != null) {
            // Update product's price
            selectedProduct.setPrice(price);
            return DataResult.SUCCESS;
        } else {
            return DataResult.PRODUCT_NOT_FOUND;
        }
    }

    /**
     * Updates a product's price specifying its product ID
     * @param id Product ID (must be a positive integer)
     * @param category Product Category
     * @return DataResult enum: SUCCESS, INVALID_ID, INVALID_PRICE, INVALID_CATEGORY
     */
    public DataResult updateProductCategory(int id, Product.Category category) {
        // Sanity checks: ID >= 0, category != null
        if (!isValidId(id)) return DataResult.INVALID_ID;
        // if (category == null) return DataResult.INVALID_CATEGORY;

        Product selectedProduct = this.readProduct(id);

        if (selectedProduct != null) {
            // Update product's category
            selectedProduct.setCategory(category);
            return DataResult.SUCCESS;
        } else {
            return DataResult.PRODUCT_NOT_FOUND;
        }
    }

    /**
     * Deletes a product specifying its product ID from the array
     * @param id Product ID (must be a positive integer)
     * @return DataResult enum: SUCCESS, INVALID_ID, PRODUCT_NOT_FOUND
     */
    public DataResult deleteProduct(int id) {
        // Sanity checks: ID >= 0
        if (!isValidId(id)) return DataResult.INVALID_ID;

        int selectedProductIndex = this.readProductIndex(id);

        if (selectedProductIndex != -1) {
            // Remove product from array
            this.productAmount--;
            for (int i = selectedProductIndex; i < this.productAmount; i++) {
                this.inventory[i] = this.inventory[i + 1];
            }
            return DataResult.SUCCESS;
        } else {
            return DataResult.PRODUCT_NOT_FOUND;
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
}
