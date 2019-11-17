package mystery2020;

public final class MysteryLimitException extends mystery2020.MysteryException {
	private static final long serialVersionUID = -6404466781790194271L;

	public MysteryLimitException(int line_nr, String message) {
		super(line_nr, "LIMIT", message);
	}
}