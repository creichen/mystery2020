package mystery2020;

public class LexerException extends StaticException {
	private static final long serialVersionUID = 5276324202959754199L;

    public
    LexerException(int line_nr, int column_nr) {
    	super(line_nr, "LEXER", "");
    }
}
