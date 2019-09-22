package mystery2020;

public abstract class MysteryException extends RuntimeException {
	private static final long serialVersionUID = -6121989545173131564L;

	private int line_nr;
	private String key;
	private String message;

	public MysteryException(int line_nr, String key, String message) {
		this.line_nr = line_nr;
		this.key = key;
		this.message = message;
	}
	
	public String toString() {
		return this.key + " " + this.line_nr + (this.message.length() == 0 ? "" : (" " + this.message));
	}
}
