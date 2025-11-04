package es.upm.etsisi.poo;

public class TimedProduct extends Product {
    private static TimedType type;
    private final int MAX_PEOPLE = 100;
    private int people;

    // It is assumed that all the parameters are valid, this should be handled before creating the object
    public TimedProduct(int id, String name, double price, int people, TimedType type) {
        super(id, name, price * people); // EL precio de la reunión depende del número de individuos
        this.type = type;
        this.people = people;
    }

    public TimedType getType() {
        return type;
    }

    public int getPeople() {
        return people;
    }

    // timeForPreparing are de ms minimun to prepare  the activity.
    public enum TimedType {
        MEETING(43200000), //12h
        LAUNCH(259200000); //72h

        private int  timeForPreparing;

        private TimedType(int timeForPreparing) {
            this.timeForPreparing = timeForPreparing;

        }
    }
}
