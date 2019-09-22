package test;

import java.util.List;

import AST.Program;
import junit.framework.TestCase;
import mystery2020.Configuration;
import mystery2020.Interpreter;
import mystery2020.runtime.Runtime;

/**
 * Helper functions for testing the evaluator
 * 
 * @author creichen
 *
 */
public class EvaluationTest extends TestCase {
	/**
	 * Try out all configurations
	 * 
	 * @param body Body (automatically enclosed in 'BEGIN' and 'END')
	 * @param expected Expected output strings
	 */
	public void
	forallConfigurationsTestOutput(String body, String ... expected) {
		String pbody = "BEGIN " + body + "\nEND";
		
		Configuration config = new Configuration(); 
		try {
			Program p = Interpreter.parseString(pbody);
			p.setConfiguration(config);
			Runtime rt = new Runtime();
			p.run(rt);

			List<String> outputs = rt.getOutput();

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
			System.err.println("With configuration:\n" + config);
			throw exn;
		}
	}
	
	public void
	testNothing() {} // shut up false failures
}
