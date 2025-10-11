package es.upm.etsisi.test;

import java.util.Locale;

import es.upm.etsisi.poo.Product;
import es.upm.etsisi.poo.Inventory;
import es.upm.etsisi.poo.Inventory.DataResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

public class InventoryTest {

	// DICTATOR LOCALE 
	@BeforeAll
	static void setEnUSLocale() {
		Locale.setDefault(new Locale("en", "US"));
	}
	
	@AfterAll
	static void unsetEnUSLocale() {
		Locale.setDefault(Locale.getDefault());
	}
	
	// Success
	@Test
	void addBookProductTest() {
        Inventory adm = new Inventory();
		Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
		
		DataResult result = adm.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());
		assertEquals(result, Inventory.DataResult.SUCCESS);
		assertEquals(prod, adm.readProduct(prod.getId()));
	}
	
	@Test
	void addShirtProductTest() {
        Inventory adm = new Inventory();
		Product prod = new Product(1, "Camiseta talla:M UPM", Product.Category.CLOTHES, 15);
		
		DataResult result = adm.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());
		assertEquals(result, Inventory.DataResult.SUCCESS);
		assertEquals(prod, adm.readProduct(prod.getId()));
	}

	@Test 
	void productListTest() {
        Inventory adm = new Inventory();

		Product.Category[] categoryValues = Product.Category.values();
		Product[] testProducts = new Product[50];
		for (int i = 0; i < testProducts.length; i++) {
			Product.Category category = categoryValues[i % categoryValues.length];
			Product prod = new Product(i, String.format("Producto(%d)", i), category, (i+1)*10);
			testProducts[i] = prod;
			DataResult result = adm.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());
			assertEquals(result, Inventory.DataResult.SUCCESS);
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
        Inventory adm = new Inventory();
		Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
		
		adm.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());

		prod.setName("Libro POO V2"); // Update
        DataResult result = adm.updateProductName(prod.getId(), prod.getName());
		
		assertEquals(result, Inventory.DataResult.SUCCESS);
		assertEquals(prod, adm.readProduct(prod.getId()));
	}

	@Test
	void updateProductPriceTest() {
        Inventory adm = new Inventory();
		Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
		
		adm.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());

		prod.setPrice(30.0); // Update
        DataResult result = adm.updateProductPrice(prod.getId(), prod.getPrice());
		
		assertEquals(result, Inventory.DataResult.SUCCESS);
		assertEquals(prod, adm.readProduct(prod.getId()));
	}

	@Test
	void removeProductTest() {
        Inventory adm = new Inventory();

		int productId = 1;
		adm.createProduct(productId, "Camiseta talla:M UPM", Product.Category.CLOTHES, 15);

		DataResult result = adm.deleteProduct(productId);
		assertEquals(result, Inventory.DataResult.SUCCESS);
		assertNull(adm.readProduct(productId));
	}

	@Test
	void readMissingProductTest() {
        Inventory adm = new Inventory();
		assertNull(adm.readProduct(1));
	}

	// Failures
	@Test
	void addAllreadyExistsTest() {
        Inventory adm = new Inventory();
		int productId = 1;
        adm.createProduct(productId, "Libro POO", Product.Category.BOOK, 25);
        DataResult result = adm.createProduct(productId, "Duplicate Libro POO", Product.Category.BOOK, 25);

		assertEquals(result, Inventory.DataResult.PRODUCT_ALREADY_EXISTS);
	}

	@Test
	void addInvalidIdTest() {
        Inventory adm = new Inventory();
		int productId = -1;
        DataResult result = adm.createProduct(productId, "Libro POO", Product.Category.BOOK, 25);
		
		assertEquals(result, Inventory.DataResult.INVALID_ID);
		assertNull(adm.readProduct(productId)); // Wasn't added
	}

	@Test
	void addInvalidNameLengthTest() {
        Inventory adm = new Inventory();
		int productId = 1;
        DataResult result = adm.createProduct(productId, "Libro POOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", Product.Category.BOOK, 25);
		
		assertEquals(result, Inventory.DataResult.INVALID_NAME);
		assertNull(adm.readProduct(productId)); // Wasn't added
	}

	@Test
	void addInvalidNegativePriceTest() {
        Inventory adm = new Inventory();
		
		int productId = 1;
        DataResult result = adm.createProduct(productId, "Libro POO", null, -2.5);
		
		assertEquals(result, Inventory.DataResult.INVALID_PRICE);
		assertNull(adm.readProduct(productId)); // Wasn't added
	}

	@Test
	void updateInvalidNameLengthTest() {
        Inventory adm = new Inventory();
		Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
		
		adm.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());

		String newName = "Libro POO V2 OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO";		
        DataResult result = adm.updateProductName(prod.getId(), newName);
		
		assertEquals(result, Inventory.DataResult.INVALID_NAME);
		assertEquals(prod, adm.readProduct(prod.getId())); // Name didn't change
	}

	@Test
	void updateInvalidPriceTest() {
        Inventory adm = new Inventory();
		Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
		
		adm.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());

		int newPrice = -1;
        DataResult result = adm.updateProductPrice(prod.getId(), newPrice);
		
		assertEquals(result, Inventory.DataResult.INVALID_PRICE);
		assertEquals(prod, adm.readProduct(prod.getId())); // Price didn't change
	}

	@Test
	void addMoreThanMaxTest() {
        Inventory adm = new Inventory();
		
        for (int i = 0; i < Inventory.MAX_CAPACITY; i++) {
            adm.createProduct(i, String.format("Product(%d)", i), Product.Category.BOOK, (i+1)*10);
        }

		int productId = Inventory.MAX_CAPACITY+1;
		DataResult result = adm.createProduct(productId, "Libro POO", Product.Category.BOOK, 25);

		assertEquals(result, Inventory.DataResult.INVENTORY_FULL);
		assertNull(adm.readProduct(productId));
	}
}
