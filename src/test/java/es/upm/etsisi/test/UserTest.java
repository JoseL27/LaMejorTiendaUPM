/* date = January 18th 2026 8:43 am */
package es.upm.etsisi.test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import es.upm.etsisi.poo.User;
import es.upm.etsisi.poo.exceptions.*;

public class UserTest extends BaseTest {
	
    public static final String[] VALID_EMAILs = {
        "persona@email.es",
        "a@b",
        "persona-grandiosamente-grandiosisima@gmail.es",
        "persona.grandiosamente.grandiosisimalaksjdfa;sdlfkj a;sldkfj as;dlfkjas;dlkfj@some.cool.email.com.es",
    };
    
    public static final String[] INVALID_EMAILs = {
        "a@",
        "@",
        "a@@upm.es",
        "@com.es",
        "abababa@",
    };
	
	
	@Test
        void validateCompanyEmail() {
        for (String e : VALID_EMAILs) 
            assertTrue(User.isValidEmail(e), 
                       String.format("Expected %s to be a valid user email, was invalid", e));
        
        for (String e : INVALID_EMAILs) 
            assertFalse(User.isValidEmail(e), 
						String.format("Expected %s to be a invalid user email, was valid", e));
    }
	
}
