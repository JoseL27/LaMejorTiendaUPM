package es.upm.etsisi.poo;

import es.upm.etsisi.poo.commands.ProductCommand;
import es.upm.etsisi.poo.commands.TicketCommand;


public interface Command {
	public void eval(String[] args, Ticket ticket, ArrayDataManager dataManager);
}
