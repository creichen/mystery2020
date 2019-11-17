package mystery2020;

public class NameException extends StaticException {
	private static final long serialVersionUID = 9162693643740601631L;

	public NameException(int line_nr, String message) {
		super(line_nr, "NAME", message);
	}

	public NameException(int line_nr) {
		super(line_nr, "NAME", "");
	}
}
