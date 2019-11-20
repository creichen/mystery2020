package mystery2020;

import AST.ASTNode;
import mystery2020.runtime.Closure;
import mystery2020.runtime.Value;
import mystery2020.runtime.Variable;
import mystery2020.runtime.VariableVector;

public abstract class MType {
	/**
	 * Check whether the other type can be widened to this type
	 *
	 * @param other
	 * @param config
	 * @return true iff the other type can be converted to this type
	 */
	public abstract boolean convertibleFrom(MType other, Configuration config);
	
	public boolean
	convertibleTo(MType other, Configuration config) {
		return other.convertibleFrom(this, config);
	}
	
	public abstract boolean
	equalTo(MType other, Configuration config);

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

	public abstract Value getDefaultValue();
	
	public static MType ANY = new MType() {
		@Override
		public boolean convertibleFrom(MType other, Configuration config) {
			return true;
		}

		@Override
		public boolean equalTo(MType other, Configuration config) {
			return other == this;
		}
		
		@Override
		public String
		toString() {
			return "Any";
		}

		@Override
		public Value getDefaultValue() {
			return Value.NOTHING;
		}
	};

	public static MType ERROR = new MType() {
		@Override
		public boolean convertibleFrom(MType other, Configuration config) {
			return true;
		}

		@Override
		public boolean equalTo(MType other, Configuration config) {
			return other == this;
		}
		
		@Override
		public String
		toString() {
			return "Error";
		}

		@Override
		public Value getDefaultValue() {
			return Value.NOTHING;
		}
	};

	// the integer top element (for integer literals)
	public static class IntegerType extends MType {
		@Override
		public boolean convertibleFrom(MType other, Configuration config) {
			return other == this;
			// CONFIG DEPENDENT
		}

		@Override
		public boolean equalTo(MType other, Configuration config) {
			return other == this;
		}
		
		@Override
		public String
		toString() {
			return "Integer";
		}
		
		@Override
		public Value getDefaultValue() {
			return new Value(this, 0);
		}
		
		// Return the more specific type or null for failure
		public IntegerType
		plusMerge(IntegerType other, Configuration config) {
			if (this.convertibleFrom(other, config)) {
				return this;
			}
			if (other.convertibleFrom(this, config)) {
				return other;
			}
			return null;
		}
	};
	
	public static MType UR_INTEGER = new IntegerType() {
		@Override
		public boolean convertibleFrom(MType other, Configuration config) {
			return other == this;
		}
		@Override
		public boolean
		isUrType() {
			return true;
		}
	};
	
	public static MType INTEGER = new IntegerType() {
		@Override
		public boolean convertibleFrom(MType other, Configuration config) {
			// CONFIG DEPENDENT
			//return other == this || other == UR_INTEGER;
			return other instanceof IntegerType;
		}
	};

	// A type that is compatible with any integer type
	public static MType ANY_INTEGER = new IntegerType() {
		@Override
		public boolean convertibleFrom(MType other, Configuration config) {
			return other instanceof IntegerType;
		}
	};
	
	public static MType UNIT = new MType() {
		@Override
		public boolean convertibleFrom(MType other, Configuration config) {
			return other == this;
			// CONFIG DEPENDENT
		}

		@Override
		public boolean equalTo(MType other, Configuration config) {
			return other == this;
		}
		
		@Override
		public String
		toString() {
			return "Unit";
		}
		
		@Override
		public Value getDefaultValue() {
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
		public boolean equalTo(MType other, Configuration config) {
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
		
		@Override
		public Value getDefaultValue() {
			return new Value(this, this.min);
		}

		public boolean convertibleFrom(MType other, Configuration config) {
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
		public boolean convertibleFrom(MType other, Configuration config) {
			return this.equalTo(other, config);
		}

		@Override
		public boolean equalTo(MType other, Configuration config) {
			if (this == other) {
				return true;
			}
			if (other instanceof ArrayType) {
				ArrayType o = (ArrayType) other;
				return o.index.equalTo(this.index, config)
						&& o.values.equalTo(this.values, config);
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
		public Value getDefaultValue() {
			Variable[] vars = new Variable[this.length()];
			for (int i = 0; i < vars.length; i++) {
				vars[i] = new Variable(this.values, "<in-array>");
				vars[i].setValue(this.values.getDefaultValue());
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
		public boolean convertibleFrom(MType other, Configuration config) {
			return this == other || this.equalTo(other, config);
		}

		@Override
		public boolean equalTo(MType other, Configuration config) {
			if (other == this) {
				return true;
			}
			if (other instanceof ProcedureType) {
				ProcedureType other_fun = (ProcedureType) other;
				if (!other_fun.ret.equalTo(this.ret, config)) {
					return false;
				}
				if (other_fun.args.length != this.args.length) {
					return false;
				}
				for (int i = 0; i < this.args.length; i++) {
					if (!other_fun.args[i].equalTo(this.args[i], config)) {
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
		public Value getDefaultValue() {
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

		@Override
		public boolean convertibleFrom(MType other, Configuration config) {
			return other == this
					|| other.isUrType() && this.type.convertibleFrom(other, config);
		}

		@Override
		public boolean equalTo(MType other, Configuration config) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Value getDefaultValue() {
			return new Value(this, this.type.getDefaultValue().getValue());
		}
		
		@Override
		public String
		toString() {
			return this.name;
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
	
	public static MType NAMED(final String name, final MType body) {
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
