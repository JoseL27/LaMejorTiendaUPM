package es.upm.etsisi.poo.exceptions;

public class MissingItemException extends DataException{
    public MissingItemException(String message){
        super(message);
    }
    
    public static MissingItemException fromId(String label, int id){
        return new MissingItemException(String.format("%s with id '%d' does not exist", label, id));
    }
}
