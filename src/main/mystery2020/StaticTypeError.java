package mystery2020;

public class StaticTypeError extends StaticException {
	
	private static final long serialVersionUID = -6596235905793313064L;

	public StaticTypeError(int line_nr, String message) {
		super(line_nr, "TYPE-STATIC", message);
	}
}
