package es.upm.etsisi.test;

import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

import es.upm.etsisi.poo.Cashier;
import es.upm.etsisi.poo.Client;
import es.upm.etsisi.poo.UserManager;
import es.upm.etsisi.poo.Inventory;
import es.upm.etsisi.poo.Ticket;
import org.junit.jupiter.api.*;

import java.util.Locale;

public class UserManagerTest {
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
    void ResetSingletons() {
		try { 
			Field f = UserManager.class.getDeclaredField("instance");
			f.setAccessible(true);
			f.set(null, null);

			f = Inventory.class.getDeclaredField("instance");
			f.setAccessible(true);
			f.set(null, null);
		} catch (Exception e) {
			fail(e.getMessage());
		}
    }

    @Test
    void normalAddCashier() {
		assertDoesNotThrow(() -> { 
				UserManager.getInstance().addCashier("Cajero1", "cajero1@upm.es");
			});
    }

    @Test
    void addCashierTwice() {
		assertDoesNotThrow(() -> {
				UserManager.getInstance().addCashier("Cajero1", "cajero1@upm.es");
				UserManager.getInstance().addCashier("Cajero2", "cajero2@upm.es");
			});
    }

    @Test
    void addCashierWithId() {
		assertDoesNotThrow(() -> {
				UserManager.getInstance().addCashier("UW0000000", "Cajero1", "cajero1@upm.es");
			});
    }

    @Test
    void addCashierWithExistingId() {
		assertDoesNotThrow(() -> {
				UserManager.getInstance().addCashier("UW0000000", "Cajero1", "cajero1@upm.es");				
			});
		assertThrows(Exception.class, () -> { 
				UserManager.getInstance().addCashier("UW0000000", "Cajero2", "cajero2@upm.es");
			});
    }

    @Test
    void addCashierWithInvalidId() {
		assertThrows(AssertionError.class, () -> {
				UserManager.getInstance().addCashier("UX00000", "Cajero0", "cajero0@upm.es");
			});
    }

    @Test
    void addCashierWithInvalidIdOther() {
		assertThrows(AssertionError.class, () -> { 
				UserManager.getInstance().addCashier("UW000000100", "Cajero0", "cajero0@upm.es");
			});
	}
			

    @Test
    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
    void FillCashiersNoId() throws Exception {
        // Filling
        String cashierName;
        String cashierEmail;
        Cashier result = null;
        for (int i = UserManager.MIN_CASHIER_ID; i <= UserManager.MAX_CASHIER_ID; i++) {
            cashierName = String.format("Cashier #%d", i);
            cashierEmail = String.format("cashier%d@upm.es", i);
            result = UserManager.getInstance().addCashier(cashierName, cashierEmail);
            assertNotNull(result);
        }
        // No more cashiers
        result = UserManager.getInstance().addCashier("Overfilled", "overfilled@upm.es");
        assertNull(result);
    }

    @Test
    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
    void FillCashiersWithId() throws Exception {
        // Filling
        String cashierId;
        String cashierName;
        String cashierEmail;
        Cashier result = null;
        for (int i = UserManager.MIN_CASHIER_ID; i <= UserManager.MAX_CASHIER_ID; i++) {
            cashierId = String.format("UW%07d", i);
            cashierName = String.format("Cashier #%d", i);
            cashierEmail = String.format("cashier%d@upm.es", i);
            result = UserManager.getInstance().addCashier(cashierId, cashierName, cashierEmail);
            assertNotNull(result);
        }
        // No more cashiers
        result = UserManager.getInstance().addCashier("Overfilled", "overfilled@upm.es");
        assertNotNull(result);
        result = UserManager.getInstance().addCashier("UW0000000", "Overfilled", "overfilled@upm.es");
        assertNotNull(result);
    }

    @Test
    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
    void fillCashierWithExistingMaxIdEdgeCaseTest() throws Exception {
        // Filling
        String cashierId = String.format("UW%07d", UserManager.MAX_CASHIER_ID);
        String cashierName = String.format("Cashier #%d", UserManager.MAX_CASHIER_ID);;
        String cashierEmail = String.format("cashier%d@upm.es", UserManager.MAX_CASHIER_ID);;
        Cashier result = UserManager.getInstance().addCashier(cashierId, cashierName, cashierEmail);
        assertNotNull(result);
        for (int i = UserManager.MIN_CASHIER_ID; i < UserManager.MAX_CASHIER_ID - 1; i++) {
            cashierId = String.format("UW%07d", i);
            cashierName = String.format("Cashier #%d", i);
            cashierEmail = String.format("cashier%d@upm.es", i);
            result = UserManager.getInstance().addCashier(cashierId, cashierName, cashierEmail);
            assertNotNull(result);
        }
        result = UserManager.getInstance().addCashier("Cashier #9999998", "cashier9999998@upm.es");
        assertNotNull(result);
        // No more cashiers
        result = UserManager.getInstance().addCashier("Overfilled", "overfilled@upm.es");
        assertNotNull(result);
    }

    // If for what reason the nextCashierId broke and is unavailable, make sure that generation by iteration works
    @Test
    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
    void fillCashierWithExistingMaxIdEdgeCaseTest2() throws Exception {
        // Render auto-increment useless
        for (int i = UserManager.MIN_CASHIER_ID; i <= UserManager.MAX_CASHIER_ID; i++) {
            String generatedId = UserManager.getInstance().generateUniqueCashierId();
            assertEquals(String.format("%07d", i), generatedId.substring(2));
        }

        // Fill until last cashier
        String cashierId;
        String cashierName;
        String cashierEmail;
        Cashier result = null;
        for (int i = UserManager.MIN_CASHIER_ID; i < UserManager.MAX_CASHIER_ID; i++) {
            cashierId = String.format("UW%07d", i);
            cashierName = String.format("Cashier #%d", i);
            cashierEmail = String.format("cashier%d@upm.es", i);
            result = UserManager.getInstance().addCashier(cashierId, cashierName, cashierEmail);
            assertNotNull(result);
        }

        // Add last cashier
        result = UserManager.getInstance().addCashier("Cashier #9999999", "cashier9999999@upm.es");
        assertNotNull(result);
        // No more cashiers
        result = UserManager.getInstance().addCashier("Overfilled", "overfilled@upm.es");
        assertNull(result);
    }

    @Test
    void removeNonExistentCashier() {
        normalAddCashier();
		String id = "UW0000001";
		Exception e = assertThrows(Exception.class, () -> { 
				UserManager.getInstance().removeCashier(id); 
			});
		assertEquals(String.format("failed to remove cashier with id '%s'", id), e.getMessage());		
    }

    @Test
    void removeCashier() {
        normalAddCashier();
		assertDoesNotThrow(() -> { 
				UserManager.getInstance().removeCashier("UW0000000"); 
			});		
    }

	@Test		
    void findNonExistentCashier() {
        normalAddCashier();
		String id = "UW0000001";
		Exception e = assertThrows(Exception.class, () -> { 
				UserManager.getInstance().getCashier(id); 
			});
		assertEquals(e.getMessage(), String.format("cashier with id '%s' not found", id));
    }

	@Test		
    void findInvalidIdCashier() {
        normalAddCashier();
		assertThrows(AssertionError.class, () -> {
				UserManager.getInstance().getCashier("UX0000000");
			});
    }

    @Test
    void findCashier() {
        normalAddCashier();
		assertDoesNotThrow(() -> {
				Cashier cashier = UserManager.getInstance().getCashier("UW0000000");
				assertEquals("UW0000000", cashier.getId());
			});
    }

    @Test
    void addOneClient() {
		assertDoesNotThrow(() -> {
				UserManager.getInstance().addCashier("Cashier 0", "cashier0@upm.es");
				Cashier cashier = UserManager.getInstance().getCashier("UW0000000");
				UserManager.getInstance().addClient("00000000Y", "Client 0", "client1@example.com", cashier.getId());
			});
    }

	@Test		
    void addClientSameId() throws Exception {
		Cashier cashier = assertDoesNotThrow(() -> {
				UserManager.getInstance().addCashier("Cashier 0", "cashier0@upm.es");
				Cashier result = UserManager.getInstance().getCashier("UW0000000");
				UserManager.getInstance().addClient("00000000Y", "Client 1", "client1@example.com", result.getId());
				return result;
		});
		
		assertThrows(Exception.class, () -> {
				UserManager.getInstance().addClient("00000000Y", "Client 2", "client2@example.com", cashier.getId());
			});		
    }

    @Test
    void removeClient() {
        addOneClient();
		assertDoesNotThrow(() -> { 
				UserManager.getInstance().removeClient("00000000Y");
			});
    }

	@Test		
    void removeClientNonExistent() {
        addOneClient();
		assertThrows(Exception.class, () -> { 
				UserManager.getInstance().removeClient("X0000000X");
			});
    }

	@Test		
    void findNonExistentClient() {
        addOneClient();
		assertThrows(Exception.class, () -> { 
				UserManager.getInstance().getClient("00000000X");
			});
    }

    @Test
    void findClient() {
        addOneClient();
		assertDoesNotThrow(() -> { 
				Client client = UserManager.getInstance().getClient("00000000Y");
				assertEquals("00000000Y", client.getId());
			});
    }

    @Test
    void listClientEmpty() {
        Client[] clients = UserManager.getInstance().listClients();
        assertEquals(0, clients.length);
    }

    @Test
    void listClient() {
        addOneClient();
        Client[] clients = UserManager.getInstance().listClients();
        assertEquals(1, clients.length);
    }

    @Test
    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
    void fillCashierTicket() throws Exception {
        normalAddCashier();
        Ticket result = null;
        Cashier cashier = UserManager.getInstance().getCashier("UW0000000");
        for (int i = UserManager.MIN_TICKET_ID; i <= UserManager.MAX_TICKET_ID; i++) {
            result = cashier.createTicket(UserManager.getInstance().generateUniqueTicketId());
            assertNotNull(result);
        }
        assertNull(UserManager.getInstance().generateUniqueTicketId());
    }

    @Test
    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
    void fillCashierTicketLastTicketWithIterationOnly() throws Exception {
        normalAddCashier();
        // Render auto-increment useless, and create the rest of the ticket wicked fast
        Ticket result = null;
        Cashier cashier = UserManager.getInstance().getCashier("UW0000000");
        for (int i = UserManager.MIN_TICKET_ID; i < UserManager.MAX_TICKET_ID; i++) {
            int generatedId = UserManager.getInstance().generateUniqueTicketId();
            result = cashier.createTicket(generatedId);
			assertNotNull(result);
            assertEquals(i, generatedId);
        }
        UserManager.getInstance().generateUniqueCashierId();

        // This should generate 99999
        result = cashier.createTicket(UserManager.getInstance().generateUniqueTicketId());
        assertNotNull(result);
        // Overfilled should return null
        Integer overfilled = UserManager.getInstance().generateUniqueTicketId();
        assertNull(overfilled);
        assertEquals(UserManager.MAX_TICKET_ID - UserManager.MIN_TICKET_ID + 1, cashier.getCreatedTicketAmount());
    }
}
