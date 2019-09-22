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
}
