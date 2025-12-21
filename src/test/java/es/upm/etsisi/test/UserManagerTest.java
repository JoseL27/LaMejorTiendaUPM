package es.upm.etsisi.test;

import static org.junit.jupiter.api.Assertions.*;

import es.upm.etsisi.poo.exceptions.MissingItemException;
import es.upm.etsisi.poo.exceptions.DuplicateItemException;
import es.upm.etsisi.poo.exceptions.DataException;
import es.upm.etsisi.poo.Cashier;
import es.upm.etsisi.poo.Client;
import es.upm.etsisi.poo.UserManager;
import es.upm.etsisi.poo.Ticket;
import org.junit.jupiter.api.*;
import java.lang.reflect.Field;
import java.util.Locale;


public class UserManagerTest {
	private final String testNIE = ClientTest.VALID_NIEs[0];
    
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
            Field f = UserManager.class.getDeclaredField("instance");
            f.setAccessible(true);
            f.set(null, null);
        } catch (Exception e) { }
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
        assertDoesNotThrow(() -> { UserManager.getInstance().addCashier("UW0000000", "Cajero1", "cajero1@upm.es"); });
    }
    
    @Test
        void addCashierWithExistingId() {
        assertDoesNotThrow(() -> {
                               UserManager.getInstance().addCashier("UW0000000", "Cajero1", "cajero1@upm.es");
                           });
        
        assertThrows(DataException.class, () -> {
                         UserManager.getInstance().addCashier("UW0000000", "Cajero2", "cajero2@upm.es");
                     });
    }
    
    @Test
        void addCashierWithInvalidId() {
        assertThrows(DataException.class, () -> {
                         UserManager.getInstance().addCashier("UX0000000", "Cajero0", "cajero0@upm.es");
                     });
        
        assertThrows(DataException.class, () -> {
                         UserManager.getInstance().addCashier("UW000000100", "Cajero0", "cajero0@upm.es");
                     });
    }
    
    @Test
        void removeNonExistentCashier() {
        normalAddCashier();
        assertThrows(MissingItemException.class, () -> { UserManager.getInstance().removeCashier("UW0000001"); });
    }
    
    @Test
        void removeCashier() {
        normalAddCashier();
        assertDoesNotThrow(() -> { UserManager.getInstance().removeCashier("UW0000000"); });
    };
    
    @Test
        void findNonExistentCashier() {
        normalAddCashier();
        assertThrows(MissingItemException.class, () -> { UserManager.getInstance().findCashier("UW0000001"); });
    }
    
    @Test
        void findCashier() {
        normalAddCashier();
        assertDoesNotThrow(() -> { UserManager.getInstance().findCashier("UW0000000"); });
    }
    
    @Test
        void addOneClient() {
        assertDoesNotThrow(() -> { 
                               UserManager.getInstance().addCashier("Cashier 0", "cashier0@upm.es");
                               Cashier cashier = UserManager.getInstance().findCashier("UW0000000");
                               UserManager.getInstance().addClient(testNIE, "Client 0", "client1@example.com", "UW0000000"); 
                           });
    }
    
    @Test
        void addClientSameId() {
        assertDoesNotThrow(() -> {
                               UserManager.getInstance().addCashier("Cashier 0", "cashier0@upm.es");
                               UserManager.getInstance().addClient(testNIE, "Client 1", "client1@example.com", "UW0000000");
                           });
        assertThrows(DuplicateItemException.class, () -> {
                         UserManager.getInstance().addClient(testNIE, "Client 2", "client2@example.com", "UW0000000");
                     });
    }
    
    @Test
        void removeClient() {
        addOneClient();
        assertDoesNotThrow(() -> {
                               UserManager.getInstance().removeClient(testNIE);
                           });
    }
    
    @Test
        void removeClientNonExistent() {
        addOneClient();
        assertThrows(MissingItemException.class, () -> {
                         UserManager.getInstance().removeClient("X0000000X");
                     });
    }
    
    @Test
        void findNonExistentClient() {
        addOneClient();
        
        assertThrows(MissingItemException.class, () -> { UserManager.getInstance().findClient("00000000X"); });
    }
    
    @Test
        void findClient() {
        addOneClient();
        assertDoesNotThrow(() -> { 
                               Client client = UserManager.getInstance().findClient(testNIE);
                               assertEquals(testNIE, client.getId()); 
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
}
