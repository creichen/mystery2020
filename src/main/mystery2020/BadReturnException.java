package mystery2020;

public class BadReturnException extends StaticException {

	private static final long serialVersionUID = 3212311L;

	public BadReturnException(int line_nr, String message) {
		super(line_nr, "BADRETURN", message);
	}

}
