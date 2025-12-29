package es.upm.etsisi.test;

import java.util.Locale;

import es.upm.etsisi.poo.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import es.upm.etsisi.poo.*;
import es.upm.etsisi.poo.exceptions.*;

public class InventoryTest extends BaseTest {
    
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
                               
                               assertEquals(prod.toString(), inventory.getBaseProduct(prod.getId()).toString());
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
                               
                               assertEquals(prod.toString(), inventory.getBaseProduct(prod.getId()).toString());
                           });
	}
    
	@Test
        void readMissingProductTest() {
        assertThrows(MissingItemException.class, () -> {
                         Inventory.getInstance().getProduct(1);
                     });
	}
    
	@Test
        void removeProductTest() {
		final int productId = 1;
        assertDoesNotThrow(() -> {
                               Inventory.getInstance().createBaseProduct(productId, "Camiseta talla:M UPM", "CLOTHES", 15, 0, false);
                               Inventory.getInstance().deleteItem(productId);
                           });
        
        assertThrows(MissingItemException.class, () -> {
                         Inventory.getInstance().getProduct(productId);
                     });
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
        assertThrows(MissingItemException.class, () -> {
                         Inventory.getInstance().getProduct(productId);
                     });
	}
    
    
	@Test
        void addInvalidNameLengthTest() {
        int productId = 1;
        String longName = "Libro POOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO";
        assertThrows(DataException.class, () -> {
                         Inventory.getInstance().createBaseProduct(productId, longName, "BOOK", 25, 0, false);
                     });
        assertThrows(MissingItemException.class, () -> {
                         Inventory.getInstance().getProduct(productId);
                     });
	}
    
    
	@Test
        void addInvalidNegativePriceTest() {
        int productId = 1;
        assertThrows(DataException.class, () -> {
                         Inventory.getInstance().createBaseProduct(productId, "Libro POO", "BOOK", -2.5, 0, false);
                     });
        assertThrows(MissingItemException.class, () -> {
                         Inventory.getInstance().getProduct(productId);
                     });
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
        assertDoesNotThrow(() -> {
                               assertEquals(prod.toString(), inventory.getProduct(prod.getId()).toString()); // Name didn't change
                           });
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
        
        assertDoesNotThrow(() -> {
                               assertEquals(prod.toString(), inventory.getProduct(prod.getId()).toString()); // Price didn't change
                           });
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
        
        assertThrows(FullCollectionException.class, () -> {
                         assertNull(inventory.createBaseProduct(696969, "Libro POO", "BOOK", 25, 0, false));
                     });
	}
}