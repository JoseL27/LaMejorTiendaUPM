package es.upm.etsisi.test;

import static org.junit.jupiter.api.Assertions.*;

import es.upm.etsisi.poo.Cashier;
import es.upm.etsisi.poo.Client;
import es.upm.etsisi.poo.UserManager;
import es.upm.etsisi.poo.Ticket;
import org.junit.jupiter.api.*;

import java.util.Locale;

public class UserManagerTest {
	private final String testNIE = "55630667S";
    private UserManager testUserManager;

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
        this.testUserManager = new UserManager();
    }

    @Test
    void normalAddCashier() {
        Cashier result = this.testUserManager.addCashier("Cajero1", "cajero1@upm.es");
        assertNotNull(result);
    }

    @Test
    void addCashierTwice() {
        Cashier result = this.testUserManager.addCashier("Cajero1", "cajero1@upm.es");
        assertNotNull(result);
        result = this.testUserManager.addCashier("Cajero2", "cajero2@upm.es");
        assertNotNull(result);
    }

    @Test
    void addCashierWithId() {
        Cashier result = this.testUserManager.addCashier("UW0000000", "Cajero1", "cajero1@upm.es");
        assertNotNull(result);
    }

    @Test
    void addCashierWithExistingId() {
        Cashier result = this.testUserManager.addCashier("UW0000000", "Cajero1", "cajero1@upm.es");
        assertNotNull(result);
        result = this.testUserManager.addCashier("UW0000000", "Cajero2", "cajero2@upm.es");
        assertNull(result);
    }

    @Test
    void addCashierWithInvalidId() {
        Cashier result = this.testUserManager.addCashier("UX0000000", "Cajero0", "cajero0@upm.es");
        assertNull(result);
        result = this.testUserManager.addCashier("UW000000100", "Cajero0", "cajero0@upm.es");
        assertNull(result);
    }

    @Test
    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
    void FillCashiersNoId() {
        // Filling
        String cashierName;
        String cashierEmail;
        Cashier result = null;
        for (int i = UserManager.MIN_CASHIER_ID; i <= UserManager.MAX_CASHIER_ID; i++) {
            cashierName = String.format("Cashier #%d", i);
            cashierEmail = String.format("cashier%d@upm.es", i);
            result = this.testUserManager.addCashier(cashierName, cashierEmail);
            assertNotNull(result);
        }
        // No more cashiers
        result = this.testUserManager.addCashier("Overfilled", "overfilled@upm.es");
        assertNull(result);
    }

    @Test
    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
    void FillCashiersWithId() {
        // Filling
        String cashierId;
        String cashierName;
        String cashierEmail;
        Cashier result = null;
        for (int i = UserManager.MIN_CASHIER_ID; i <= UserManager.MAX_CASHIER_ID; i++) {
            cashierId = String.format("UW%07d", i);
            cashierName = String.format("Cashier #%d", i);
            cashierEmail = String.format("cashier%d@upm.es", i);
            result = this.testUserManager.addCashier(cashierId, cashierName, cashierEmail);
            assertNotNull(result);
        }
        // No more cashiers
        result = this.testUserManager.addCashier("Overfilled", "overfilled@upm.es");
        assertNotNull(result);
        result = this.testUserManager.addCashier("UW0000000", "Overfilled", "overfilled@upm.es");
        assertNotNull(result);
    }

    @Test
    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
    void fillCashierWithExistingMaxIdEdgeCaseTest() {
        // Filling
        String cashierId = String.format("UW%07d", UserManager.MAX_CASHIER_ID);
        String cashierName = String.format("Cashier #%d", UserManager.MAX_CASHIER_ID);;
        String cashierEmail = String.format("cashier%d@upm.es", UserManager.MAX_CASHIER_ID);;
        Cashier result = this.testUserManager.addCashier(cashierId, cashierName, cashierEmail);
        assertNotNull(result);
        for (int i = UserManager.MIN_CASHIER_ID; i < UserManager.MAX_CASHIER_ID - 1; i++) {
            cashierId = String.format("UW%07d", i);
            cashierName = String.format("Cashier #%d", i);
            cashierEmail = String.format("cashier%d@upm.es", i);
            result = this.testUserManager.addCashier(cashierId, cashierName, cashierEmail);
            assertNotNull(result);
        }
        result = this.testUserManager.addCashier("Cashier #9999998", "cashier9999998@upm.es");
        assertNotNull(result);
        // No more cashiers
        result = this.testUserManager.addCashier("Overfilled", "overfilled@upm.es");
        assertNotNull(result);
    }

    // If for what reason the nextCashierId broke and is unavailable, make sure that generation by iteration works
    @Test
    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
    void fillCashierWithExistingMaxIdEdgeCaseTest2() {
        // Render auto-increment useless
        for (int i = UserManager.MIN_CASHIER_ID; i <= UserManager.MAX_CASHIER_ID; i++) {
            String generatedId = this.testUserManager.generateUniqueCashierId();
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
            result = this.testUserManager.addCashier(cashierId, cashierName, cashierEmail);
            assertNotNull(result);
        }

        // Add last cashier
        result = this.testUserManager.addCashier("Cashier #9999999", "cashier9999999@upm.es");
        assertNotNull(result);
        // No more cashiers
        result = this.testUserManager.addCashier("Overfilled", "overfilled@upm.es");
        assertNull(result);
    }

    @Test
    void removeNonExistentCashier() {
        normalAddCashier();
        boolean result = this.testUserManager.removeCashier("UW0000001");
        assertNotNull(result);
    }

    @Test
    void removeInvalidIdCashier() {
        normalAddCashier();
        boolean result = this.testUserManager.removeCashier("UX0000000");
        assertNotNull(result);
        result = this.testUserManager.removeCashier("UW00000000");
        assertNotNull(result);
    }

    @Test
    void removeCashier() {
        normalAddCashier();
        boolean result = this.testUserManager.removeCashier("UW0000000");
        assertTrue(result);
    }

    @Test
    void findNonExistentCashier() {
        normalAddCashier();
        Cashier cashier = this.testUserManager.findCashier("UW0000001");
        assertNull(cashier);
    }

    @Test
    void findInvalidIdCashier() {
        normalAddCashier();
        Cashier cashier = this.testUserManager.findCashier("UX0000000");
        assertNull(cashier);
        cashier = this.testUserManager.findCashier("UW00000000");
        assertNull(cashier);
    }

    @Test
    void findCashier() {
        normalAddCashier();
        Cashier cashier = this.testUserManager.findCashier("UW0000000");
        assertNotNull(cashier);
        assertEquals("UW0000000", cashier.getId());
    }

    @Test
    void addOneClient() {
        this.testUserManager.addCashier("Cashier 0", "cashier0@upm.es");
        Cashier cashier = this.testUserManager.findCashier("UW0000000");
        Client result = this.testUserManager.addClient(testNIE, "Client 0", "client1@example.com", cashier);
        assertNotNull(result);
    }

    @Test
    void addClientSameId() {
        this.testUserManager.addCashier("Cashier 0", "cashier0@upm.es");
        Cashier cashier = this.testUserManager.findCashier("UW0000000");
        Client result = this.testUserManager.addClient(testNIE, "Client 1", "client1@example.com", cashier);
        assertNotNull(result);
        result = this.testUserManager.addClient(testNIE, "Client 2", "client2@example.com", cashier);
        assertNull(result);
    }

    @Test
    void removeClient() {
        addOneClient();
        boolean result = this.testUserManager.removeClient(testNIE);
        assertTrue(result);
    }

    @Test
    void removeClientNonExistent() {
        addOneClient();
        boolean result = this.testUserManager.removeClient("X0000000X");
        assertFalse(result);
    }

    @Test
    void findNonExistentClient() {
        addOneClient();
        Client client = this.testUserManager.findClient("00000000X");
        assertNull(client);
    }

    @Test
    void findClient() {
        addOneClient();
        Client client = this.testUserManager.findClient(testNIE);
        assertNotNull(client);
        assertEquals(testNIE, client.getId());
    }

    @Test
    void listClientEmpty() {
        Client[] clients = this.testUserManager.listClients();
        assertEquals(0, clients.length);
    }

    @Test
    void listClient() {
        addOneClient();
        Client[] clients = this.testUserManager.listClients();
        assertEquals(1, clients.length);
    }

    @Test
    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
    void fillCashierTicket() {
        normalAddCashier();
        Ticket result = null;
        Cashier cashier = this.testUserManager.findCashier("UW0000000");
        for (int i = UserManager.MIN_TICKET_ID; i <= UserManager.MAX_TICKET_ID; i++) {
            result = cashier.createTicket(this.testUserManager.generateUniqueTicketId());
            assertNotNull(result);
        }
        assertNull(this.testUserManager.generateUniqueTicketId());
    }

    @Test
    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
    void fillCashierTicketLastTicketWithIterationOnly() {
        normalAddCashier();
        // Render auto-increment useless, and create the rest of the ticket wicked fast
        Ticket result = null;
        Cashier cashier = this.testUserManager.findCashier("UW0000000");
        for (int i = UserManager.MIN_TICKET_ID; i < UserManager.MAX_TICKET_ID; i++) {
            int generatedId = this.testUserManager.generateUniqueTicketId();
            result = cashier.createTicket(generatedId);
			assertNotNull(result);
            assertEquals(i, generatedId);
        }
        this.testUserManager.generateUniqueCashierId();

        // This should generate 99999
        result = cashier.createTicket(this.testUserManager.generateUniqueTicketId());
        assertNotNull(result);
        // Overfilled should return null
        Integer overfilled = this.testUserManager.generateUniqueTicketId();
        assertNull(overfilled);
        assertEquals(UserManager.MAX_TICKET_ID - UserManager.MIN_TICKET_ID + 1, cashier.getCreatedTicketAmount());
    }
}
