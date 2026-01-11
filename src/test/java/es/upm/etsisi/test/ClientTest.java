/* date = December 14th 2025 10:52 pm */

package es.upm.etsisi.test;

import es.upm.etsisi.poo.Client;
import es.upm.etsisi.poo.Cashier;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ClientTest extends BaseTest { 
    
    public static final String[] VALID_DNIs = {
        // DNI 
        "57239779R",
        "02923616V",
        "25121976L",
        "57473455C",
        "91789240N",
        "54739566A",
        "79875974X",
        "52570866L",
        "18845721G",
        "74801099Q",
        
        // NIE
        "Z8360655E",
        "Y3794022W",
        "Y1255162C",
        "Z0561282H",
        "Z2991017X",
        "X6590282T",
        "Y3058924F",
        "Z2280808H",
        "Y0053726N",
    };
    
    
    public static final String[] INVALID_DNIs = {
        // DNI
        "5729779R",
        "02923616H",
        "25121176L",
        "57473455D",
        "91789840N",
        "5473956S6A",
        "798759788X",
        "52570866B",
        "18845721A",
        "74801099G",
        
        // NIE
        "A8360655E",
        "Y3794022G",
        "Y1255162D",
        "Z0561282D",
        "Z29910177X",
        "X659028T",
        "Y3058928F",
        "Z2280845H",
        "Y0053726Z",
    };
    
    public static final String[] VALID_NIFs = {
        "P6377243H",
        "E96771019",
        "V53380069",
        "E07092802",
        "V30135057",
        "C16481228",
        "Q2988229G",
        "H82048869",
    };
    
    public static final String[] INVALID_NIFs = {
        "HH6377243H",
        "E9677101D",
        "V5338006A",
        "E07092803",
        "V30135059",
        "C164812219",
        "Q298822G",
        "H820488A9",
    };
    
    public static final Client VALID_CLIENT = 
        new Client(VALID_NIFs[0], "jose", "josqlito@correo.com", CashierTest.VALID_CASHIER);
    
    void testClientId(String id, Client.IdType expectedIdType) {
        Client.IdType idType = Client.getIdType(id);
        if (idType != expectedIdType) {
            String expStr = expectedIdType != null ? expectedIdType.toString() : "null";
            fail(String.format("Expected '%s' to be a %s, was %s\n", id, expStr, idType.toString()));
        }
    }
    
    @Test
        void idValidationDNI() {
        for (String id : VALID_DNIs) 
            testClientId(id, Client.IdType.DNI);
        
        for (String id : INVALID_DNIs) 
            testClientId(id, null);
    }
    
    @Test
        void idValidationNIF() {
        for (String id : VALID_NIFs) 
            testClientId(id, Client.IdType.NIF);
        
        for (String id : INVALID_NIFs) 
            testClientId(id, null);
    }
    
    @Test
        void constructorRegular() {
        assertDoesNotThrow(() -> {
                               new Client(VALID_NIFs[0], "jose", "josqlito@correo.com", CashierTest.VALID_CASHIER);
                               new Client(VALID_DNIs[0], "jose", "josqlito@correo.com", CashierTest.VALID_CASHIER);
                           });
    }
    
    @Test
        void constructorFailIdDNI() { 
        assertThrows(IllegalArgumentException.class, () -> {
                         new Client(INVALID_DNIs[0], "jose", "josqlito@correo.com", CashierTest.VALID_CASHIER);
                     });
        
    }
    
    
    @Test
        void constructorFailIdNIF() { 
        assertThrows(IllegalArgumentException.class, () -> {
                         new Client(INVALID_NIFs[0], "jose", "josqlito@correo.com", CashierTest.VALID_CASHIER);
                     });
    }
    
    @Test
        void constructorFailNullCashier() { 
        assertThrows(IllegalArgumentException.class, () -> {
                         new Client(VALID_DNIs[0], "jose", "josqlito@correo.com", null);
                     });
        
    }
    
    @Test
        void toStringFormat() {
        Cashier cs = new Cashier("UW9999999", "andres", "andres@upm.es");
        Client cl = new Client("X0586929S", "jose", "josqlito@correo.com", cs);
        
        String expected = String.format("USER{identifier='%s', name='%s', email='%s', cash=%s}",
                                        cl.getId(), cl.getName(), cl.getEmail(), cs.getId());
        assertEquals(expected, cl.toString());
    }
    
}
