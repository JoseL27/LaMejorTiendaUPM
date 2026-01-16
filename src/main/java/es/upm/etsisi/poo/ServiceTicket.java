/* date = December 31st 2025 10:17 am */
package es.upm.etsisi.poo;

public class ServiceTicket extends Ticket {
	public ServiceTicket(int id, boolean isCustomId) {
		super(id, isCustomId);
	}
	
	@Override
		public boolean validateItemKind(InventoryItem item) {
		return item instanceof ServiceProduct;
	}
	
	@Override
		public String summaryString() {
        StringBuilder builder = new StringBuilder();
		
        builder.append("Ticket : ")
			.append(this.getComposedId())
			.append("\n");
		
		if (!ticketItems.isEmpty()) {
			builder.append("Services Included: \n");
			
			this.ticketItems.sort(null);
			
			for(TicketItem item: ticketItems){
				builder.append("  ")
					.append(item.toString())
					.append("\n");
			}
		}
		
		return builder.toString();
	}
}