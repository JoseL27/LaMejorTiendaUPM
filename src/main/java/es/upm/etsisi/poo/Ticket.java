package es.upm.etsisi.poo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Set;

public class Ticket {
	private class ProductInfo implements Comparable<ProductInfo> {
		Product product;
		int amount;

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
		public int compareTo(ProductInfo toCompare) {
			return product.getName().compareTo(toCompare.getProduct().getName());
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
	
	public boolean removeProduct(int id)
	{
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

		int total = 0;
		int totalDiscount = 0;
		Arrays.sort(productInfos, 0, count-1);

		for (int productInfoIndex = 0; productInfoIndex < count; productInfoIndex++) {
			ProductInfo productInfo = productInfos[productInfoIndex];
			Product product = productInfo.getProduct();
			
			float productDiscount = (productInfo.getAmount() > 1) 
				? productInfo.getAmount() * product.getCategory().getDiscountPercent() : 0;

			total += productInfo.getAmount() * product.getPrice();
			totalDiscount += productDiscount;

			for (int productCounter = 0; productCounter < productInfo.getAmount(); productCounter++) {
				sb.append(String.format("{class:Product, id:%d, name:'%s', category:%s, price:%.1f}", 
										product.getId(), product.getName(), product.getCategory(), product.getPrice()));
				
				if (productDiscount > 0) { 
					sb.append(String.format(" **discount: -%.1f", productDiscount));
				}
				sb.append("\n");
			}
		}
		
		sb.append(String.format("Total price: %.1f\n", (float)total));
		sb.append(String.format("Total discount: %.1f\n", (float)totalDiscount));
		sb.append(String.format("Final Price: %.1f\n", (float)(total - totalDiscount)));
		return sb.toString();
	}
}
