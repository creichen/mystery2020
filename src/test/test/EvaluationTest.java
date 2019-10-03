package test;

import java.util.List;

import AST.Program;
import junit.framework.TestCase;
import mystery2020.Configuration;
import mystery2020.Interpreter;
import mystery2020.MysteryException;
import mystery2020.runtime.Runtime;

/**
 * Helper functions for testing the evaluator
 * 
 * @author creichen
 *
 */
public class EvaluationTest extends TestCase {
	
	static class EvaluationTester {
		private String body;
		
		public
		EvaluationTester(String body) {
			this.body = body;
			this.expandBody();
		}
		
		public void
		expandBody() {
			if (!this.body.startsWith("VAR")) {
				this.body = "BEGIN " + this.body + "\nEND";
			}
		}
		
		public List<String>
		run(Configuration config) {
			Program p = Interpreter.parseString(body);
			p.setConfiguration(config);
			Runtime rt = new Runtime();
			p.run(rt);
			return rt.getOutput();
		}
		
		public void
		runAndCheckOutput(Configuration configuration, String[] expected) {
			try {
				List<String> outputs = this.run(configuration);

				int offset = 0;
				boolean failmatch = false;
				for (String obj : outputs) {
					if (!obj.equals(expected[offset++])) {
						failmatch = true;
						break;
					}
				}
				if (failmatch || (expected.length != outputs.size())) {
					System.err.println("Expected\tActual");
					offset = 0;
					for (String obj : outputs) {
						String e = "";
						if (offset < expected.length) {
							e = "\"" + expected[offset++] + "\"";
						}
						String pad = "\t\t";
						if (e.length() > 6) {
							pad = "\t";
						}
						System.err.print("  " + e + pad + "\"" + obj + "\"");
						if (!obj.equals(e)) {
							System.err.print("\t<- mismatch");
						}
						System.err.println();
					}
					int missing = expected.length - outputs.size();
					if (missing > 0) {
						System.err.println("... " + missing + " additional expected elements that are missing");
					}
				}
				assertEquals(expected.length, outputs.size());
				assertFalse(failmatch);
			} catch (RuntimeException exn) {
				System.err.println("With configuration:\n" + configuration);
				throw exn;
			}
		}
	}
	
	/**
	 * Try out all configurations
	 * 
	 * @param body Body (automatically enclosed in 'BEGIN' and 'END', unless it starts with 'VAR')
	 * @param expected Expected output strings
	 */
	public void
	forallConfigurationsTestOutput(String body, String ... expected) {
		EvaluationTester evtest = new EvaluationTester(body);
		Configuration config = new Configuration(); 
		evtest.runAndCheckOutput(config, expected);
	}

	public void
	forallConfigurationsTestFail(String body, Class<? extends MysteryException> expected) {
		EvaluationTester evtest = new EvaluationTester(body);
		Configuration config = new Configuration();
		try {
			evtest.run(config);
		} catch (MysteryException exn) {
			if (expected.isInstance(exn)) {
				return;
			}
			throw exn; // not what we were looking for
		}
		fail();
	}
	
	public void
	testNothing() {} // shut up false failures
}
