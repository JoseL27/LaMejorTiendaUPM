package es.upm.etsisi.poo;

public class TimedProduct extends Product {
    private TimedType type;
    private final int MAX_PEOPLE = 100;
    private int people;

    public TimedProduct(int id, String name, double price, int people, TimedType type) {
        super(id, name, price * people);
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
        MEETING(43200000),
        LAUNCH(259200000);

        private int  timeForPreparing;

        private TimedType(int timeForPreparing) {
            this.timeForPreparing = timeForPreparing;

        }
    }
}
