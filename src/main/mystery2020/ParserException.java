package mystery2020;

public class ParserException extends StaticException {
	private static final long serialVersionUID = -5785868125381239954L;
	@SuppressWarnings("unused")
	private int line_start, line_end;
	@SuppressWarnings("unused")
	private int column_start, column_end;

	public ParserException(int line_start, int column_start, int line_end, int column_end) {
		this.line_start = line_start;
		this.line_end= line_end;
		this.column_start = column_start;
		this.column_end= column_end;
	}
	
	public String
	toString() {
		return "PARSER " +  this.line_start;
	}
}
