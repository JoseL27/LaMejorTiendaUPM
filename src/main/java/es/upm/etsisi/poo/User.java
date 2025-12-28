package es.upm.etsisi.poo;

public abstract class User {
    private String id;
    private String name;

    public User(String id, String name, String email) throws IllegalArgumentException{
        if (id == null) throw new IllegalArgumentException("User id can not be null");
        if (name == null) throw new IllegalArgumentException("User name can not be null");
        if (email == null) throw new IllegalArgumentException("User email can not be null");
        this.id = id;
        this.name = name;
    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }


    @Override
    public boolean equals(Object obj) {
        if ( !(obj instanceof User) )
            return false;
        User other = (User)obj;
        return this.id.equals(other.id); // ID should be a globally unique identifier for any User (Client/Cashier/etc)
    }
}
