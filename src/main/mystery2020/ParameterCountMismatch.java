package mystery2020;

public class ParameterCountMismatch extends MysteryException {

	private static final long serialVersionUID = -8549933728048317682L;

	public ParameterCountMismatch(int line_nr, String message) {
		super(line_nr, "ARGCOUNT", message);
	}
}
