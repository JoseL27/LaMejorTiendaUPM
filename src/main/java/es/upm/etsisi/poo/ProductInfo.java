package es.upm.etsisi.poo;
import java.util.Arrays;

public class ProductInfo implements Comparable<ProductInfo> {
	/**
	 * ProductInfo struct-like holder as a Product pair.
	 * Has basic constructors and getters.
	 * Implements 'Comparable' class to alfabetically order products as in the requirement document.
	 */

	private Product product;
	private String[] personalizations;
		
	public ProductInfo(Product product, String[] personalizations) {
		this.product = product;
		this.personalizations = personalizations;
	}

	public Product getProduct() { 
		return this.product;
	}
	
	public String[] getPersonalizations() { 
		return this.personalizations;
	}

	public boolean duplicateOf(ProductInfo other) {
		if (other == null) return false;
		return (this.product == null && other.product == null) 
			|| ((this.product != null && this.product.duplicateOf(other.product))
				&& Arrays.equals(this.personalizations, other.personalizations));
	}

	/**
	 * Compares this to another ProductInfo based on name only
	 * @param other the ProductInfo to be compared.
	 * @return A value less than 0 if this is lesser, 0 if they are equal, and a value greater than if this is greater
	 */
	@Override
	public int compareTo(ProductInfo other) {
		return this.product.getName().compareTo(other.product.getName());
	}
}
