package mystery2020;

public abstract class StaticException extends MysteryException {
	private static final long serialVersionUID = 4059106739564106605L;
	
	public
	StaticException(int line_nr, String code, String message) {
		super(line_nr, code, message);
	}
}
