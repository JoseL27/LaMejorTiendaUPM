package es.upm.etsisi.poo;

public class Product
{
	
	public enum Category
	{
		MERCH		(0.00f),
		PAPELERIA	(0.05f),
		ROPA		(0.07f),
		LIBRO		(0.01f),
		ELECTRONICA	(0.03f);

		private final float discountPercent;

		Category(float discountPercent)
		{
			this.discountPercent = discountPercent;
		}

		public  float getDiscountPercent() 
		{
			return discountPercent;
		}
	}
	
	public final int id;
	private String name;
	private Category category;
	private double price;

	// constructor
	public Product ( int id, String name, Category category, double price )
	{
		this.id = id;
		this.name = name;
		this.category = category;
		this.price = price;
	}

	public int compareTo ( Product p )
	{
		return this.name.compareTo ( p.name );
	}

	public boolean  equals ( Product p )
	{
		return this.id == p.id;
	}
	// getters y setters, estos no se usan, son shit code

	public Category category ()
	{
		return this.category;
	}

	public double price ( int amount )
	{
		return this.price * amount;
	}

	@Override

	public String toString ()
	{
		return  "{class:Product, id:" + this.id +
				", name:" + this.name + ", category:" + this.category +
				", price:" + this.price + "}";
	}

}
