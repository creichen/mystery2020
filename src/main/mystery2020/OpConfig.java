package mystery2020;

public class OpConfig {
	public static enum Associativity {
		LEFT("l"),
		RIGHT("r"),
		NONE("-");
		
		private String code;
		private Associativity(String code) {
			this.code = code;
		}

		public String
		getCode() {
			return this.code;
		}
	}

	private int precedence;
	private Associativity associativity;

	public OpConfig(int precedence, Associativity associativity) {
		this.precedence = precedence;
		this.associativity = associativity;
	}
	
	public int
	getPrecedence() {
		return this.precedence;
	}
	
	public Associativity
	getAssociativity() {
		return this.associativity;
	}

	@Override
	public String
	toString() {
		return this.precedence + this.associativity.getCode();
	}

	public static OpConfig
	parse(String s) {
		if (s.length() == 2) {
			int precedence = -1;
			try {
				precedence = Integer.parseInt(s.substring(0, 1));
			} catch (NumberFormatException exn) {
				// ignore, handled later
			}
			String assoc_key = s.substring(1, 2);
			Associativity assoc = null;
			for (Associativity assoc_option : Associativity.values()) {
				if (assoc_option.getCode().equals(assoc_key)) {
					assoc = assoc_option;
					break;
				}
			}
			if (precedence != -1 && assoc != null) {
				return new OpConfig(precedence, assoc);
			}
		}
		throw new IllegalArgumentException("Not a valid operator configuration: `"+s+"'");
	}
}