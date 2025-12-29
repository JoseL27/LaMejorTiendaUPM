package es.upm.etsisi.poo;

import es.upm.etsisi.poo.exceptions.DataException;
import es.upm.etsisi.poo.exceptions.FailedCommandException;

public interface Command {
	void eval(String[] args) throws FailedCommandException, DataException;
}
