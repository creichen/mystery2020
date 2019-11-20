package mystery2020;

public class RecursiveTypeException extends MysteryException {

	private static final long serialVersionUID = 7292526173371675149L;
	
	public RecursiveTypeException(int line_nr, String message) {
		super(line_nr, "RECTYPE", message);
	}
}
