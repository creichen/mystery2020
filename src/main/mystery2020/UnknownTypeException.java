package mystery2020;

public class UnknownTypeException extends MysteryException {
	
	private static final long serialVersionUID = -4683173118303208263L;

	public UnknownTypeException(int line_nr, String message) {
		super(line_nr, "UTYPE", message);
	}
}
