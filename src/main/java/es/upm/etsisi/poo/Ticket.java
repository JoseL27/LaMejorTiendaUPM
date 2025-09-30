package es.upm.etsisi.poo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Set;

public class Ticket
{
	private final Map <Product, AtomicInteger> cart;
	private final Map <Product.Category, AtomicInteger> counter;

	public Ticket ()
	{
		cart = new HashMap <Product, AtomicInteger> ( 100 );
		counter = new HashMap <Product.Category, AtomicInteger> ();
	}

	public boolean addProduct(Product product, int amount)
	{
		if ( cart.containsKey ( product ) )
		{
			cart.get ( product ).addAndGet ( amount );
			print ();
			return true;
		}
		else if ( amount > 0 )
		{
			cart.put ( product, new AtomicInteger ( amount ) );
			print ();
			return true;
		}
		return false;
	}
	
	public boolean removeProduct(Product product)
	{
		if ( cart.containsKey ( product ) )
		{
			cart.remove ( product );
			print ();
			return true;
		}
		return false;
	}

	/*public TicketProduct[] getProducts()
	{
		//neccesary?? Nope.
		return null;
	}*/

	private void categoryCounter()
	{
		for ( Product.Category c : Product.Category.values() )
		{
			counter.put ( c, new AtomicInteger ( 0 ) );
		}
		for ( Product p : cart.keySet() )
		{
			counter.get ( p.category () ).addAndGet ( cart.get ( p ).get() );
		}
	}

	public void print ()
	{
		final Set <Product> products = cart.keySet();
		final Product[] productsArray = products.toArray ( new Product[0] );
		Arrays.sort ( productsArray );
		categoryCounter ();
		double total = 0.0;
		double discount = 0.0;
		// inicialize counters

		for ( Product p : productsArray )
		{
			int amount = cart.get ( p ).get();
			final double price = p.price ( amount );
			for ( int i = 0; i < amount; i++ )
			{
				System.out.print ( p.toString () );
				final double disc = p.category().getDiscountPercent();
				if ( counter.get ( p.category () ).get () >= 2  && disc > 0 )
				{
					System.out.print ( "**discount -" + ( disc * 100 ) );
				}
			}
			if ( counter.get ( p.category() ).get() >= 2 )
			{
				discount += price * p.category().getDiscountPercent();
			}
			total += price;
		}
		System.out.println ( "Total price: " + total );
		System.out.println ( "Discount: " + discount );
		System.out.println ( "Final Price: " + ( total - discount ) );
	}


	
	
	// constructor
	// getters y setters, esto no es POO, es una chapuza
}
