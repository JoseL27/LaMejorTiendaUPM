/* date = December 31st 2025 6:34 am */
package es.upm.etsisi.poo;

import es.upm.etsisi.poo.exceptions.DuplicateItemException;
import es.upm.etsisi.poo.exceptions.FullCollectionException;

import java.time.DateTimeException;
import java.util.Iterator;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;

public class ProductTicket extends Ticket {
	
	public ProductTicket(int id, boolean isCustomId) {
		super(id, isCustomId);
	}
	
	@Override
		public boolean validateItemKind(InventoryItem item) {
		return (item instanceof BaseProduct || item instanceof TimedProduct);
	}
	
	/**
	* Product only specific summaryString
	 * Example:
	 * $ ticket add 1 2
	 * {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
	 * {class:Product, id:1, name:'Libro POO V2', category:BOOK, price:30.0} **discount -3.0
	 * Total price: 60.0
	 * Total discount: 6.0
	 * Final Price: 54.0
	 */
	@Override
		public String summaryString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Ticket : ")
			.append(getComposedId())
			.append("\n");
		
		double totalPrice = 0;
		double totalItemDiscount = 0;
		double discountPercentOverAll = 0;
		
		ticketItems.sort(null);
		
		int[] categoriesCount = categoriesProductCount();
		
		for (TicketItem info : ticketItems) {
			double price = info.getPrice();
			double itemDiscount = info.getItemDiscount(categoriesCount);
			
			totalPrice += price * info.getAmount();
			totalItemDiscount += itemDiscount * info.getAmount();
			discountPercentOverAll = info.getDiscountPercentOverAll(categoriesCount);
			
			for (int i = 0; i < info.getAmount(); i++) {
				sb.append("  ").append(info.toString());
				if (itemDiscount > 0) {
					sb.append(" **discount -").append(Ticket.DECIMAL_FORMAT.format(itemDiscount));
				}
				sb.append(System.lineSeparator());
			}
			
		}
		
		double finalPrice = (totalPrice - totalItemDiscount) * (1 - discountPercentOverAll);
		
		sb.append("  Total price: ").append(DECIMAL_FORMAT.format(totalPrice)).append("\n");
		sb.append("  Total discount: ").append(DECIMAL_FORMAT.format(totalItemDiscount)).append("\n");
		sb.append("  Final Price: ").append(DECIMAL_FORMAT.format(finalPrice)).append("\n");
		return sb.toString();
	}
	
	
	/**
	 * @return array table of the amount of products in each.
	 * <p>
	 * To check the amount of some catagory use category.ordinal() to index the arary.
	 * E.g. categoriesProductCount[(int)baseProduct.getCategory().ordinal()]
	 */
	private int[] categoriesProductCount() {
		int[] table = new int[BaseProduct.Category.values().length];
		
		for (TicketItem info : ticketItems) {
			
			if (info.getItem() instanceof BaseProduct baseProduct) {
				int index = (int)baseProduct.getCategory().ordinal();
				table[index] += info.getAmount();
			}
		}
		
		return table;
	}
	
}
