package mystery2020.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregated runtime information
 *  
 * @author creichen
 *
 */
public class Runtime {
	public ArrayList<String> output = new ArrayList<>();
	
	public Runtime() {
	}
	
	/**
	 * Executed before each statement (to bound steps) 
	 */
	public void
	runStep() {
	}
	
	/**
	 * Execute before each call (to bound recursion depth)
	 */
	public void
	runCall() {
	}
	
	/**
	 * Adds a piece of output
	 * @param out The string to be output
	 */
	public void
	addOutput(String out) {
		this.output.add(out);
	}

	public List<String>
	getOutput() {
		return this.output;
	}
}
