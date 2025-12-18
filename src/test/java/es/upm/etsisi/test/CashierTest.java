/* date = December 14th 2025 11:10 pm */
package es.upm.etsisi.test;

import static org.junit.jupiter.api.Assertions.*;

import es.upm.etsisi.poo.Cashier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

public class CashierTest {
    
    @Disabled
        @Test
        void validateId() {
        assertTrue(Cashier.isValidId("UW1234567"));
        assertTrue(Cashier.isValidId("UW0000000"));
        assertTrue(Cashier.isValidId("UW9999999"));
        
        assertFalse(Cashier.isValidId("WW1234567"));
        assertFalse(Cashier.isValidId("UU1234567"));
        assertFalse(Cashier.isValidId("UW123456"));
        assertFalse(Cashier.isValidId("UW12345678"));
    }
    
    @Disabled
        @Test
        void validateCompanyEmail() {
        assertTrue(Cashier.isCompanyEmail("persona@upm.es"));
        assertTrue(Cashier.isCompanyEmail("persona-grandiosamente-grandiosisima@upm.es"));
        assertTrue(Cashier.isCompanyEmail("persona.grandiosamente.grandiosisima@upm.es"));
        
        assertFalse(Cashier.isCompanyEmail("persona@upv.es"));
        assertFalse(Cashier.isCompanyEmail("persona@upm.esp"));
        assertFalse(Cashier.isCompanyEmail("persona@@upm.es"));
        assertFalse(Cashier.isCompanyEmail("@upm.es"));
    }
    
    @Test
        void toStringFormat() {
        Cashier cs = new Cashier("0", "pierna", "cabeza@upm.es");
        
        String expected = String.format("Cash{identifier='%s', name='%s', email='%s'}",
                                        cs.getId(), cs.getName(), cs.getEmail());
        assertEquals(expected, cs.toString());
    }
}
