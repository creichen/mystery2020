package mystery2020;

public class IntLiteralException extends StaticException {
	private static final long serialVersionUID = 7871221479441919131L;

	public
	IntLiteralException(int line_nr, int colun_nr, String intliteral) {
		super(line_nr, "INTLITERAL", "");
	}
}
