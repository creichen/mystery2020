package mystery2020.runtime;

import AST.ASTNode;
import mystery2020.Configuration;
import mystery2020.MType;

public class Variable {
	private String name;
	private MType type;
	private Value value;

	/**
	 * Constructs a new variable
	 * 
	 * The value is initialised to null, which is not a valid value.  Use initDefault() to properly initialise.
	 * 
	 * @param type Type of the variable, must be set
	 * @param name Name of the variable, may be null
	 */
	public Variable(MType type, String name) {
		assert type != null;
		this.type = type;
		this.name = name;
		this.value = null;
	}

	public void
	initDefault() {
		this.value = type.getDefaultValue();
	}

	public Variable
	copyAndInit() {
		Variable v = new Variable(this.type, this.name);
		v.initDefault();
		return v;
	}

	public String
	getName() {
		return this.name;
	}
	
	public boolean
	hasName() {
		return this.name != null;
	}
	
	public MType
	getType() {
		return this.type;
	}
	
	public Value
	getValue() {
		return this.value;
	}
	
	public void
	checkAndSetValue(ASTNode<?> node, Value v, Configuration config) {
		this.type.ensureCanAssignFrom(node, v.getType());
		this.type.ensureValueIsAssignable(node, v);
		this.setValue(v, config);
	}
	
	/**
	 * No type checking here, type checking happens at assignments and calls
	 * 
	 * @param v
	 * @param config Configuration for special assignment semantics, or null for default semantics
	 */
	public void
	setValue(Value v, Configuration config) {
		Object raw_value = v.getValue();
		if (config != null) {
			if (this.type instanceof MType.ArrayType
					&& this.value.getValue() instanceof VariableVector
					&& v.getValue() instanceof VariableVector) {
				VariableVector lhs = (VariableVector) this.value.getValue();
				VariableVector rhs = (VariableVector) v.getValue();
				raw_value = config.array_assignment.get().assign(lhs, rhs, config);
			}
		}
		this.value = new Value(this.type, raw_value);
	}
	
	@Override
	public String
	toString() {
		return this.name + ":" + this.type + "=" + this.value; 
	}

	public void
	setName(String new_name) {
		this.name = new_name;
	}
}
