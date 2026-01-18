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
		
        assertDoesNotThrow(() -> {
							   BaseProduct prod = new BaseProduct(1, "Libro POO", 25, "BOOK", 0, false);
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
        assertDoesNotThrow(() -> {
							   BaseProduct prod = new BaseProduct(1, "Libro POO", 25, "BOOK", 0, false);
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
		final Inventory inv = Inventory.getInstance();
        assertDoesNotThrow(() -> {
                               BaseProduct p = inv.createBaseProduct(productId, "Camiseta talla:M UPM", "CLOTHES", 15, 0, false);
                               inv.deleteItemFromStrId(Integer.toString(1));
                           });
        
        assertThrows(MissingItemException.class, () -> {
                         inv.getProduct(productId);
                     });
	}
	
	
	@Test
        void removeServiceWhenRemovingProductSameIdNumber() {
		final Inventory inv = Inventory.getInstance();
		
        assertDoesNotThrow(() -> {
							   ServiceProduct s = inv.createServiceProduct("INSURANCE", App.now().plusHours(1));
							   inv.getItemFromStringId("1S"); // NOTE(erb): service id starts at 1
                           });
        assertThrows(MissingItemException.class, () -> inv.deleteItemFromStrId("1"));
        assertDoesNotThrow(() -> inv.getItemFromStringId("1S")); // NOTE(erb): wasn't deleted
	}
	
	
	@Test
        void removeProductWhenRemovingServiceSameIdNumber() {
		final Inventory inv = Inventory.getInstance();
		
        assertDoesNotThrow(() -> {
							   BaseProduct p = inv.createBaseProduct(1, "Camiseta talla:M UPM", "CLOTHES", 15, 0, false);
							   inv.getItemFromStringId("1");
                           });
        assertThrows(MissingItemException.class, () -> inv.deleteItemFromStrId("1S"));
        assertDoesNotThrow(() -> inv.getItemFromStringId("1")); // NOTE(erb): wasn't deleted
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
        void updateInvalidNameLengthTest() throws InvalidDataException {
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
        void updateInvalidBaseCategory() throws InvalidDataException {
        final Inventory inventory = Inventory.getInstance();
		final BaseProduct prod = new BaseProduct(1, "Libro POO",  25, "BOOK", 0, false);
        assertDoesNotThrow(() -> {
                               inventory.createBaseProduct(prod.getId(), 
                                                           prod.getName(), 
                                                           prod.getCategory().toString(), 
                                                           prod.getPrice(), 
                                                           0, false);
                           });
        
        assertThrows(InvalidDataException.class, () -> {
                         inventory.updateBaseProductCategory(prod.getId(), "JIBERISH");
                     });
		
        assertDoesNotThrow(() -> {
                               assertEquals(prod.toString(), inventory.getProduct(prod.getId()).toString()); // Category didn't change
                           });
	}
    
	
	@Test
        void updateBaseCategoryBreakingMaxPersCount() throws InvalidDataException {
        final Inventory inventory = Inventory.getInstance();
		
		// NOTE(erb): CLOTHES allows 5 personalizations, we are setting a max of 4 (correct)
		int maxPersonalizations = 4;
		final BaseProduct prod = new BaseProduct(1, "Libro POO",  25, "CLOTHES", maxPersonalizations, true);
        assertDoesNotThrow(() ->  {
							   inventory.createBaseProduct(prod.getId(), 
														   prod.getName(), 
														   prod.getCategory().toString(), 
														   prod.getPrice(), 
														   maxPersonalizations, true);
                           });
        
		// NOTE(erb): ELECTRONICS allows only 2 personalizations, which is less than the max we set 4.
        assertThrows(InvalidDataException.class, () -> {
                         inventory.updateBaseProductCategory(prod.getId(), "ELECTRONICS");
                     });
		
        assertDoesNotThrow(() -> {
                               assertEquals(prod.toString(), inventory.getProduct(prod.getId()).toString()); // Category didn't change
                           });
	}
    
	@Test
        void updateInvalidPriceTest() throws InvalidDataException {
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
                         inventory.createBaseProduct(696969, "Libro POO", "BOOK", 25, 0, false);
                     });
		
		assertEquals(inventory.getItems().size(), Inventory.MAX_PRODUCTS);
	}
	
	
	@Test
        void addTimedAfterWithoutEnoughHours() {
		final Inventory inventory = Inventory.getInstance();
		
        assertThrows(InvalidDataException.class, () -> {
						 inventory.createTimedProduct(696969, "timed thing", 25, 10, "MEETING", App.now().plusHours(1));
                     });
		
		assertEquals(inventory.getItems().size(), 0);
	}
}