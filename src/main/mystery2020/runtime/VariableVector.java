package mystery2020.runtime;

/**
 * Represents a vector of variables (either abstract or concrete), as used in a formal/actual parameter list or activation record
 * 
 * @author creichen
 *
 */
public class VariableVector {
	private Variable[] vector;
	
	public VariableVector(Variable[] v) {
		this.vector = v;
	}
	
	public Variable
	get(int i) {
		return this.vector[i];
	}
	
	public VariableVector
	instantiate() {
		VariableVector retval = new VariableVector(this.vector);
		for (int i = 0 ; i < this.vector.length; i++) {
			retval.vector[i] = retval.vector[i].copyAndInit();
		}
		return retval;
	}
	
	@Override
	public String
	toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < vector.length; i++) {
			sb.append(" #" + i);
			sb.append("=" + this.vector[i]);
		}
		return sb.toString();
	}
}
