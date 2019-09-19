package mystery2020;

public class IntLiteralException extends StaticException {
	private static final long serialVersionUID = 7871221479441919131L;

	int line_nr;

	public IntLiteralException(int line_nr, int colun_nr, String intliteral) {
		this.line_nr = line_nr;
	}
	
	@Override
	public String
	toString() {
		return "INTLITERAL " + this.line_nr;
	}
}
