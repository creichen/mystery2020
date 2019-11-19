package mystery2020;

import mystery2020.runtime.Closure;
import mystery2020.runtime.Value;
import mystery2020.runtime.Variable;
import mystery2020.runtime.VariableVector;

public interface MType {
	/**
	 * Check whether the other type can be widened to this type
	 *
	 * @param other
	 * @param config
	 * @return true iff the other type can be converted to this type
	 */
	public boolean convertibleFrom(MType other, Configuration config);
	
	public default boolean
	convertibleTo(MType other, Configuration config) {
		return other.convertibleFrom(this, config);
	}
	
	public boolean
	equalTo(MType other, Configuration config);
	
	public default boolean
	valueEquals(Object v1, Object v2, Configuration config) {
		return v1.equals(v2);
	}

	public Value getDefaultValue();
	
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
	
	public static MType UR_INTEGER = new MType() {
		// for integer literals
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
	};
	
	public static MType INTEGER = new MType() {
		@Override
		public boolean convertibleFrom(MType other, Configuration config) {
			return other == this || other == UR_INTEGER;
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
	
	static class SubrangeType implements MType {
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
		public boolean convertibleFrom(MType other, Configuration config) {
			// FIXME: CONFIG DEPENDENT
			return false;
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
	}
	
	static class ArrayType implements MType {
		private SubrangeType index;
		private MType values;
		
		public ArrayType(SubrangeType index, MType values) {
			this.index = index;
			this.values = values;
		}

		@Override
		public boolean convertibleFrom(MType other, Configuration config) {
			return this == other;
		}

		@Override
		public boolean equalTo(MType other, Configuration config) {
			return this == other;
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
		public String toString() {
			return "ARRAY " + this.index + " OF " + this.values;
		}
	}

	static class ProcedureType implements MType {
		private MType[] args;
		private MType ret;

		public ProcedureType(MType[] args, MType ret) {
			this.args = args;
			this.ret = ret;
		}
		
		@Override
		public boolean convertibleFrom(MType other, Configuration config) {
			// FIXME: CONFIG DEPENDENT
			return false;
		}

		@Override
		public boolean equalTo(MType other, Configuration config) {
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
	
	static class NamedType implements MType {
		private String name;
		private MType type;
		
		public NamedType(String name, MType type) {
			this.name = name;
			this.type = type;
		}

		@Override
		public boolean convertibleFrom(MType other, Configuration config) {
			// TODO Auto-generated method stub
			return false;
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
