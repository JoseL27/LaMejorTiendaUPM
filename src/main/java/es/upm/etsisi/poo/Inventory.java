package es.upm.etsisi.poo;

import java.util.HashMap;
import java.util.Map;

public class Inventory
{
    // Inventory implementation goes here
    private Map<Integer, Product> products;

    public Inventory()
    {
        products = new HashMap<>( 200 );
    }

    public boolean addProduct ( Product product )
    {
        if ( products.containsKey ( product.id ) )
        {
            return false; // Product already exists
        }
        products.put ( product.id, product );
        return true;
    }

    public boolean removeProduct ( Product product )
    {
        if ( products.containsKey ( product.id ) )
        {
            products.remove ( product.id );
            return true;
        }
        return false; // Product not found
    }

    public Product getProductById ( int id )
    {
        return products.get ( id ); // Returns null if not found
    }


}
