package es.upm.etsisi.test;

import es.upm.etsisi.poo.Product;
import es.upm.etsisi.poo.ArrayDataManager;
import es.upm.etsisi.poo.DataManager.DataResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

public class ArrayDataManagerTest {
	
	// Success
	@Test
	void addBookProductTest() {
        ArrayDataManager adm = new ArrayDataManager();
		Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
		
		DataResult result = adm.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());
		assertEquals(result, DataResult.SUCCESS);
		assertEquals(prod, adm.readProduct(prod.getId()));
	}
	
	@Test
	void addShirtProductTest() {
        ArrayDataManager adm = new ArrayDataManager();
		Product prod = new Product(1, "Camiseta talla:M UPM", Product.Category.CLOTHES, 15);
		
		DataResult result = adm.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());
		assertEquals(result, DataResult.SUCCESS);
		assertEquals(prod, adm.readProduct(prod.getId()));
	}

	@Test 
	void productListTest() {
        ArrayDataManager adm = new ArrayDataManager();

		Product.Category[] categoryValues = Product.Category.values();
		Product[] testProducts = new Product[50];
		for (int i = 0; i < testProducts.length; i++) {
			Product.Category category = categoryValues[i % categoryValues.length];
			Product prod = new Product(i, String.format("Producto(%d)", i), category, (i+1)*10);
			testProducts[i] = prod;
			DataResult result = adm.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());
			assertEquals(result, DataResult.SUCCESS);
		}

		Product[] listProducts = adm.listProducts();
		assertEquals(listProducts.length, testProducts.length);

		// Provides better logs instead of calling assertEquals(listProducts, testProducts) directly
		for (int i = 0; i < listProducts.length; i++) {
			assertEquals(listProducts[i], testProducts[i]); 
		}
	}

	@Test
	void updateProductNameTest() {
        ArrayDataManager adm = new ArrayDataManager();
		Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
		
		adm.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());

		prod.setName("Libro POO V2"); // Update
        DataResult result = adm.updateProductName(prod.getId(), prod.getName());
		
		assertEquals(result, DataResult.SUCCESS);
		assertEquals(prod, adm.readProduct(prod.getId()));
	}

	@Test
	void updateProductPriceTest() {
        ArrayDataManager adm = new ArrayDataManager();
		Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
		
		adm.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());

		prod.setPrice(30.0); // Update
        DataResult result = adm.updateProductPrice(prod.getId(), prod.getPrice());
		
		assertEquals(result, DataResult.SUCCESS);
		assertEquals(prod, adm.readProduct(prod.getId()));
	}

	@Test
	void removeProductTest() {
        ArrayDataManager adm = new ArrayDataManager();

		int productId = 1;
		adm.createProduct(productId, "Camiseta talla:M UPM", Product.Category.CLOTHES, 15);

		DataResult result = adm.deleteProduct(productId);
		assertEquals(result, DataResult.SUCCESS);
		assertNull(adm.readProduct(productId));
	}

	@Test
	void readMissingProductTest() {
        ArrayDataManager adm = new ArrayDataManager();
		assertNull(adm.readProduct(1));
	}

	// Failures
	@Test
	void addAllreadyExistsTest() {
        ArrayDataManager adm = new ArrayDataManager();
		int productId = 1;
        adm.createProduct(productId, "Libro POO", Product.Category.BOOK, 25);
        DataResult result = adm.createProduct(productId, "Duplicate Libro POO", Product.Category.BOOK, 25);

		assertEquals(result, DataResult.PRODUCT_ALREADY_EXISTS);
	}

	@Test
	void addInvalidIdTest() {
        ArrayDataManager adm = new ArrayDataManager();
		int productId = -1;
        DataResult result = adm.createProduct(productId, "Libro POO", Product.Category.BOOK, 25);
		
		assertEquals(result, DataResult.INVALID_ID);
		assertNull(adm.readProduct(productId)); // Wasn't added
	}

	@Test
	void addInvalidNameLengthTest() {
        ArrayDataManager adm = new ArrayDataManager();
		int productId = 1;
        DataResult result = adm.createProduct(productId, "Libro POOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", Product.Category.BOOK, 25);
		
		assertEquals(result, DataResult.INVALID_NAME);
		assertNull(adm.readProduct(productId)); // Wasn't added
	}

	@Test
	void addInvalidNullCategoryTest() {
        ArrayDataManager adm = new ArrayDataManager();
		int productId = 1;
        DataResult result = adm.createProduct(productId, "Libro POOOOOOOOOOOO", null, 25);
		
		assertEquals(result, DataResult.INVALID_CATEGORY);
		assertNull(adm.readProduct(productId)); // Wasn't added
	}

	@Test
	void addInvalidNegativePriceTest() {
        ArrayDataManager adm = new ArrayDataManager();
		
		int productId = 1;
        DataResult result = adm.createProduct(productId, "Libro POO", null, -2.5);
		
		assertEquals(result, DataResult.INVALID_PRICE);
		assertNull(adm.readProduct(productId)); // Wasn't added
	}

	@Test
	void updateInvalidNameLengthTest() {
        ArrayDataManager adm = new ArrayDataManager();
		Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
		
		adm.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());

		String newName = "Libro POO V2 OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO";		
        DataResult result = adm.updateProductName(prod.getId(), newName);
		
		assertEquals(result, DataResult.INVALID_NAME);
		assertEquals(prod, adm.readProduct(prod.getId())); // Name didn't change
	}

	@Test
	void updateInvalidPriceTest() {
        ArrayDataManager adm = new ArrayDataManager();
		Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
		
		adm.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());

		int newPrice = -1;
        DataResult result = adm.updateProductPrice(prod.getId(), newPrice);
		
		assertEquals(result, DataResult.INVALID_PRICE);
		assertEquals(prod, adm.readProduct(prod.getId())); // Price didn't change
	}

	@Test
	void addMoreThanMaxTest() {
        ArrayDataManager adm = new ArrayDataManager();
		
        for (int i = 0; i < ArrayDataManager.MAX_CAPACITY; i++) {
            adm.createProduct(i, String.format("Product(%d)", i), Product.Category.BOOK, (i+1)*10);
        }

		int productId = ArrayDataManager.MAX_CAPACITY+1;
		DataResult result = adm.createProduct(productId, "Libro POO", Product.Category.BOOK, 25);

		assertEquals(result, DataResult.INVENTORY_FULL);
		assertNull(adm.readProduct(productId));
	}
}
