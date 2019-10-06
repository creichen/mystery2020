package mystery2020;

public class CallToNothingException extends MysteryException {

	private static final long serialVersionUID = 1577654885799818672L;

	public CallToNothingException(int line_nr) {
		super(line_nr, "NULLPROCEDURE", "Call to non-existing procedure");
	}

}
