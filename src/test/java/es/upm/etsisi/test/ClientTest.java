/* date = December 14th 2025 10:52 pm */

package es.upm.etsisi.test;

import es.upm.etsisi.poo.Client;
import es.upm.etsisi.poo.Cashier;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ClientTest extends BaseTest { 
    
    public static final String[] VALID_NIFs = {
        "60860897E",
        "89650658Q",
        "79631068P",
        "10454534E",
        "83597236X",
    };
    
    public static final String[] INVALID_NIFs = {
        "60860897F",
        "89650658D",
        "79631068C",
        "10454534B",
        "83597236A",
    };
    
    
    public static final String[] VALID_NIEs = {
        "X0586929S",
        "X9357778K",
        "X4234859F",
        "X3490423N",
        "Z6949267Y",
    };
    
    
    public static final String[] INVALID_NIEs = {
        "X0586929A",
        "X9357778B",
        "X4234859C",
        "X3490423D",
        "Z6949267E",
    };
    
    public static final String[] VALID_CIFs = {
        "A14155667",
        "R6554800J",
        "E80206790",
        "W9785496B",
        "Q5256608J",
    };
    
    public static final String[] INVALID_CIFs = {
        "Q5256608A",
        "A1415566B",
        "R6554800C",
        "D80206790",
        "W9785496F",
    };
    
    public static final Client VALID_CLIENT = 
        new Client(VALID_NIFs[0], "jose", "josqlito@correo.com", CashierTest.VALID_CASHIER);
    
    void testClientId(String id, String tag, boolean expected) {
        if (Client.isValidId(id) != expected) {
            String expStr = expected ? "valid" : "invalid";
            String wasStr = !expected ? "valid" : "invalid";
            fail(String.format("Expected '%s' to be %s %s, was %s\n", id, expStr, tag, wasStr));
        }
    }
    
    @Test
        void idValidationNIF() {
        for (String id : VALID_NIFs) 
            testClientId(id, "nif", true);
        
        for (String id : INVALID_NIFs) 
            testClientId(id, "nif", false);
    }
    
    @Test
        void idValidationNIE() {
        for (String id : VALID_NIEs) 
            testClientId(id, "nie", true);
        
        for (String id : INVALID_NIEs) 
            testClientId(id, "nie", false);
    }
    
    @Disabled // NOTE(erb): for future CompanyClients
        @Test
        void idValidationCIF() {
        for (String id : VALID_CIFs) 
            testClientId(id, "nie", true);
        
        for (String id : INVALID_CIFs) 
            testClientId(id, "nie", false);
    }
    
    @Test
        void constructorRegular() {
        assertDoesNotThrow(() -> {
                               new Client(VALID_NIFs[0], "jose", "josqlito@correo.com", CashierTest.VALID_CASHIER);
                               
                               new Client(VALID_NIEs[0], "jose", "josqlito@correo.com", CashierTest.VALID_CASHIER);
                               
                               /* 
                                new Client(VALID_CIFs[0], "jose", "josqlito@correo.com", CashierTest.VALID_CASHIER);
                                */
                           });
    }
    
    @Test
        void constructorFailIdNIF() { 
        assertThrows(IllegalArgumentException.class, () -> {
                         new Client(INVALID_NIFs[0], "jose", "josqlito@correo.com", CashierTest.VALID_CASHIER);
                     });
        
        
    }
    
    @Test
        void constructorFailIdNIE() { 
        assertThrows(IllegalArgumentException.class, () -> {
                         new Client(INVALID_NIEs[0], "jose", "josqlito@correo.com", CashierTest.VALID_CASHIER);
                     });
        
    }
    
    
    @Disabled
        @Test
        void constructorFailIdCIF() { 
        assertThrows(IllegalArgumentException.class, () -> {
                         new Client(INVALID_CIFs[0], "jose", "josqlito@correo.com", CashierTest.VALID_CASHIER);
                     });
        
    }
    
    @Test
        void constructorFailNullCashier() { 
        assertThrows(IllegalArgumentException.class, () -> {
                         new Client(VALID_NIEs[0], "jose", "josqlito@correo.com", null);
                     });
        
    }
    
    @Test
        void toStringFormat() {
        Cashier cs = new Cashier("UW9999999", "andres", "andres@upm.es");
        Client cl = new Client("X0586929S", "jose", "josqlito@correo.com", cs);
        
        String expected = String.format("Client{identifier='%s', name='%s', email='%s', cash=%s}",
                                        cl.getId(), cl.getName(), cl.getEmail(), cs.getId());
        assertEquals(expected, cl.toString());
    }
    
}
