package mystery2020.runtime;

import mystery2020.Configuration;
import mystery2020.DynamicTypeError;
import mystery2020.MType;
import mystery2020.NotAClosureException;

public class Value {
	private MType type;
	private Object value;

	public Value(MType type, Object obj) {
		this.type = type;
		this.value = obj;
		if (this.value == null) {
			throw new RuntimeException("Must not be null");
		}
	}

	public static final Value NOTHING = new Value(MType.ANY, new Object() {
		@Override
		public boolean
		equals(Object other) {
			return other == this;
		}

		@Override
		public String
		toString() {
			return "NOTHING";
		}
	});

	public MType
	getType() {
		return this.type;
	}

	public Object
	getValue() {
		return this.value;
	}

	public Value
	call(Runtime rt, int line, VariableVector args) {
		if (this.value instanceof Closure) {
			return ((Closure)this.value).call(rt, line, args);

		}
		throw new NotAClosureException(line, "Cannot be called: " + this);
	}

	/**
	 * Retrieves int without type checking
	 *
	 * @param line_nr Line number, for error reporting
	 * @return int interpretation, or a DynamicTypeError on failure
	 */
	public int
	getInt(int line_nr) {
		if (this.value instanceof Integer) {
			return (Integer) this.value;
		}
		throw new DynamicTypeError(line_nr, "Non-int (" + this.type.toString() + ") to int");
	}

	public VariableVector
	getArray(int line_nr) {
		if (this.value instanceof VariableVector) {
			return (VariableVector) this.value;
		}
		throw new DynamicTypeError(line_nr, "Non-array (" + this.type.toString() + ") to array");
	}

	public static Value
	True(Configuration cfg) {
		return new Value(MType.UR_INTEGER, 1);
	}

	public static Value
	False(Configuration cfg) {
		return new Value(MType.UR_INTEGER, 0);
	}

	public boolean
	equalTo(Object other, Configuration config) {
		if (other instanceof Value) {
			Value v = (Value) other;
			return this.getType().equalTo(v.getType(), config)
					&& this.getType().valueEquals(this.getValue(), v.getValue(), config);
		}
		return false;
	}

	@Override
	public String
	toString() {
		return this.value + ":" + this.type;
	}
}
