package mystery2020;

public class AssociativityException extends StaticException {
	private static final long serialVersionUID = -8057908701736247357L;

	public AssociativityException(int line_nr) {
		super(line_nr, "PRECASSOC", "");
	}
}
