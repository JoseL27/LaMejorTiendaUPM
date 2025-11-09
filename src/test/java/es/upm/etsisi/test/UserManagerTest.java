package es.upm.etsisi.test;

import static org.junit.jupiter.api.Assertions.*;

import es.upm.etsisi.poo.Cashier;
import es.upm.etsisi.poo.Client;
import es.upm.etsisi.poo.UserManager;
import org.junit.jupiter.api.*;

import java.util.Locale;

public class UserManagerTest {
    UserManager testUserManager;

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
    void CreateNewUserManager() {
        this.testUserManager = new UserManager();
    }

    @Test
    void normalAddCashier() {
        boolean result = this.testUserManager.addCashier("Cajero1", "cajero1@upm.es");
        assertTrue(result);
    }

    @Test
    void addCashierTwice() {
        boolean result = this.testUserManager.addCashier("Cajero1", "cajero1@upm.es");
        assertTrue(result);
        result = this.testUserManager.addCashier("Cajero2", "cajero2@upm.es");
        assertTrue(result);
    }

    @Test
    void addCashierWithId() {
        boolean result = this.testUserManager.addCashier("UW0000000", "Cajero1", "cajero1@upm.es");
        assertTrue(result);
    }

    @Test
    void addCashierWithExistingId() {
        boolean result = this.testUserManager.addCashier("UW0000000", "Cajero1", "cajero1@upm.es");
        assertTrue(result);
        result = this.testUserManager.addCashier("UW0000000", "Cajero2", "cajero2@upm.es");
        assertFalse(result);
    }

    @Test
    void addCashierWithInvalidId() {
        boolean result = this.testUserManager.addCashier("UX0000000", "Cajero0", "cajero0@upm.es");
        assertFalse(result);
        result = this.testUserManager.addCashier("UW00000010", "Cajero0", "cajero0@upm.es");
        assertFalse(result);
    }

    @Test
    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
    void FillCashiersNoId() {
        // Filling
        String cashierName;
        String cashierEmail;
        boolean result;
        for (int i = UserManager.MIN_CASHIER_ID; i <= UserManager.MAX_CASHIER_ID; i++) {
            cashierName = String.format("Cashier #%d", i);
            cashierEmail = String.format("cashier%d@upm.es", i);
            result = this.testUserManager.addCashier(cashierName, cashierEmail);
            assertTrue(result);
        }
        // No more cashiers
        result = this.testUserManager.addCashier("Overfilled", "overfilled@upm.es");
        assertFalse(result);
    }

    @Test
    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
    void FillCashiersWithId() {
        // Filling
        String cashierId;
        String cashierName;
        String cashierEmail;
        boolean result;
        for (int i = UserManager.MIN_CASHIER_ID; i <= UserManager.MAX_CASHIER_ID; i++) {
            cashierId = String.format("UW%07d", i);
            cashierName = String.format("Cashier #%d", i);
            cashierEmail = String.format("cashier%d@upm.es", i);
            result = this.testUserManager.addCashier(cashierId, cashierName, cashierEmail);
            assertTrue(result);
        }
        // No more cashiers
        result = this.testUserManager.addCashier("Overfilled", "overfilled@upm.es");
        assertFalse(result);
        result = this.testUserManager.addCashier("UW0000000", "Overfilled", "overfilled@upm.es");
        assertFalse(result);
    }

    @Test
    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
    void fillCashierWithExistingMaxIdEdgeCaseTest() {
        // Filling
        String cashierId = String.format("UW%07d", UserManager.MAX_CASHIER_ID);
        String cashierName = String.format("Cashier #%d", UserManager.MAX_CASHIER_ID);;
        String cashierEmail = String.format("cashier%d@upm.es", UserManager.MAX_CASHIER_ID);;
        boolean result = this.testUserManager.addCashier(cashierId, cashierName, cashierEmail);
        assertTrue(result);
        for (int i = UserManager.MIN_CASHIER_ID; i < UserManager.MAX_CASHIER_ID - 1; i++) {
            cashierId = String.format("UW%07d", i);
            cashierName = String.format("Cashier #%d", i);
            cashierEmail = String.format("cashier%d@upm.es", i);
            result = this.testUserManager.addCashier(cashierId, cashierName, cashierEmail);
            assertTrue(result);
        }
        result = this.testUserManager.addCashier("Cashier #9999998", "cashier9999998@upm.es");
        assertTrue(result);
        // No more cashiers
        result = this.testUserManager.addCashier("Overfilled", "overfilled@upm.es");
        assertFalse(result);
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
        boolean result;
        for (int i = UserManager.MIN_CASHIER_ID; i < UserManager.MAX_CASHIER_ID; i++) {
            cashierId = String.format("UW%07d", i);
            cashierName = String.format("Cashier #%d", i);
            cashierEmail = String.format("cashier%d@upm.es", i);
            result = this.testUserManager.addCashier(cashierId, cashierName, cashierEmail);
            assertTrue(result);
        }

        // Add last cashier
        result = this.testUserManager.addCashier("Cashier #9999999", "cashier9999999@upm.es");
        assertTrue(result);
        // No more cashiers
        result = this.testUserManager.addCashier("Overfilled", "overfilled@upm.es");
        assertFalse(result);
    }

    @Test
    void removeNonExistentCashier() {
        normalAddCashier();
        boolean result = this.testUserManager.removeCashier("UW0000001");
        assertFalse(result);
    }

    @Test
    void removeInvalidIdCashier() {
        normalAddCashier();
        boolean result = this.testUserManager.removeCashier("UX0000000");
        assertFalse(result);
        result = this.testUserManager.removeCashier("UW00000000");
        assertFalse(result);
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
        boolean result = this.testUserManager.addClient("X0000000Y", "Client 0", "client1@example.com", cashier);
        assertTrue(result);
    }

    @Test
    void addClientSameId() {
        this.testUserManager.addCashier("Cashier 0", "cashier0@upm.es");
        Cashier cashier = this.testUserManager.findCashier("UW0000000");
        boolean result = this.testUserManager.addClient("X0000000Y", "Client 0", "client1@example.com", cashier);
        assertTrue(result);
        result = this.testUserManager.addClient("X0000000Y", "Client 0", "client1@example.com", cashier);
        assertFalse(result);
    }

    @Test
    void removeClient() {
        addOneClient();
        boolean result = this.testUserManager.removeClient("X0000000Y");
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
        Client client = this.testUserManager.findClient("X0000000X");
        assertNull(client);
    }

    @Test
    void findClient() {
        addOneClient();
        Client client = this.testUserManager.findClient("X0000000Y");
        assertNotNull(client);
        assertEquals("X0000000Y", client.getId());
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
    void fillCashierTicket() {
        normalAddCashier();
        boolean result;
        Cashier cashier = this.testUserManager.findCashier("UW0000000");
        for (int i = UserManager.MIN_TICKET_ID; i <= UserManager.MAX_TICKET_ID; i++) {
            result = cashier.createTicket(this.testUserManager.generateUniqueTicketId());
            assertTrue(result);
        }
        result = cashier.createTicket(this.testUserManager.generateUniqueTicketId());
        assertFalse(result);
    }

    @Test
    void fillCashierTicketWithIterationOnly() {
        normalAddCashier();
        // Render auto-increment useless
        boolean result;
        for (int i = UserManager.MIN_TICKET_ID; i <= UserManager.MAX_TICKET_ID; i++) {
            int generatedId = this.testUserManager.generateUniqueTicketId();
            assertEquals(i, generatedId);
        }

        // Stress test
        Cashier cashier = this.testUserManager.findCashier("UW0000000");
        for (int i = UserManager.MIN_TICKET_ID; i <= UserManager.MAX_TICKET_ID; i++) {
            result = cashier.createTicket(this.testUserManager.generateUniqueTicketId());
            assertTrue(result);
        }
        result = cashier.createTicket(this.testUserManager.generateUniqueTicketId());
        assertFalse(result);
        assertEquals(UserManager.MAX_TICKET_ID - UserManager.MIN_TICKET_ID + 1, cashier.getCreatedTicketAmount());
    }
}
