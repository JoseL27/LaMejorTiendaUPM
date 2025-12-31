/* date = December 31st 2025 10:17 am */
package es.upm.etsisi.poo;

public class ServiceTicket extends Ticket {
	public ServiceTicket(int id) {
		super(id);
	}
	
	@Override
		public boolean validateItemKind(InventoryItem item) {
		return false;
	}
	
	@Override
		public String summaryString() {
		return "";
	}
}
