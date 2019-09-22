package mystery2020;

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
	};
	
	static class IntervalType implements MType {
		private int min, max;

		public IntervalType(int min, int max) {
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
			if (other instanceof IntervalType) {
				IntervalType other_interval = (IntervalType) other;
				return other_interval.min == this.min && other_interval.max == this.max;
			}
			return false;
		}

		@Override
		public String
		toString() {
			return "[" + this.min + " TO " + this.max + "]";
		}
	}
	
	public static MType INTERVAL(final int min, final int max) {
		return new IntervalType(min, max);
	}
}
