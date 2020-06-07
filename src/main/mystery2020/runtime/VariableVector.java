package mystery2020.runtime;

import mystery2020.Configuration;
import mystery2020.MysteryArrayOOBException;
import AST.ASTNode;

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

	/**
	 * Creates and initialises a (deep) copy of this variable vector
	 */
	public VariableVector
	instantiate(ASTNode owner) {
		VariableVector retval = new VariableVector(this.vector);
		for (int i = 0 ; i < this.vector.length; i++) {
			retval.vector[i] = retval.vector[i].copyAndInit(owner);
		}
		return retval;
	}

	/**
	 * Accesses a suitable instance, depending on how local variable storage is configured
	 */
	public VariableVector
	accessInstance(Runtime rt, ASTNode ast_node) {
		return rt.getConfiguration().variable_storage.get().getVariableVectorInstance(this, ast_node);
	}

	@Override
	public String
	toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < this.vector.length; i++) {
			sb.append(" #" + i);
			sb.append("=" + this.vector[i]);
		}
		return sb.toString();
	}

	public int
	size() {
		return this.vector.length;
	}

	public boolean
	equalTo(VariableVector other, Configuration config) {
		if (other.offset != this.offset || other.vector.length != this.vector.length) {
			return false;
		}
		for (int i = 0; i < this.vector.length; i++) {
			if (!this.vector[i].getValue().equalTo(other.vector[i].getValue(), config)) {
				return false;
			}
		}
		return true;
	}
}
