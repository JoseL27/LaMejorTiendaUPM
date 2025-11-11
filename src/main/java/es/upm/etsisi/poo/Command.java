package es.upm.etsisi.poo;

public interface Command {
	public void eval(String[] args, UserManager userManager, Inventory dataManager);
}
