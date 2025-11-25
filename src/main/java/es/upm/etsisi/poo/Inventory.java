package es.upm.etsisi.poo;

import java.time.LocalDateTime;

/**
 * Manages Create Read Update Delete operations for Products using Array to store the products. Provides input
 * sanity checks for all operations.
 */
public class Inventory {

    public static final int MAX_PRODUCTS = 200; // E1: no more than 200 products

	private static Inventory instance = new Inventory();

    private Product[] inventory; // List
    private int productAmount;
	private int nextId;

	public static Inventory getInstance() {
		if (Inventory.instance == null) {
			Inventory.instance = new Inventory();
		}
		return instance;
	}

    /**
     * Creates a new Inventory with an empty inventory
     */
    private Inventory() {
        this.inventory = new Product[MAX_PRODUCTS];
        this.productAmount = 0;
		this.nextId = 0;
    }

    /**
     * Attempts to find the product with the same ID
     * @param id Product ID
     * @return Product with the specified id, null if not found or ID is invalid (ID < 0)
     */
    public Product getProduct(int id) throws Exception {
        assert Product.isValidId(id);

		int index = this.readProductIndex(id);

		if (index == -1) {
			throw new Exception(String.format("could not find product with id '%d'", id));
		}

		Product product = this.inventory[index];
		return product;
	}

    /**
     * Creates a product and adds it to the array
     * @param id Product ID (must be a positive integer)
     * @param name Product name (length must be less than 100)
     * @param category Product Category
     * @param price Product price (must be greater than 0)
     * @return true if the product is created correctly, false in other case
     */
    public BaseProduct createBaseProduct(int id, String name, BaseProduct.Category category, 
										 double price, int maxPers, boolean personalized) throws Exception {
        assert Product.isValidId(id) && Product.isValidName(name) && Product.isValidPrice(price);

		BaseProduct prodToAdd = new BaseProduct(id, name, price, category, maxPers, personalized);
		appendProduct(prodToAdd);
		return prodToAdd;
    }

    /**
     * Tries to create a new timed product with its attributes set to the values of the parameters
     * @return The product that was created, or null if the creation failed
     */
    public Product createTimedProduct(int id, String name, double price, int people, 
									  TimedProduct.TimedType type, LocalDateTime expirationDate) throws Exception {
        assert Product.isValidId(id) && Product.isValidName(name) && Product.isValidPrice(price);

        TimedProduct prodToAdd = new TimedProduct(id, name, price, people, type, expirationDate);
		appendProduct(prodToAdd);
		return prodToAdd;
    }

	public Product appendProduct(Product product) throws Exception {
        if (this.containsProduct(product.getId())) {
			throw new Exception(String.format("unable to add, product with id '%d' allready exists", product.getId()));
        }

        if (this.productAmount >= MAX_PRODUCTS) {
			throw new Exception("unable to add, too many products");
		}

		if (product.getId() > this.nextId) {
			this.nextId = product.getId() + 1;
		}
		
		this.inventory[this.productAmount] = product;
		this.productAmount++;
        return product;
	}

    /**
     * Updates a product's name specifying its product ID
     * @param id Product ID (must be a positive integer)
     * @param name Product name (length must be less than 100)
     * @return true if the product's name is updated correctly, false in other case
     */
    public Product updateProductName(int id, String name) throws Exception {
        assert Product.isValidId(id) && Product.isValidName(name);

        Product selectedProduct = this.getProduct(id);
		selectedProduct.setName(name);
		return selectedProduct;
    }

    /**
     * Updates a product's price specifying its product ID
     * @param id Product ID (must be a positive integer)
     * @param price Product price (must be greater than 0)
     * @return true if the product's price is updated correctly, false in other case
     */
    public Product updateProductPrice(int id, double price) throws Exception {
        assert Product.isValidId(id) && Product.isValidPrice(price);

        Product selectedProduct = this.getProduct(id);

		selectedProduct.setPrice(price);
		return selectedProduct;
    }

    /**
     * Updates a product's category specifying its product ID
     * @param id Product ID (must be a positive integer)
     * @param category Product Category
     * @return true if the product's category is updated correctly, false in other case
     */
    public BaseProduct updateProductCategory(int id, BaseProduct.Category category) throws Exception {
        assert Product.isValidId(id);
		
        BaseProduct selectedProduct = (BaseProduct)this.getProduct(id);
		
		selectedProduct.setCategory(category);
		return selectedProduct;
    }

    /**
     * Deletes a product specifying its product ID from the array
     * @param id Product ID (must be a positive integer)
     * @return true if the product is deleted correctly, false in other case
     */
    public Product deleteProduct(int id) throws Exception {
        assert Product.isValidId(id);
		
        int index = this.readProductIndex(id);

        if (index == -1) {
			throw new Exception(String.format("product with id '%d' not found", id));
		}

		Product deleted = this.inventory[index];
		
		this.productAmount--;
		for (int i = index; i < this.productAmount; i++) {
			this.inventory[i] = this.inventory[i + 1];
		}

		return deleted;
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
     * Attempts to find the product with the same ID, returning its position in the array
     * @param id Product ID
     * @return Product object's index in the array, -1 if not found or ID is invalid (ID < 0)
     */
    private int readProductIndex(int id) {
        assert Product.isValidId(id);
		
        int result = -1;
        int i = 0;
        while (result == -1 && i < this.productAmount) {
            if (this.inventory[i].getId() == id)
                result = i;
            i++;
        }
        return result;
    }

	private boolean containsProduct(int id) {
		return this.readProductIndex(id) != -1;
	}

	public int generateUniqueProductId() {
		return this.nextId++;
	}
}
