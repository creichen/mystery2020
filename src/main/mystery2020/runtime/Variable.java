package mystery2020.runtime;

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
	setValue(Value v) {
		// typecheck here?
		this.value = v;
	}
	
	@Override
	public String
	toString() {
		return this.name + ":" + this.type + "=" + this.value; 
	}
}
