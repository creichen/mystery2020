package mystery2020;

public class MysteryArrayOOBException extends MysteryException {
	private static final long serialVersionUID = -8469051221392647294L;

	public MysteryArrayOOBException(int line_nr, String message) {
		super(line_nr, "OOB", message);
	}
}
