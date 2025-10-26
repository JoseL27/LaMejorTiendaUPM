package es.upm.etsisi.test;

import java.util.Locale;

import es.upm.etsisi.poo.Product;
import es.upm.etsisi.poo.Inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import static org.junit.jupiter.api.Assertions.*;

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
        Inventory inventory = new Inventory();
		Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
		
		boolean result = inventory.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());
		assertTrue(result);
		assertEquals(prod, inventory.readProduct(prod.getId()));
	}
	
	@Test
	void addShirtProductTest() {
        Inventory inventory = new Inventory();
		Product prod = new Product(1, "Camiseta talla:M UPM", Product.Category.CLOTHES, 15);
		
		boolean result = inventory.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());
		assertTrue(result);
		assertEquals(prod, inventory.readProduct(prod.getId()));
	}

	@Test 
	void productListTest() {
        Inventory inventory = new Inventory();

		Product.Category[] categoryValues = Product.Category.values();
		Product[] testProducts = new Product[50];
		for (int i = 0; i < testProducts.length; i++) {
			Product.Category category = categoryValues[i % categoryValues.length];
			Product prod = new Product(i, String.format("Producto(%d)", i), category, (i+1)*10);
			testProducts[i] = prod;
			boolean result = inventory.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());
			assertTrue(result);
		}

		Product[] listProducts = inventory.listProducts();
		assertEquals(listProducts.length, testProducts.length);

		// Provides better logs instead of calling assertEquals(listProducts, testProducts) directly
		for (int i = 0; i < listProducts.length; i++) {
			assertEquals(listProducts[i], testProducts[i]); 
		}
	}

	@Test
	void updateProductNameTest() {
        Inventory inventory = new Inventory();
		Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
		
		inventory.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());

		prod.setName("Libro POO V2"); // Update
        boolean result = inventory.updateProductName(prod.getId(), prod.getName());
		
		assertTrue(result);
		assertEquals(prod, inventory.readProduct(prod.getId()));
	}

	@Test
	void updateProductPriceTest() {
        Inventory inventory = new Inventory();
		Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
		
		inventory.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());

		prod.setPrice(30.0); // Update
        boolean result = inventory.updateProductPrice(prod.getId(), prod.getPrice());
		
		assertTrue(result);
		assertEquals(prod, inventory.readProduct(prod.getId()));
	}

	@Test
	void removeProductTest() {
        Inventory inventory = new Inventory();

		int productId = 1;
		inventory.createProduct(productId, "Camiseta talla:M UPM", Product.Category.CLOTHES, 15);

		boolean result = inventory.deleteProduct(productId);
		assertTrue(result);
		assertNull(inventory.readProduct(productId));
	}

	@Test
	void readMissingProductTest() {
        Inventory inventory = new Inventory();
		assertNull(inventory.readProduct(1));
	}

	// Failures
	@Test
	void addAllreadyExistsTest() {
        Inventory inventory = new Inventory();
		int productId = 1;
        inventory.createProduct(productId, "Libro POO", Product.Category.BOOK, 25);
        boolean result = inventory.createProduct(productId, "Duplicate Libro POO", Product.Category.BOOK, 25);

		assertFalse(result);
	}

	@Test
	void addInvalidIdTest() {
        Inventory inventory = new Inventory();
		int productId = -1;
        boolean result = inventory.createProduct(productId, "Libro POO", Product.Category.BOOK, 25);
		
		assertFalse(result);
		assertNull(inventory.readProduct(productId)); // Wasn't added
	}

	@Test
	void addInvalidNameLengthTest() {
        Inventory inventory = new Inventory();
		int productId = 1;
        boolean result = inventory.createProduct(productId, "Libro POOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", Product.Category.BOOK, 25);
		
		assertFalse(result);
		assertNull(inventory.readProduct(productId)); // Wasn't added
	}

	@Test
	void addInvalidNegativePriceTest() {
        Inventory inventory = new Inventory();
		
		int productId = 1;
        boolean result = inventory.createProduct(productId, "Libro POO", null, -2.5);
		
		assertFalse(result);
		assertNull(inventory.readProduct(productId)); // Wasn't added
	}

	@Test
	void updateInvalidNameLengthTest() {
        Inventory inventory = new Inventory();
		Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
		
		inventory.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());

		String newName = "Libro POO V2 OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO";		
        boolean result = inventory.updateProductName(prod.getId(), newName);
		
		assertFalse(result);
		assertEquals(prod, inventory.readProduct(prod.getId())); // Name didn't change
	}

	@Test
	void updateInvalidPriceTest() {
        Inventory inventory = new Inventory();
		Product prod = new Product(1, "Libro POO", Product.Category.BOOK, 25);
		
		inventory.createProduct(prod.getId(), prod.getName(), prod.getCategory(), prod.getPrice());

		int newPrice = -1;
        boolean result = inventory.updateProductPrice(prod.getId(), newPrice);
		
		assertFalse(result);
		assertEquals(prod, inventory.readProduct(prod.getId())); // Price didn't change
	}

	@Test
	void addMoreThanMaxTest() {
        Inventory inventory = new Inventory();
		
        for (int i = 0; i < Inventory.MAX_CAPACITY; i++) {
            inventory.createProduct(i, String.format("Product(%d)", i), Product.Category.BOOK, (i+1)*10);
        }

		int productId = Inventory.MAX_CAPACITY+1;
		boolean result = inventory.createProduct(productId, "Libro POO", Product.Category.BOOK, 25);

		assertFalse(result);
		assertNull(inventory.readProduct(productId));
	}
}
