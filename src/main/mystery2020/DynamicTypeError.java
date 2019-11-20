package mystery2020;

public class DynamicTypeError extends MysteryException {
	private static final long serialVersionUID = -415527355972175236L;

	public DynamicTypeError(int line_nr, String message) {
		super(line_nr, "TYPE-DYNAMIC", message);
	}
}
