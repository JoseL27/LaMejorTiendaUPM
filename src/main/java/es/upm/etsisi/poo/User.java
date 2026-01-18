package es.upm.etsisi.poo;

import es.upm.etsisi.poo.exceptions.InvalidDataException;
import java.io.Serializable;

public abstract class User implements Serializable {
    private String id;
    private String name;
    private String email;
    
    public User(String id, String name, String email) throws InvalidDataException {
		
		if (!isValidEmail(email)) {
            throw new InvalidDataException("Invalid user email: '" + email + "'");
		}
		
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    public String getId(){
        return this.id;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getEmail(){
        return this.email;
    }
    
    @Override
        public boolean equals(Object obj) {
        if ( !(obj instanceof User) )
            return false;
        User other = (User)obj;
        return this.id.equals(other.id); // ID should be a globally unique identifier for any User (Client/Cashier/etc)
    }
	
	public static boolean isValidEmail(String email) {
		boolean result = false;
		
		String[] splitEmail = email.split("@");
		
		if (splitEmail.length == 2) {
			result = splitEmail[0].length() > 0 && 
				splitEmail[1].length() > 0;
		}
		return result;
	}
}