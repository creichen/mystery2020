package mystery2020;

public class LexerException extends StaticException {
	private static final long serialVersionUID = 5276324202959754199L;
	private int line_nr;

    public
    LexerException(int line_nr, int column_nr) {
	this.line_nr = line_nr;
    }

    public String
    toString() {
	return "LEXER " + this.line_nr;
    }
}
