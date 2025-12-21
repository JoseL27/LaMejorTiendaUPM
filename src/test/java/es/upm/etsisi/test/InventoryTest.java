package es.upm.etsisi.test;

import java.util.Locale;

import es.upm.etsisi.poo.Product;
import es.upm.etsisi.poo.BaseProduct;
import es.upm.etsisi.poo.Inventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import es.upm.etsisi.poo.exceptions.MissingItemException;
import es.upm.etsisi.poo.exceptions.DuplicateItemException;
import es.upm.etsisi.poo.exceptions.DataException;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;

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
    
    @BeforeEach
        void createNewUserManager() {
        try {
            Field f = Inventory.class.getDeclaredField("instance");
            f.setAccessible(true);
            f.set(null, null);
        } catch (Exception e) { }
    }
	
	// Success
	@Test
        void addBaseProducts() {
        final BaseProduct.Category[] categories = BaseProduct.Category.values();
        for (int i = 0; i < categories.length; i++) {
            final int id = i+1;
            final String c = categories[i].toString();
            final double price = (i+1)*5;
            assertDoesNotThrow(() -> {
                                   Inventory.getInstance().createBaseProduct(id, "Base", c, price, 0, false);
                               });
        }
	}
    
    
	@Test 
        void productListTest() {
        final Inventory inventory = Inventory.getInstance();
        
		BaseProduct.Category[] categoryValues = BaseProduct.Category.values();
		BaseProduct[] testProducts = new BaseProduct[50];
        
		for (int i = 0; i < testProducts.length; i++) {
            String category = categoryValues[i % categoryValues.length].toString();
            
			final BaseProduct prod = new BaseProduct(i, String.format("Producto(%d)", i), (i+1)*10, category, 0, false);
			testProducts[i] = prod;
            assertDoesNotThrow(() -> {
                                   inventory.createBaseProduct(prod.getId(), prod.getName(), 
                                                               prod.getCategory().toString(), prod.getPrice(), 0 , false);
                               });
		}
        
		Product[] listProducts = inventory.listProducts();
		assertEquals(listProducts.length, testProducts.length);
        
		for (int i = 0; i < listProducts.length; i++) {
			assertEquals(listProducts[i].toString(), testProducts[i].toString()); 
		}
	}
    
    
	@Test
        void updateProductNameTest() {
        final BaseProduct prod = new BaseProduct(1, "Libro POO", 25, "BOOK", 0, false);
		
        assertDoesNotThrow(() -> {
                               Inventory inventory = Inventory.getInstance();
                               
                               inventory.createBaseProduct(prod.getId(), 
                                                           prod.getName(), 
                                                           
                                                           prod.getCategory().toString(), 
                                                           prod.getPrice(), 
                                                           0, false);
                               prod.setName("Libro POO V2");
                               
                               inventory.updateProductName(prod.getId(), prod.getName());
                               
                               assertEquals(prod.toString(), inventory.readProduct(prod.getId()).toString());
                           });
	}
    
    
	@Test
        void updateProductPriceTest() {
        final BaseProduct prod = new BaseProduct(1, "Libro POO", 25, "BOOK", 0, false);
        assertDoesNotThrow(() -> {
                               Inventory inventory = Inventory.getInstance();
                               
                               inventory.createBaseProduct(prod.getId(), 
                                                           prod.getName(), 
                                                           
                                                           prod.getCategory().toString(), 
                                                           prod.getPrice(), 
                                                           0, false);
                               prod.setPrice(30);
                               
                               inventory.updateProductPrice(prod.getId(), prod.getPrice());
                               
                               assertEquals(prod.toString(), inventory.readProduct(prod.getId()).toString());
                           });
	}
    
    
	@Test
        void removeProductTest() {
		final int productId = 1;
        assertDoesNotThrow(() -> {
                               Inventory.getInstance().createBaseProduct(productId, "Camiseta talla:M UPM", "CLOTHES", 15, 0, false);
                               Inventory.getInstance().deleteProduct(productId);
                           });
        
		assertNull(Inventory.getInstance().readProduct(productId));
	}
    
	@Test
        void readMissingProductTest() {
		assertNull(Inventory.getInstance().readProduct(1));
	}
    
	// Failures
	@Test
        void addAllreadyExistsTest() {
		final int productId = 1;
        assertDoesNotThrow(() -> {
                               Inventory.getInstance().createBaseProduct(productId, "Libro POO", "BOOK", 25, 0, false);
                           });
        assertThrows(DuplicateItemException.class, () -> {
                         Inventory.getInstance().createBaseProduct(productId, "Duplicate Libro POO", "BOOK", 25, 0, false);
                     });
	}
    
	@Test
        void addInvalidIdTest() {
		int productId = -1;
        assertThrows(DataException.class, () -> {
                         Inventory.getInstance().createBaseProduct(productId, "Libro POO", "BOOK", 25, 0, false);
                     });
		assertNull(Inventory.getInstance().readProduct(productId));
	}
    
    
	@Test
        void addInvalidNameLengthTest() {
        int productId = 1;
        String longName = "Libro POOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO";
        assertThrows(DataException.class, () -> {
                         Inventory.getInstance().createBaseProduct(productId, longName, "BOOK", 25, 0, false);
                     });
		assertNull(Inventory.getInstance().readProduct(productId)); // Wasn't added
	}
    
    
	@Test
        void addInvalidNegativePriceTest() {
        int productId = 1;
        assertThrows(DataException.class, () -> {
                         Inventory.getInstance().createBaseProduct(productId, "Libro POO", "BOOK", -2.5, 0, false);
                     });
		assertNull(Inventory.getInstance().readProduct(productId)); // Wasn't added
	}
    
	@Test
        void updateInvalidNameLengthTest() {
        final Inventory inventory = Inventory.getInstance();
		final BaseProduct prod = new BaseProduct(1, "Libro POO",  25, "BOOK", 0, false);
        assertDoesNotThrow(() -> {
                               inventory.createBaseProduct(prod.getId(), 
                                                           prod.getName(), 
                                                           prod.getCategory().toString(), 
                                                           prod.getPrice(), 
                                                           0, false);
                           });
        
		final String newName = "Libro POO V2 OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO";		
        assertThrows(DataException.class, () -> {
                         inventory.updateProductName(prod.getId(), newName);
                     });
		assertEquals(prod.toString(), inventory.readProduct(prod.getId()).toString()); // Name didn't change
	}
    
	@Test
        void updateInvalidPriceTest() {
        final Inventory inventory = Inventory.getInstance();
		final BaseProduct prod = new BaseProduct(1, "Libro POO",  25, "BOOK", 0, false);
        assertDoesNotThrow(() -> {
                               inventory.createBaseProduct(prod.getId(), 
                                                           prod.getName(), 
                                                           prod.getCategory().toString(), 
                                                           prod.getPrice(), 
                                                           0, false);
                           });
		final double newPrice = -1.0;
        assertThrows(DataException.class, () -> {
                         inventory.updateProductPrice(prod.getId(), newPrice);
                     });
		assertEquals(prod.toString(), inventory.readProduct(prod.getId()).toString()); // Price didn't change
	}
    
	@Test
        void addMoreThanMaxTest() {
        final Inventory inventory = Inventory.getInstance();
		
        for (int i = 0; i < Inventory.MAX_PRODUCTS; i++) {
            final int num = i;
            assertDoesNotThrow(() -> {
                                   inventory.createBaseProduct(num, String.format("Product(%d)", num), "BOOK", (num+1)*10, 0, false);
                               });
        }
        
        assertDoesNotThrow(() -> {
                               assertNull(inventory.createBaseProduct(696969, "Libro POO", "BOOK", 25, 0, false));
                           });
	}
}