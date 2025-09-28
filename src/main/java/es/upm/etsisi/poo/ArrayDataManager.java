package es.upm.etsisi.poo;

public class ArrayDataManager implements DataManager {

	public ArrayDataManager() {
	}
	
	public DataResult createProduct(int id, String name, Product.Category category, double price) {
		return null;
	}
	
	// public DataResult readProduct() {
	// 	return null;
	// }
	
	public DataResult updateProductName(int id, String name) {
		return null;
	}
	
	public DataResult updateProductPrice(int id, double price) {
		return null;
	}
	
	public DataResult updateProductCategory(int id, Product.Category category) {
		return null;
	}
	
	public DataResult deleteProduct(int id) {
		return null;
	}

	public Product[] listProducts() {
		return new Product[0];
	}
}
