package es.upm.etsisi.poo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Set;

public class Ticket {
	private class ProductInfo implements Comparable<ProductInfo> {
		private Product product;
		private int amount;

		public ProductInfo(Product product, int amount) {
			this.product = product;
			this.amount = amount;
		}

		public Product getProduct() { 
			return this.product;
		}
		
		public int getAmount() { 
			return this.amount;
		}

		public void setProduct(Product product) { 
			this.product = product;
		}
		
		public void incrementAmount(int increment) { 
			this.amount += increment;
		}

		@Override
		public int compareTo(ProductInfo other) {
			return this.product.getName().compareTo(other.product.getName());
		}
	}

	public static final int TICKET_MAX_PRODUCTS = 100;
	
	private ProductInfo[] productInfos;
	private int count;

	public Ticket() {
		productInfos = new ProductInfo[TICKET_MAX_PRODUCTS];
		count = 0;
	}

	public boolean addProduct(Product product, int amount) {
		boolean result = false;
		ProductInfo foundProductInfo = findProductInfo(product.getId());
			
		if (foundProductInfo != null) {
			foundProductInfo.incrementAmount(amount);
		} else {
			result = appendProductInfo(new ProductInfo(product, amount));
		}

		return result;
	}
	
	public boolean removeProduct(int id) {
		int foundIndex = productInfoIndex(id);
		if (foundIndex != -1) {
			productInfos[foundIndex] = productInfos[count];
			productInfos[count]	= null;
			count--;
			return true;
		}
		return false;
	}

	private int productInfoIndex(int id) {
		int result = -1;
		int index = 0;
		while (result == -1 && index < count) {
			ProductInfo currentProductInfo = productInfos[index];
			if (currentProductInfo.getProduct().getId() == id) {
				result = index;
			}
			index++;
		}
		return index;
	}

	private ProductInfo findProductInfo(int id) {
		int index = productInfoIndex(id);
		return (index != -1) ? productInfos[index] : null;
	}

	private boolean appendProductInfo(ProductInfo productInfo) {
		if (count + 1 < productInfos.length) {
			productInfos[count] = productInfo;
			count++;
			return true;
		}
		return false;
	}

	public String summaryString()
	{
		StringBuilder sb = new StringBuilder();

		double totalPrice = 0;
		double totalDiscount = 0;
		Arrays.sort(productInfos, 0, count);

		for (int productInfoIndex = 0; productInfoIndex < count; productInfoIndex++) {
			ProductInfo productInfo = productInfos[productInfoIndex];
			Product product = productInfo.getProduct();
			
			totalPrice += productInfo.getAmount() * product.getPrice();

			boolean hasDiscount = productInfo.getAmount() > 1;

			for (int productCounter = 0; productCounter < productInfo.getAmount(); productCounter++) {
				sb.append(String.format("{class:Product, id:%d, name:'%s', category:%s, price:%.1f}", 
										product.getId(), product.getName(), product.getCategory(), product.getPrice()));
				
				if (hasDiscount) {
					double productDiscount = product.getPrice() * product.getCategory().getDiscountPercent();
					totalDiscount += productDiscount;
					sb.append(String.format(" **discount -%.1f", productDiscount));
				}
				sb.append("\n");
			}
		}
		
		sb.append(String.format("Total price: %.1f\n", (float)totalPrice));
		sb.append(String.format("Total discount: %.1f\n", (float)totalDiscount));
		sb.append(String.format("Final Price: %.1f\n", (float)(totalPrice - totalDiscount)));
		return sb.toString();
	}

	public static void main(String[] args) {
		Ticket ticket = new Ticket();
		ticket.addProduct(new Product(1, "Libro POO V2", Product.Category.BOOK, 30), 2);
		ticket.addProduct(new Product(2, "Camiseta talla:M UPM", Product.Category.CLOTHES, 15), 1);

		System.out.println(ticket.summaryString());
	}
}
