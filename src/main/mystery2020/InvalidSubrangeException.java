package mystery2020;

public class InvalidSubrangeException extends StaticException {
	private static final long serialVersionUID = 5389100084442620012L;
	
	public InvalidSubrangeException(int line_nr, String message) {
		super(line_nr, "SUBRANGE", message);
	}
}
