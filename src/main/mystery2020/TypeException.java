package mystery2020;

public class TypeException extends MysteryException {
	private static final long serialVersionUID = -5739984900912396595L;
	
	public TypeException(int line_nr, String message) {
		super(line_nr, "TYPE", message); 
	}
}
