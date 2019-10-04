package mystery2020;

import mystery2020.runtime.Value;

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

	public static MType INTEGER = new MType() {
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
			return new Value(ANY, 0);
		}
	};
	
	static class SubrangeType implements MType {
		private int min, max;

		public SubrangeType(int min, int max) {
			this.min = min;
			this.max = max;
		}
		
		@Override
		public boolean convertibleFrom(MType other, Configuration config) {
			// TODO Auto-generated method stub
			// CONFIG DEPENDENT
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
			return new Value(ANY, this.min);
		}
	}
	
	public static MType SUBRANGE(final MinMax minmax) {
		return new SubrangeType(minmax.getMin(), minmax.getMax());
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
