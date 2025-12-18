/* date = December 14th 2025 10:52 pm */

package es.upm.etsisi.test;

import es.upm.etsisi.poo.Client;
import es.upm.etsisi.poo.Cashier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;

public class ClientTest {
    
    @Test
        void idValidationNIF() {
        assertTrue(Client.isValidId("60860897E"));
        assertTrue(Client.isValidId("89650658Q"));
        assertTrue(Client.isValidId("79631068P"));
        assertTrue(Client.isValidId("10454534E"));
        assertTrue(Client.isValidId("83597236X"));
        
        assertFalse(Client.isValidId("60860897F"));
        assertFalse(Client.isValidId("89650658D"));
        assertFalse(Client.isValidId("79631068C"));
        assertFalse(Client.isValidId("10454534B"));
        assertFalse(Client.isValidId("83597236A"));
    }
    
    @Test
        void idValidationNIE() {
        assertTrue(Client.isValidId("X0586929S"));
        assertTrue(Client.isValidId("X9357778K"));
        assertTrue(Client.isValidId("X4234859F"));
        assertTrue(Client.isValidId("X3490423N"));
        assertTrue(Client.isValidId("Z6949267Y"));
        
        assertFalse(Client.isValidId("X0586929A"));
        assertFalse(Client.isValidId("X9357778B"));
        assertFalse(Client.isValidId("X4234859C"));
        assertFalse(Client.isValidId("X3490423D"));
        assertFalse(Client.isValidId("Z6949267E"));
    }
    
    @Disabled // NOTE(erb): for future CompanyClients
        @Test
        void idValidationCIF() {
        assertTrue(Client.isValidId("Q5256608J"));
        assertTrue(Client.isValidId("A14155667"));
        assertTrue(Client.isValidId("R6554800J"));
        assertTrue(Client.isValidId("E80206790"));
        assertTrue(Client.isValidId("W9785496B"));
        
        assertFalse(Client.isValidId("Q5256608J"));
        assertFalse(Client.isValidId("A14155667"));
        assertFalse(Client.isValidId("R6554800J"));
        assertFalse(Client.isValidId("E80206790"));
        assertFalse(Client.isValidId("W9785496B"));
    }
    
    
    @Test
        void toStringFormat() {
        Cashier cs = new Cashier("0", "andres", "andres@sevilla.com");
        Client cl = new Client("0", "jose", "josqlito@correo.com", cs);
        
        String expected = String.format("Client{identifier='%s', name='%s', email='%s', cash=%s}",
                                        cl.getId(), cl.getName(), cl.getEmail(), cs.getId());
        assertEquals(expected, cl.toString());
    }
    
}
