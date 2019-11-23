package mystery2020;

public class InvalidModeException extends MysteryException {

	private static final long serialVersionUID = 2657956436079534741L;
	
	public InvalidModeException(int line_nr, String message) {
		super(line_nr, "MODE", message);
	}
}
