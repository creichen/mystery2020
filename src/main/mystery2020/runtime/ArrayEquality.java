package mystery2020.runtime;

import mystery2020.AbstractConfigOption;
import mystery2020.Configuration;

public abstract class ArrayEquality extends AbstractConfigOption<ArrayEquality> {

	public ArrayEquality(String name, String code) {
		super(name, code);
	}
	
	public abstract boolean isEqual(VariableVector v1, VariableVector v2, Configuration config);

	public static ArrayEquality Reference = new ArrayEquality("Reference Equality", "R") {
		@Override
		public boolean isEqual(VariableVector lhs, VariableVector rhs, Configuration config) {
			return lhs == rhs;
		}
	};

	public static ArrayEquality Structural = new ArrayEquality("Structural Equality", "S") {
		@Override
		public boolean isEqual(VariableVector lhs, VariableVector rhs, Configuration config) {
			return lhs.equalTo(rhs, config);
		}
	};

}
