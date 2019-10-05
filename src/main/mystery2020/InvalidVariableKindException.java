package mystery2020;

public class InvalidVariableKindException extends StaticException {

	private static final long serialVersionUID = 4857966375246481827L;

	public InvalidVariableKindException(int line_nr, String message) {
		super(line_nr, "KIND", message);
	}
}
