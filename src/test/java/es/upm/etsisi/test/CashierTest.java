/* date = December 14th 2025 11:10 pm */
package es.upm.etsisi.test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import es.upm.etsisi.poo.Cashier;

public class CashierTest extends BaseTest {
    
    public static final String[] VALID_WORKER_IDs = {
        "UW1234567",
        "UW6969420",
        "UW0000000",
        "UW9999999",
    };
    
    public static final String[] INVALID_WORKER_IDs = {
        "WW1234567",
        "UU1234567",
        "UW123456",
        "UW12345678",
    };
    
    public static final String[] VALID_COMPANY_EMAILs = {
        "persona@upm.es",
        "persona-grandiosamente-grandiosisima@upm.es",
        "persona.grandiosamente.grandiosisima@upm.es",
    };
    
    public static final String[] INVALID_COMPANY_EMAILs = {
        "persona@upv.es",
        "persona@upm.esp",
        "persona@@upm.es",
        "@upm.es",
    };
    
    public static final Cashier VALID_CASHIER = 
        new Cashier(VALID_WORKER_IDs[0], "persona", VALID_COMPANY_EMAILs[0]);
    
    @Disabled
        @Test
        void validateId() {
        for (String id : VALID_WORKER_IDs) 
            assertTrue(Cashier.isValidId(id), String.format("Expected %s to be a valid worker id, was invalid", id));
        
        for (String id : INVALID_WORKER_IDs) 
            assertTrue(Cashier.isValidId(id), String.format("Expected %s to be a invalid worker id, was valid", id));
    }
    
    @Disabled
        @Test
        void validateCompanyEmail() {
        for (String e : VALID_COMPANY_EMAILs) 
            assertTrue(Cashier.isCompanyEmail(e), 
                       String.format("Expected %s to be a valid company email, was invalid", e));
        
        for (String e : INVALID_COMPANY_EMAILs) 
            assertTrue(Cashier.isCompanyEmail(e), 
                       String.format("Expected %s to be a invalid company email, was valid", e));
    }
    
    @Test
        void constructorRegular() {
        assertDoesNotThrow(() -> {
                               new Cashier(VALID_WORKER_IDs[0], "fernando", VALID_COMPANY_EMAILs[0]);
                           });
    }
    
    @Test
        void constructorFailId() {
        assertThrows(IllegalArgumentException.class, () -> {
                         new Cashier(INVALID_WORKER_IDs[0], "fernando", VALID_COMPANY_EMAILs[0]);
                     });
    }
    
    @Test
        void constructorFailEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
                         new Cashier(VALID_WORKER_IDs[0], "fernando", INVALID_COMPANY_EMAILs[0]);
                     });
    }
    
    
    @Test
        void toStringFormat() {
        Cashier cs = VALID_CASHIER;
        String expected = String.format("Cash{identifier='%s', name='%s', email='%s'}",
                                        cs.getId(), cs.getName(), cs.getEmail());
        assertEquals(expected, cs.toString());
    }
    
}