package es.upm.etsisi.poo.exceptions;

public class DuplicateItemException extends DataException{
    public DuplicateItemException(String message){
        super(message);
    }
    
    public static DuplicateItemException fromId(String label, int id) {
        return new DuplicateItemException(String.format("%s with id '%d' allready exists", label, id));
    }
}
