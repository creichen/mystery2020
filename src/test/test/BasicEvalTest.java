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

	public void
	testBasicAdd() {
		forallConfigurationsTestOutput(
				"PRINT 1 + 2",
				"3");
	}

	public void
	testBasicEQ0() {
		forallConfigurationsTestOutput(
				"PRINT 1 == 2",
				"0");
	}
	
	public void
	testBasicEQ1() {
		forallConfigurationsTestOutput(
				"PRINT 2 == 2",
				"1");
	}

	public void
	testBasicGT0() {
		forallConfigurationsTestOutput(
				"PRINT 2 > 2",
				"0");
	}

	public void
	testBasicGT1() {
		forallConfigurationsTestOutput(
				"PRINT 2 > 1",
				"1");
	}

	public void
	testBasicAND0() {
		forallConfigurationsTestOutput(
				"PRINT 1 AND 2",
				"1");
	}

	public void
	testBasicAND1() {
		forallConfigurationsTestOutput(
				"PRINT 2 AND 0",
				"0");
	}

	public void
	testBasicAND2() {
		forallConfigurationsTestOutput(
				"PRINT 0 AND 1",
				"0");
	}

	public void
	testBasicVar() {
		forallConfigurationsTestOutput(
				"VAR x : INTEGER\n" +
				"BEGIN x := 7; PRINT x END",
				"7");
	}

	public void
	testTwoVar() {
		forallConfigurationsTestOutput(
				"VAR x : INTEGER;\n" +
				"VAR y : INTEGER\n" +
				"BEGIN x := 7; y := 6; PRINT y; PRINT y + x END",
				"6", "13");
	}
}
