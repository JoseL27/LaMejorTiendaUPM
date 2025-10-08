package es.upm.etsisi.test;

import es.upm.etsisi.poo.Ticket;
import es.upm.etsisi.poo.Product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

public class TicketTest {

	// Success
	@Test	
	void addProductTest() { 
		Ticket ticket = new Ticket();
		assertTrue(ticket.addProduct(new Product(1, "Product", Product.Category.BOOK, 10), 1));
	}

	@Test		
	void removeProductTest() { 
		Ticket ticket = new Ticket();
		ticket.addProduct(new Product(1, "Product", Product.Category.BOOK, 10), 1);
		assertNotNull(ticket.removeProduct(1));
	}

	@Test			
	void listProductTest() { 
		Ticket ticket = new Ticket();
		ticket.addProduct(new Product(1, "Libro POO V2", Product.Category.BOOK, 30), 2);

		String expectedString = 
			"{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0\n"
			+"{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0\n"
			+"Total price: 60.0\n"
			+"Total discount: 6.0\n"
			+"Final Price: 54.0";

		assertEquals(expectedString, ticket.summaryString());
	}

	@Test
	void listProductTest2() {
		Ticket ticket = new Ticket();
		ticket.addProduct(new Product(1, "Libro POO V2", Product.Category.BOOK, 30), 2);
		ticket.addProduct(new Product(2, "Camiseta talla:M UPM", Product.Category.CLOTHES, 15), 1);
		
		String expectedString = 
			"{class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}\n"
			+"{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0\n"
			+"{class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0\n"
			+"Total price: 75.0\n"
			+"Total discount: 6.0\n"
			+"Final Price: 69.0";
		
		assertEquals(expectedString, ticket.summaryString());
	}

	@Test
	void listProductTest3() {
		Ticket ticket = new Ticket();
		ticket.addProduct(new Product(2, "Camiseta talla:M UPM", Product.Category.CLOTHES, 15), 1);
		
		String expectedString = "{class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:15.0}\n"
			+"Total price: 15.0\n"
			+"Total discount: 0.0\n"
			+"Final Price: 15.0";
		assertEquals(expectedString, ticket.summaryString());
	}

	@Test
	void discountTest() {
		Ticket ticket = new Ticket();
		ticket.addProduct(new Product(2, "Camiseta talla:M UPM", Product.Category.CLOTHES, 10), 1);
		ticket.addProduct(new Product(3, "Camiseta talla:XL teleco", Product.Category.CLOTHES, 20),1);

		String expectedString = "{class:Product, id:2, name:'Camiseta talla:M UPM', category:CLOTHES, price:10.0} **discount -0.7\n"
				+"{class:Product, id:3, name:'Camiseta talla:XL teleco', category:CLOTHES, price:20.0} **discount -1.4\n"
				+"Total price: 30.0\n"
				+"Total discount: 2.1\n"
				+"Final Price: 27.9";
		assertEquals(expectedString, ticket.summaryString());
	}

	// Failures
	@Test
	void maxProductTest() {
		Ticket ticket = new Ticket();
		for (int i = 0; i < Ticket.TICKET_MAX_PRODUCTS; ++i) { 
			ticket.addProduct(new Product(i, String.format("Product(%d)", i), Product.Category.CLOTHES, (i+1)*10), 1);
		}
		assertFalse(ticket.addProduct(new Product(Ticket.TICKET_MAX_PRODUCTS+1, "Product", Product.Category.CLOTHES, 10), 1));
	}
}
