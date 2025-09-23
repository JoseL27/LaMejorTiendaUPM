package es.upm.etsisi.poo;

enum DataResult {
};

public interface DataManager {
	
	public DataResult createProduct(int id, String name, Product.Category category, double price);
	// public DataResult readProduct();
	
	public DataResult updateProductName(int id, String name);
	public DataResult updateProductPrice(int id, double price);
	public DataResult updateProductCategory(int id, Product.Category category);
	
	public DataResult deleteProduct(int id);

	public DataResult listProducts(Product[] outProductsList);
}
