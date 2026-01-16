package es.upm.etsisi.poo;

import java.io.Serializable;

public abstract class User implements Serializable {
    private String id;
    private String name;
    private String email;
    
    public User(String id, String name, String email){
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
}