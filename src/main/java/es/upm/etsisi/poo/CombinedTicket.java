/* date = December 31st 2025 10:29 am */
package es.upm.etsisi.poo;


public class CombinedTicket extends Ticket {
	public CombinedTicket(int id, boolean isCustomId) {
		super(id, isCustomId);
	}
	
	@Override
		public boolean validateItemKind(InventoryItem item) {
		return (item instanceof Product || item instanceof ServiceProduct); // or just true
	}
	
	@Override
		public String summaryString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Ticket : ")
			.append(getComposedId())
			.append("\n");
		
		double totalPrice = 0;
		double productItemDiscount = 0;
		double globalServiceDiscountPercentage = 0;
		
		ticketItems.sort(null);
		int[] categoriesCount = categoriesProductCount();
		
		
		int serviceCount = 0;
		int productCount = 0;
		for (TicketItem info : ticketItems) {
			if (info.getItem() instanceof ServiceProduct) {
				serviceCount++;
			} else if (info.getItem() instanceof Product) {
				productCount++;
			}
		}
		
		globalServiceDiscountPercentage = Math.min(ServiceProduct.SERVICE_DISCOUNT * serviceCount, 1); // Clamp to 100%
		
		// Services
		if (!ticketItems.isEmpty()) {
			if (serviceCount > 0) {
				sb.append("Services Included: \n");
				for (TicketItem info : ticketItems) {
					if (info.getItem() instanceof ServiceProduct) {
						sb.append("  ")
							.append(info.toString())
							.append('\n');
					}
				}
			}
			
			// Products (BaseProduct, TimedProduct)
			if (productCount > 0) {
				sb.append("Product Included\n");
				for (TicketItem info : ticketItems) {
					if (info.getItem() instanceof Product) {
						double price = info.getPrice();
						double itemDiscount = info.getItemDiscount(categoriesCount);
						
						totalPrice += price * info.getAmount();
						productItemDiscount += itemDiscount * info.getAmount();
						
						int printRepeatAmount = info instanceof TimedTicketItem ? 1 : info.getAmount();
						for (int i = 0; i < printRepeatAmount; i++) {
							sb.append("  ").append(info.toString());
							if (itemDiscount > 0) {
								sb.append(" **discount -").append(Ticket.DECIMAL_FORMAT.format(itemDiscount));
							}
							sb.append('\n');
						}
					}
				}
				
				double serviceDiscount = (totalPrice - productItemDiscount) * globalServiceDiscountPercentage;
				String serviceDiscountFmt = DECIMAL_FORMAT.format(serviceDiscount);
				double finalPrice = (totalPrice - productItemDiscount) * (1 - globalServiceDiscountPercentage);
				
				sb.append("  Total price: ").append(DECIMAL_FORMAT.format(totalPrice)).append("\n");
				sb.append(String.format("  Extra Discount from services:%s **discount -%s%n", serviceDiscountFmt, serviceDiscountFmt));
				sb.append("  Total discount: ").append(DECIMAL_FORMAT.format(productItemDiscount + serviceDiscount)).append("\n");
				sb.append("  Final Price: ").append(DECIMAL_FORMAT.format(finalPrice)).append("\n");
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * @return array table of the amount of products in each.
	 * <p>
	 * To check the amount of some catagory use category.ordinal() to index the arary.
	 * E.g. categoriesProductCount[(int)baseProduct.getCategory().ordinal()]
	 */
	private int[] categoriesProductCount() {
		// Yoink
		int[] table = new int[BaseProduct.Category.values().length];
		for (TicketItem info : ticketItems) {
			
			// TODO(enrique): Think of a more OOP way to do this.
			if (info.getItem() instanceof BaseProduct baseProduct) {
				int index = (int)baseProduct.getCategory().ordinal();
				table[index] += info.getAmount();
			}
		}
		
		return table;
	}
}
