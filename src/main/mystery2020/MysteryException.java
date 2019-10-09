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
	
	public int
	getLineNr() {
		return this.line_nr;
	}

	public String
	getKey() {
		return this.key;
	}

	public String toString() {
		return this.key + " " + this.line_nr + (this.message.length() == 0 ? "" : (" " + this.message));
	}

	public void printProgram(String body) {
		final String[] lines = body.split("\n");
		String pad = "";
		while (pad.length() < this.key.length()) {
			pad += " ";
		}
		final String marker = " > ";
		pad +=                "   ";
		for (int i = 0; i < lines.length; i++) {
			int current_line_nr = i + 1;
			if (current_line_nr == this.line_nr) {
				System.err.print(this.key + marker);
			} else {
				System.err.print(pad);
			}
			System.err.print(String.format("%-4d", current_line_nr));
			System.err.println(lines[i]);
		}
	}
}
