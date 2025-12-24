//package es.upm.etsisi.test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import es.upm.etsisi.poo.Cashier;
//import es.upm.etsisi.poo.Client;
//import es.upm.etsisi.poo.UserManager;
//import es.upm.etsisi.poo.Ticket;
//import org.junit.jupiter.api.*;
//import java.lang.reflect.Field;
//
//import java.util.Locale;
//
//public class UserManagerTest {
//	private final String testNIE = "55630667S";
//
//    // DICTATOR LOCALE
//    @BeforeAll
//    static void setEnUSLocale() {
//        Locale.setDefault(new Locale("en", "US"));
//    }
//
//    @AfterAll
//    static void unsetEnUSLocale() {
//        Locale.setDefault(Locale.getDefault());
//    }
//
//    @BeforeEach
//    void createNewUserManager() {
//		try {
//			Field f = UserManager.class.getDeclaredField("instance");
//			f.setAccessible(true);
//			f.set(null, null);
//		} catch (Exception e) { }
//    }
//
//    @Test
//    void normalAddCashier() {
//        Cashier result = UserManager.getInstance().addCashier("Cajero1", "cajero1@upm.es");
//        assertNotNull(result);
//    }
//
//    @Test
//    void addCashierTwice() {
//        Cashier result = UserManager.getInstance().addCashier("Cajero1", "cajero1@upm.es");
//        assertNotNull(result);
//        result = UserManager.getInstance().addCashier("Cajero2", "cajero2@upm.es");
//        assertNotNull(result);
//    }
//
//    @Test
//    void addCashierWithId() {
//        Cashier result = UserManager.getInstance().addCashier("UW0000000", "Cajero1", "cajero1@upm.es");
//        assertNotNull(result);
//    }
//
//    @Test
//    void addCashierWithExistingId() {
//        Cashier result = UserManager.getInstance().addCashier("UW0000000", "Cajero1", "cajero1@upm.es");
//        assertNotNull(result);
//        result = UserManager.getInstance().addCashier("UW0000000", "Cajero2", "cajero2@upm.es");
//        assertNull(result);
//    }
//
//    @Test
//    void addCashierWithInvalidId() {
//        Cashier result = UserManager.getInstance().addCashier("UX0000000", "Cajero0", "cajero0@upm.es");
//        assertNull(result);
//        result = UserManager.getInstance().addCashier("UW000000100", "Cajero0", "cajero0@upm.es");
//        assertNull(result);
//    }
//
//    @Test
//    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
//    void FillCashiersNoId() {
//        // Filling
//        String cashierName;
//        String cashierEmail;
//        Cashier result = null;
//        for (int i = UserManager.MIN_CASHIER_ID; i <= UserManager.MAX_CASHIER_ID; i++) {
//            cashierName = String.format("Cashier #%d", i);
//            cashierEmail = String.format("cashier%d@upm.es", i);
//            result = UserManager.getInstance().addCashier(cashierName, cashierEmail);
//            assertNotNull(result);
//        }
//        // No more cashiers
//        result = UserManager.getInstance().addCashier("Overfilled", "overfilled@upm.es");
//        assertNull(result);
//    }
//
//    @Test
//    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
//    void FillCashiersWithId() {
//        // Filling
//        String cashierId;
//        String cashierName;
//        String cashierEmail;
//        Cashier result = null;
//        for (int i = UserManager.MIN_CASHIER_ID; i <= UserManager.MAX_CASHIER_ID; i++) {
//            cashierId = String.format("UW%07d", i);
//            cashierName = String.format("Cashier #%d", i);
//            cashierEmail = String.format("cashier%d@upm.es", i);
//            result = UserManager.getInstance().addCashier(cashierId, cashierName, cashierEmail);
//            assertNotNull(result);
//        }
//        // No more cashiers
//        result = UserManager.getInstance().addCashier("Overfilled", "overfilled@upm.es");
//        assertNotNull(result);
//        result = UserManager.getInstance().addCashier("UW0000000", "Overfilled", "overfilled@upm.es");
//        assertNotNull(result);
//    }
//
//    @Test
//    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
//    void fillCashierWithExistingMaxIdEdgeCaseTest() {
//        // Filling
//        String cashierId = String.format("UW%07d", UserManager.MAX_CASHIER_ID);
//        String cashierName = String.format("Cashier #%d", UserManager.MAX_CASHIER_ID);;
//        String cashierEmail = String.format("cashier%d@upm.es", UserManager.MAX_CASHIER_ID);;
//        Cashier result = UserManager.getInstance().addCashier(cashierId, cashierName, cashierEmail);
//        assertNotNull(result);
//        for (int i = UserManager.MIN_CASHIER_ID; i < UserManager.MAX_CASHIER_ID - 1; i++) {
//            cashierId = String.format("UW%07d", i);
//            cashierName = String.format("Cashier #%d", i);
//            cashierEmail = String.format("cashier%d@upm.es", i);
//            result = UserManager.getInstance().addCashier(cashierId, cashierName, cashierEmail);
//            assertNotNull(result);
//        }
//        result = UserManager.getInstance().addCashier("Cashier #9999998", "cashier9999998@upm.es");
//        assertNotNull(result);
//        // No more cashiers
//        result = UserManager.getInstance().addCashier("Overfilled", "overfilled@upm.es");
//        assertNotNull(result);
//    }
//
//    // If for what reason the nextCashierId broke and is unavailable, make sure that generation by iteration works
//    @Test
//    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
//    void fillCashierWithExistingMaxIdEdgeCaseTest2() {
//        // Render auto-increment useless
//        for (int i = UserManager.MIN_CASHIER_ID; i <= UserManager.MAX_CASHIER_ID; i++) {
//            String generatedId = UserManager.getInstance().generateUniqueCashierId();
//            assertEquals(String.format("%07d", i), generatedId.substring(2));
//        }
//
//        // Fill until last cashier
//        String cashierId;
//        String cashierName;
//        String cashierEmail;
//        Cashier result = null;
//        for (int i = UserManager.MIN_CASHIER_ID; i < UserManager.MAX_CASHIER_ID; i++) {
//            cashierId = String.format("UW%07d", i);
//            cashierName = String.format("Cashier #%d", i);
//            cashierEmail = String.format("cashier%d@upm.es", i);
//            result = UserManager.getInstance().addCashier(cashierId, cashierName, cashierEmail);
//            assertNotNull(result);
//        }
//
//        // Add last cashier
//        result = UserManager.getInstance().addCashier("Cashier #9999999", "cashier9999999@upm.es");
//        assertNotNull(result);
//        // No more cashiers
//        result = UserManager.getInstance().addCashier("Overfilled", "overfilled@upm.es");
//        assertNull(result);
//    }
//
//    @Test
//    void removeNonExistentCashier() {
//        normalAddCashier();
//        boolean result = UserManager.getInstance().removeCashier("UW0000001");
//        assertNotNull(result);
//    }
//
//    @Test
//    void removeInvalidIdCashier() {
//        normalAddCashier();
//        boolean result = UserManager.getInstance().removeCashier("UX0000000");
//        assertNotNull(result);
//        result = UserManager.getInstance().removeCashier("UW00000000");
//        assertNotNull(result);
//    }
//
//    @Test
//    void removeCashier() {
//        normalAddCashier();
//        boolean result = UserManager.getInstance().removeCashier("UW0000000");
//        assertTrue(result);
//    }
//
//    @Test
//    void findNonExistentCashier() {
//        normalAddCashier();
//        Cashier cashier = UserManager.getInstance().findCashier("UW0000001");
//        assertNull(cashier);
//    }
//
//    @Test
//    void findInvalidIdCashier() {
//        normalAddCashier();
//        Cashier cashier = UserManager.getInstance().findCashier("UX0000000");
//        assertNull(cashier);
//        cashier = UserManager.getInstance().findCashier("UW00000000");
//        assertNull(cashier);
//    }
//
//    @Test
//    void findCashier() {
//        normalAddCashier();
//        Cashier cashier = UserManager.getInstance().findCashier("UW0000000");
//        assertNotNull(cashier);
//        assertEquals("UW0000000", cashier.getId());
//    }
//
//    @Test
//    void addOneClient() {
//        UserManager.getInstance().addCashier("Cashier 0", "cashier0@upm.es");
//        Cashier cashier = UserManager.getInstance().findCashier("UW0000000");
//        Client result = UserManager.getInstance().addClient(testNIE, "Client 0", "client1@example.com", cashier);
//        assertNotNull(result);
//    }
//
//    @Test
//    void addClientSameId() {
//        UserManager.getInstance().addCashier("Cashier 0", "cashier0@upm.es");
//        Cashier cashier = UserManager.getInstance().findCashier("UW0000000");
//        Client result = UserManager.getInstance().addClient(testNIE, "Client 1", "client1@example.com", cashier);
//        assertNotNull(result);
//        result = UserManager.getInstance().addClient(testNIE, "Client 2", "client2@example.com", cashier);
//        assertNull(result);
//    }
//
//    @Test
//    void removeClient() {
//        addOneClient();
//        boolean result = UserManager.getInstance().removeClient(testNIE);
//        assertTrue(result);
//    }
//
//    @Test
//    void removeClientNonExistent() {
//        addOneClient();
//        boolean result = UserManager.getInstance().removeClient("X0000000X");
//        assertFalse(result);
//    }
//
//    @Test
//    void findNonExistentClient() {
//        addOneClient();
//        Client client = UserManager.getInstance().findClient("00000000X");
//        assertNull(client);
//    }
//
//    @Test
//    void findClient() {
//        addOneClient();
//        Client client = UserManager.getInstance().findClient(testNIE);
//        assertNotNull(client);
//        assertEquals(testNIE, client.getId());
//    }
//
//    @Test
//    void listClientEmpty() {
//        Client[] clients = UserManager.getInstance().listClients();
//        assertEquals(0, clients.length);
//    }
//
//    @Test
//    void listClient() {
//        addOneClient();
//        Client[] clients = UserManager.getInstance().listClients();
//        assertEquals(1, clients.length);
//    }
//
//    @Test
//    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
//    void fillCashierTicket() {
//        normalAddCashier();
//        Ticket result = null;
//        Cashier cashier = UserManager.getInstance().findCashier("UW0000000");
//        for (int i = UserManager.MIN_TICKET_ID; i <= UserManager.MAX_TICKET_ID; i++) {
//            result = cashier.createTicket(UserManager.getInstance().generateUniqueTicketId());
//            assertNotNull(result);
//        }
//        assertNull(UserManager.getInstance().generateUniqueTicketId());
//    }
//
//    @Test
//    @Disabled("This test will take a LOOOOOOOONG LOOOOOOONG TIIIIIIMEEEE (Cue the Sakeru Gumi's Ad music)")
//    void fillCashierTicketLastTicketWithIterationOnly() {
//        normalAddCashier();
//        // Render auto-increment useless, and create the rest of the ticket wicked fast
//        Ticket result = null;
//        Cashier cashier = UserManager.getInstance().findCashier("UW0000000");
//        for (int i = UserManager.MIN_TICKET_ID; i < UserManager.MAX_TICKET_ID; i++) {
//            int generatedId = UserManager.getInstance().generateUniqueTicketId();
//            result = cashier.createTicket(generatedId);
//			assertNotNull(result);
//            assertEquals(i, generatedId);
//        }
//        UserManager.getInstance().generateUniqueCashierId();
//
//        // This should generate 99999
//        result = cashier.createTicket(UserManager.getInstance().generateUniqueTicketId());
//        assertNotNull(result);
//        // Overfilled should return null
//        Integer overfilled = UserManager.getInstance().generateUniqueTicketId();
//        assertNull(overfilled);
//        assertEquals(UserManager.MAX_TICKET_ID - UserManager.MIN_TICKET_ID + 1, cashier.getCreatedTicketAmount());
//    }
//}
