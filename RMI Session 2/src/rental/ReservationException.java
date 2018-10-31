package rental;

public class ReservationException extends Exception {

	// Serializable only because it extends Exception, which is serializable
	private static final long serialVersionUID = -8159128964694199920L;

	public ReservationException(String string) {
        super(string);
    }
}