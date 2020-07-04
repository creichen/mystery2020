package mystery2020;

import AST.ASTNode;
import mystery2020.runtime.Closure;
import mystery2020.runtime.Value;
import mystery2020.runtime.Variable;
import mystery2020.runtime.VariableVector;

public abstract class MType {
	/**
	 *
	 * @param other
	 * @param config
	 * @return true iff the other type can be converted to this type
	 */
	public boolean
	convertibleFromForeignType(MType other, Configuration config) {
		return false;
	}

	public final boolean
	isSubtypeOf(MType other, Configuration config) {
		return this == other
				|| other == ANY
				|| this.equalTo(other, config)
				|| this.isNontrivialSubtypeOf(other, config);
	}

	public boolean
	isNontrivialSubtypeOf(MType other, Configuration config) {
		return false;
	}

	public final boolean
	convertibleTo(MType other, Configuration config) {
		return this.isSubtypeOf(other, config)
				|| other.convertibleFromForeignType(this, config)
				;
	}

	public final boolean
	equalTo(MType other, Configuration config) {
		MTypeComparison comp = new MTypeComparison(config);
		return comp.isEq(this, other);
	}

	public boolean
	isStructurallyEqual(MType other, MTypeComparison comp) {
		return other == this;
	}

	public boolean
	valueEquals(Object v1, Object v2, Configuration config) {
		return v1.equals(v2);
	}

	public void
	ensureCanAssignFrom(ASTNode<?> node, MType source) {
		if (node.dyncheck()
			&& !source.convertibleTo(this, node.config())) {
			throw new DynamicTypeError(node.line(), "Cannot assign " + source + " to " + this);
		}
	}

	public ArrayType
	getArrayType(ASTNode<?> node) {
		throw new DynamicTypeError(node.line(), "Not an array type: " + this);
	}

	public ProcedureType
	getProcedureType(ASTNode<?> node) {
		throw new DynamicTypeError(node.line(), "Not a procedure type: " + this);
	}

	/**
	 * Type is machine inferred and thus canbe assigned to named types
	 * @return
	 */
	public boolean
	isUrType() {
		return this.ur_type;
	}

	public MType
	getBodyType() {
		if (this.namedType() != null) {
			return this.namedType().getBody();
		}
		return this;
	}

	public NamedType
	namedType() {
		return null;
	}

	public IntegerType
	asInteger() {
		throw new RuntimeException("Must only be used after ANY_INTEGER.ensureCanAssignFrom!");
	}


	/**
	 * Value-based checks; these are only used after type checking has been passed
	 *
	 * @param node
	 * @param value
	 */
	public void ensureValueIsAssignable(ASTNode<?> node, Value value) {
		if (!this.allowsValue(value.getValue(), node.config())) {
			throw new DynamicTypeError(node.line(), "Value " + value + " not allowed for type " + this);
		}
	}

	/**
	 * Value-based checks; these are only used after type checking has been passed
	 *
	 * @param node
	 * @param value
	 */
	protected boolean allowsValue(Object value, Configuration config) {
		return true;
	}

	private boolean ur_type = false;
	public MType
	makeUrType() {
		this.ur_type = true;
		return this;
	}

	public StaticTypeError
	ensureCanAssignFromStatic(ASTNode<?> node, MType source) {
		if (source == ERROR) { // already handled elsewhere
			return null;
		}
		if (!source.convertibleTo(this, node.config())) {
			return new StaticTypeError(node.line(), "Cannot assign " + source + " to " + this);
		}
		return null;
	}

	public abstract Value getDefaultValue(ASTNode node);

	public static MType ANY = new IntegerType() {
		@Override
		public boolean convertibleFromForeignType(MType other, Configuration config) {
			return true;
		}

		@Override
		public String
		toString() {
			return "Any";
		}

		@Override
		public Value getDefaultValue(ASTNode node) {
			return Value.NOTHING;
		}
	};

	public static MType ERROR = new MType() {
		@Override
		public boolean convertibleFromForeignType(MType other, Configuration config) {
			return true;
		}

		@Override
		public String
		toString() {
			return "ERROR";
		}

		@Override
		public Value getDefaultValue(ASTNode node) {
			return Value.NOTHING;
		}

		@Override
		public IntegerType asInteger() {
			return ANY_INTEGER;
		}
	};

	// the integer top element (for integer literals)
	public static class IntegerType extends MType {
		@Override
		public String
		toString() {
			return "INTEGER";
		}

		@Override
		public Value getDefaultValue(ASTNode node) {
			return new Value(this, 0);
		}

		// Return the more specific type or null for failure
		public IntegerType
		plusMerge(IntegerType other, Configuration config) {
			if (other.convertibleTo(this, config)) {
				return this;
			}
			if (this.convertibleTo(other, config)) {
				return other;
			}
			if (other instanceof IntegerType && other instanceof IntegerType) {
				return (IntegerType) INTEGER;
			}
			return null;
		}

		@Override
		public boolean
		isNontrivialSubtypeOf(MType other, Configuration config) {
			return other instanceof IntegerType && (!(other instanceof SubrangeType));
		}

		public boolean
		valueEquals(Object v1, Object v2, Configuration config) {
			return v1.equals(v2);
		}

		public IntegerType
		asInteger() {
			return this;
		}
	};

	public static MType UR_INTEGER = new IntegerType() {
		@Override
		public boolean
		isUrType() {
			return true;
		}

		@Override
		public boolean
		isStructurallyEqual(MType other, MTypeComparison comp) {
			return other == UR_INTEGER || other == INTEGER || other == ANY_INTEGER;
		}

	};

	public static MType INTEGER = new IntegerType() {
		@Override
		public boolean convertibleFromForeignType(MType other, Configuration config) {
			return config.type_names_TYPE.get().normaliseType(other) instanceof IntegerType;
			//|| (other instanceof NamedType && config.type_names_TYPE.get().normaliseType(other).convertibleTo(this, config));
		}

		@Override
		public boolean
		isStructurallyEqual(MType other, MTypeComparison comp) {
			return other == UR_INTEGER || other == INTEGER || other == ANY_INTEGER;
		}
	};

	// A type that is compatible with any integer type
	public static IntegerType ANY_INTEGER = new IntegerType() {
		@Override
		public boolean convertibleFromForeignType(MType other, Configuration config) {
			return other instanceof IntegerType;
		}

		@Override
		public boolean
		isStructurallyEqual(MType other, MTypeComparison comp) {
			return other == UR_INTEGER || other == INTEGER || other == ANY_INTEGER;
		}
	};

	public static MType UNIT = new MType() {
		@Override
		public String
		toString() {
			return "UNIT";
		}

		@Override
		public Value getDefaultValue(ASTNode node) {
			return new Value(this, Value.NOTHING);
		}
	};

	public static class SubrangeType extends IntegerType {
		private int min, max;

		public SubrangeType(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public int
		getMin() {
			return this.min;
		}

		public int
		getMax() {
			return this.max;
		}

		@Override
		public boolean isStructurallyEqual(MType other, MTypeComparison comp) {
			if (other instanceof SubrangeType) {
				SubrangeType other_interval = (SubrangeType) other;
				return other_interval.min == this.min && other_interval.max == this.max;
			}
			return false;
		}

		@Override
		public String
		toString() {
			return "[" + this.min + " TO " + this.max + "]";
		}

		protected boolean allowsValue(Object value, Configuration config) {
			int v = (Integer) value;
			return v >= this.min && v <= this.max;
		}

		@Override
		public Value getDefaultValue(ASTNode node) {
			return new Value(this, this.min);
		}

		@Override
		public boolean
		isNontrivialSubtypeOf(MType other, Configuration config) {
			if (other instanceof IntegerType && (!(other instanceof SubrangeType))) {
				return true;
			}
			if (other instanceof SubrangeType) {
				SubrangeType other_s = (SubrangeType)other;
				if (this.getMin() < other_s.getMin()) {
					return false;
				}
				if (this.getMax() > other_s.getMax()) {
					return false;
				}
				return true;
			}
			return false;
		}

		public boolean convertibleFromForeignType(MType other, Configuration config) {
			// CONFIG DEPENDENT
			if (other instanceof SubrangeType) {
				SubrangeType other_s = (SubrangeType)other;
				if (this.getMin() > other_s.getMax()) {
					return false;
				}
				if (this.getMax() < other_s.getMin()) {
					return false;
				}
				return true; // still needs dynamic check
			}
			//return other == this || other == UR_INTEGER;
			return other instanceof IntegerType;
		}
	}

	public static class ArrayType extends MType {
		private SubrangeType index;
		private MType values;

		public ArrayType(SubrangeType index, MType values) {
			this.index = index;
			this.values = values;
		}

		public MType
		getValues() {
			return this.values;
		}

		public SubrangeType
		getIndices() {
			return this.index;
		}

		@Override
		public boolean
		valueEquals(Object v1, Object v2, Configuration config) {
			if (v1 instanceof VariableVector && v2 instanceof VariableVector) {
				return config.array_equality.get().isEqual((VariableVector) v1, (VariableVector) v2, config);
			}
			return false;
		}

		@Override
		public boolean isStructurallyEqual(MType other, MTypeComparison comp) {
			if (this == other) {
				return true;
			}
			if (other instanceof ArrayType) {
				ArrayType o = (ArrayType) other;
				return comp.isEq(o.index, this.index)
						&& comp.isEq(o.values, this.values);
			}
			return false;
		}

		public boolean
		isUrType() {
			// make config dependent
			return true; // right now all arrays are ur-arrays
		}

		int
		length() {
			return this.index.getMax() - this.index.getMin() + 1;
		}

		public int
		startOffset() {
			return this.index.getMin();
		}

		@Override
		public Value getDefaultValue(ASTNode node) {
			if (this.length() > Configuration.ARRAY_SIZE_LIMIT) {
				throw new MysteryLimitException(node.line(), "Array too large");
			}
			Variable[] vars = new Variable[this.length()];
			for (int i = 0; i < vars.length; i++) {
				vars[i] = new Variable(this.values, "<in-array>");
				vars[i].setValue(this.values.getDefaultValue(node), null);
			}
			return new Value(this, new VariableVector(vars, this.startOffset()));
		}

		@Override
		public ArrayType
		getArrayType(ASTNode<?> node) {
			return this;
		}


		@Override
		public String toString() {
			return "ARRAY " + this.index + " OF " + this.values;
		}
	}

	public static class ProcedureType extends MType {
		private MType[] args;
		private MType ret;

		public ProcedureType(MType[] args, MType ret) {
			this.args = args;
			this.ret = ret;
		}

		public MType
		getRet() {
			return this.ret;
		}

		public MType[]
		getArgs() {
			return this.args;
		}

		@Override
		public boolean isNontrivialSubtypeOf(MType other_, Configuration config) {
			if (!(other_ instanceof ProcedureType)) {
				return false;
			}
			ProcedureType other = (ProcedureType) other_;

			if (other.getArgs().length != this.getArgs().length) {
				return false;
			}
			if (!config.procedure_return_subtyping.get().isSubtypeOf(this.getRet(), other.getRet(), config)) {
				return false;
			}
			for (int i = 0; i < this.getArgs().length; i++) {
				if (!config.procedure_arg_subtyping.get().isSubtypeOf(this.getArgs()[i], other.getArgs()[i], config)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean isStructurallyEqual(MType other, MTypeComparison comp) {
			if (other == this) {
				return true;
			}
			if (other instanceof ProcedureType) {
				ProcedureType other_fun = (ProcedureType) other;
				if (!comp.isEq(other_fun.ret, this.ret)) {
					return false;
				}
				if (other_fun.args.length != this.args.length) {
					return false;
				}
				for (int i = 0; i < this.args.length; i++) {
					if (!comp.isEq(other_fun.args[i], this.args[i])) {
						return false;
					}
				}
				return true;
			}
			return false;
		}

		@Override
		public ProcedureType
		getProcedureType(ASTNode<?> node) {
			return this;
		}

		@Override
		public String
		toString() {
			StringBuffer sb = new StringBuffer("PROCEDURE(");
			boolean first = true;
			for (MType arg : this.args) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(arg.toString());
			}
			sb.append(") : ");
			sb.append(this.ret.toString());
			return sb.toString();
		}

		@Override
		public Value getDefaultValue(ASTNode node) {
			return new Value(this, new Closure(null, null) {
				@Override
				public Value
				call(mystery2020.runtime.Runtime rt, int line_nr, VariableVector args) {
					throw new CallToNothingException(line_nr);
				}
			});

		}
	}

	public static class NamedType extends MType {
		private String name;
		private MType type;

		public NamedType(String name, MType type) {
			this.name = name;
			this.type = type;
		}

		private MType
		getBodySingle() {
			return this.type;
		}

		public MType
		getBody() {
			MType t = this.type;
			while (t.namedType() != null) {
				t = t.namedType().getBody();
			}
			return t;
		}

		public void
		setBody(MType ty) {
			this.type = ty;
		}

		@Override
		public boolean convertibleFromForeignType(MType other, Configuration config) {
			return other.isUrType() && other.convertibleTo(this.type, config);
		}

		@Override
		public Value getDefaultValue(ASTNode node) {
			return new Value(this, this.type.getDefaultValue(node).getValue());
		}

		public IntegerType
		asInteger() {
			return this.type.asInteger();
		}

		@Override
		public String
		toString() {
			return this.name;
		}

		@Override
		public NamedType
		namedType() {
			return this;
		}
	}

	public static SubrangeType SUBRANGE(final MinMax minmax) {
		return new SubrangeType(minmax.getMin(), minmax.getMax());
	}

	public static MType PROCEDURE(MType[] arg, MType ret) {
		return new ProcedureType(arg, ret);
	}

	public static MType ARRAY(final MinMax index, MType values) {
		return new ArrayType(SUBRANGE(index), values);
	}

	public static NamedType NAMED(final String name, final MType body) {
		return new NamedType(name, body);
	}

	public static int
	parseInt(int line, int column, String number) {
		try {
			return Integer.parseInt(number);
		} catch (NumberFormatException exn) {
			throw new IntLiteralException(line, column, number);
		}
	}
}
