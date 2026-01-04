/* date = December 31st 2025 10:29 am */
package es.upm.etsisi.poo;


public class CombinedTicket extends Ticket {
	public CombinedTicket(int id) {
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
