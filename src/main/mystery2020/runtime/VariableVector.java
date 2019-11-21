package mystery2020.runtime;

import mystery2020.MysteryArrayOOBException;

/**
 * Represents a vector of variables (either abstract or concrete), as used in a formal/actual parameter list or activation record
 * 
 * @author creichen
 *
 */
public class VariableVector {
	private Variable[] vector;
	int offset = 0;
	boolean bounds_check = false; 
	
	/**
	 * 
	 * @param v
	 * @param offset The offset of v[0] from the perspective of get()
	 */
	public VariableVector(Variable[] v, int offset) {
		this.vector = v;
		this.offset = offset;
		this.bounds_check = true;
	}
	
	public VariableVector(Variable[] v) {
		this(v, 0);
		this.bounds_check = false;
	}
	
	public Variable
	get(int i) {
		return this.vector[i - this.offset];
	}
	
	public int
	getOffset() {
		return this.offset;
	}
	
	public Variable
	getChecked(int line_nr, int i) {
		if (i < this.offset || (i - this.offset) >= this.vector.length) {
			throw new MysteryArrayOOBException(line_nr, "Index " + i + " not in bounds [" + this.offset + ".." + (this.offset + this.vector.length - 1) + "]");
		}
		return this.get(i);
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

	public int
	size() {
		return this.vector.length;
	}
}
