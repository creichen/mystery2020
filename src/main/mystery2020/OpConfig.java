package mystery2020;

public class OpConfig {
	public static enum Associativity {
		LEFT,
		RIGHT,
		NONE
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
}
