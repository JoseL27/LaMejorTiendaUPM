package es.upm.etsisi.test;
/* date = December 24th 2025 7:40 pm */

import es.upm.etsisi.poo.UserManager;
import es.upm.etsisi.poo.Inventory;

import java.util.Locale;
import java.time.LocalDateTime;
import java.lang.reflect.Field;

import org.junit.jupiter.api.*;

public class BaseTest { 
	// DICTATOR LOCALE 
    @BeforeAll
        private static void setEnUSLocale() {
        Locale.setDefault(new Locale("en", "US"));
    }
    
    @AfterAll
        private static void unsetEnUSLocale() {
        Locale.setDefault(Locale.getDefault());
    }
    
    @BeforeEach
        private void clearSingletons() {
        try {
            Field f; 
            
            f = Inventory.class.getDeclaredField("instance");
            f.setAccessible(true);
            f.set(null, null);
            
            f = UserManager.class.getDeclaredField("instance");
            f.setAccessible(true);
            f.set(null, null);
            
        } catch (Exception e) {
        }
    }
}
