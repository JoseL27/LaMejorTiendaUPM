package es.upm.etsisi.poo;

public interface DataManager {
	enum DataResult {
		SUCCESS,
		INVALID_NAME,
		INVALID_ID,
		PRODUCT_NOT_FOUND,
		PRODUCT_ALREADY_EXISTS,
		INVALID_PRICE,
		INVENTORY_FULL
	};

	public DataResult createProduct(int id, String name, Product.Category category, double price);
	// public DataResult readProduct();
	
	public DataResult updateProductName(int id, String name);
	public DataResult updateProductPrice(int id, double price);
	public DataResult updateProductCategory(int id, Product.Category category);
	
	public DataResult deleteProduct(int id);

	public Product[] listProducts();
}
