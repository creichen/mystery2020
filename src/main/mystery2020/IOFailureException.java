package mystery2020;

public class IOFailureException extends RuntimeException {
	private static final long serialVersionUID = -6501496299807495993L;

	public
    IOFailureException(Exception exn) {
	super(exn);
    }

    public String
    toString() {
	return "IO\n" + super.toString();
    }
}
