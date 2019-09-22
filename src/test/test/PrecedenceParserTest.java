package test;

import mystery2020.AssociativityException;
import mystery2020.Configuration.Op;
import mystery2020.OpConfig;
import mystery2020.OpConfig.Associativity;
import mystery2020.PrecedenceParser;

import java.util.HashMap;

import junit.framework.TestCase;

public class PrecedenceParserTest extends TestCase {
	@Override
	public void
	setUp() {
		PrecedenceParser.testMode = true;
	}
	
	@Override
	public void
	tearDown() {
		PrecedenceParser.testMode = false;
	}
	
	private static final Associativity LEFT = Associativity.LEFT;
	private static final Associativity RIGHT = Associativity.RIGHT;		
	private static final Associativity NONE = Associativity.NONE;
	
	private static final Op PLUS = Op.ADD;
	private static final Op AND = Op.AND;
	private static final Op EQ = Op.EQ;
	private static final Op GT = Op.GT;
	
	public static void
	tryParse(OpConfig plus, OpConfig and, OpConfig eq, OpConfig gt,
			String expected, Op ... operators) {
		HashMap<Op, OpConfig> config = new HashMap<>();
		config.put(PLUS, plus);
		config.put(AND, and);
		config.put(EQ, eq);
		config.put(GT, gt);
		PrecedenceParser pp = new PrecedenceParser(config);
		
		AST.Expr expr = new AST.Number("" + operators.length);
		for (int i = operators.length - 1; i >= 0; i--) {
			expr = new AST.BinOpSequence(new AST.Number("" + i),
					PrecedenceParser.makeOperator(operators[i]),
					expr);
		}
	
		try {
			AST.Expr result = pp.parse(expr);
			if (expected == null) {
				fail();
			}
			assertEquals(expected, result.toString());
		} catch (AssociativityException exn) {
			assertNull(expected);
		}
	}
	
	public void testTrivial0() {
		tryParse(
				new OpConfig(3, RIGHT), // + 
				new OpConfig(2, RIGHT), // AND
				new OpConfig(1, RIGHT), // ==
				new OpConfig(0, RIGHT), // >
				"(0 + 1)",
				PLUS);
	}
	
	public void testTrivial1() {
		tryParse(
				new OpConfig(3, LEFT), // + 
				new OpConfig(2, RIGHT), // AND
				new OpConfig(1, RIGHT), // ==
				new OpConfig(0, RIGHT), // >
				"(0 + 1)",
				PLUS);
	}
	
	public void testTrivial2() {
		tryParse(
				new OpConfig(3, NONE), // + 
				new OpConfig(2, RIGHT), // AND
				new OpConfig(1, RIGHT), // ==
				new OpConfig(0, RIGHT), // >
				"(0 + 1)",
				PLUS);
	}

	public void testLeft0() {
		tryParse(
				new OpConfig(3, LEFT), // + 
				new OpConfig(2, RIGHT), // AND
				new OpConfig(1, RIGHT), // ==
				new OpConfig(0, RIGHT), // >
				"((0 + 1) + 2)",
				PLUS, PLUS);
	}

	public void testLeft1() {
		tryParse(
				new OpConfig(3, LEFT), // + 
				new OpConfig(3, LEFT), // AND
				new OpConfig(1, RIGHT), // ==
				new OpConfig(0, RIGHT), // >
				"((0 + 1) AND 2)",
				PLUS, AND);
	}

	public void testRight0() {
		tryParse(
				new OpConfig(3, RIGHT), // + 
				new OpConfig(2, RIGHT), // AND
				new OpConfig(1, RIGHT), // ==
				new OpConfig(0, RIGHT), // >
				"(0 + (1 + 2))",
				PLUS, PLUS);
	}

	public void testRight1() {
		tryParse(
				new OpConfig(3, RIGHT), // + 
				new OpConfig(3, RIGHT), // AND
				new OpConfig(1, RIGHT), // ==
				new OpConfig(0, RIGHT), // >
				"(0 + (1 AND 2))",
				PLUS, AND);
	}

	public void testNon0() {
		tryParse(
				new OpConfig(3, NONE), // + 
				new OpConfig(2, RIGHT), // AND
				new OpConfig(1, RIGHT), // ==
				new OpConfig(0, RIGHT), // >
				null, // expect exception
				PLUS, PLUS);
	}

	public void testPrecedence0() {
		tryParse(
				new OpConfig(0, LEFT), // + 
				new OpConfig(1, LEFT), // AND
				new OpConfig(2, LEFT), // ==
				new OpConfig(3, LEFT), // >
				"((0 AND (1 == 2)) + (3 > 4))",
				AND, EQ, PLUS, GT);
	}

	public void testAssociativityMismatchOK() {
		tryParse(
				new OpConfig(3, LEFT), // + 
				new OpConfig(1, LEFT), // AND
				new OpConfig(3, RIGHT), // ==
				new OpConfig(0, LEFT), // >
				"(((0 + 1) + 2) AND (3 == (4 == 5)))",
				PLUS, PLUS, AND, EQ, EQ);
	}
	
	public void testAssociativityMismatchFail() {
		tryParse(
				new OpConfig(3, LEFT), // + 
				new OpConfig(1, LEFT), // AND
				new OpConfig(3, RIGHT), // ==
				new OpConfig(0, LEFT), // >
				null,
				PLUS, PLUS, EQ, EQ);
	}
}
