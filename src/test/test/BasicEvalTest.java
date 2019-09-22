package test;

public class BasicEvalTest extends EvaluationTest {
	public void
	testPrint() {
		forallConfigurationsTestOutput(
				"PRINT 0",
				"0");
	}

	public void
	testPrint2() {
		forallConfigurationsTestOutput(
				"PRINT 9; PRINT 1",
				"9", "1");
	}
	
	public void
	testCondTrue0() {
		forallConfigurationsTestOutput(
				"IF 1 THEN\n PRINT 1\n ELSE\n PRINT 0 END\n",
				"1");
	}
	
	public void
	testCondTrue1() {
		forallConfigurationsTestOutput(
				"IF -1 THEN PRINT 1 ELSE PRINT 0 END",
				"1");
	}
	
	public void
	testCondTrue2() {
		forallConfigurationsTestOutput(
				"IF 2 THEN PRINT 1 ELSE PRINT 0 END",
				"1");
	}
	
	public void
	testCondFalse() {
		forallConfigurationsTestOutput(
				"IF 0 THEN PRINT 1 ELSE PRINT 0 END",
				"0");
	}
}
