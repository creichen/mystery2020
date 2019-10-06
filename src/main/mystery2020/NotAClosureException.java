package mystery2020;

public class NotAClosureException extends MysteryException {
	
	private static final long serialVersionUID = 2617080477416808882L;

	public NotAClosureException(int line_nr, String message) {
		super(line_nr, "NOCLOSURE", message);
	}
}
