package es.upm.etsisi.test;
/* date = December 24th 2025 7:40 pm */

import es.upm.etsisi.poo.UserManager;
import es.upm.etsisi.poo.Serialize;
import es.upm.etsisi.poo.Inventory;

import java.util.Locale;
import java.util.Map;
import java.time.LocalDateTime;
import java.lang.reflect.Field;
import java.io.Serializable;

import org.junit.jupiter.api.*;

public class BaseTest { 
	// DICTATOR LOCALE 
    @BeforeAll
		public static void setEnUSLocale() {
        Locale.setDefault(new Locale("en", "US"));
    }
    
    @AfterAll
		public static void unsetEnUSLocale() {
        Locale.setDefault(Locale.getDefault());
    }
    
    @BeforeEach
		public void clearSingletons() {
        try {
            Field f; 
            
            f = Inventory.class.getDeclaredField("instance");
            f.setAccessible(true);
            f.set(null, null);
            
            f = UserManager.class.getDeclaredField("instance");
            f.setAccessible(true);
            f.set(null, null);
			
			f = Serialize.class.getDeclaredField("dataStore");
			f.setAccessible(true);
			Map<String, Serializable> dataStore = (Map<String, Serializable>)f.get(null);
			dataStore.clear();
            
        } catch (Exception e) {
        }
    }
}
