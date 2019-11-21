package mystery2020.runtime;

import mystery2020.AbstractConfigOption;
import mystery2020.Configuration;

public abstract class ArrayAssignmentSemantics extends AbstractConfigOption<ArrayAssignmentSemantics> {

	public ArrayAssignmentSemantics(String name, String code) {
		super(name, code);
	}

	public abstract VariableVector
	assign(VariableVector lhs, VariableVector rhs, Configuration config);

	public static ArrayAssignmentSemantics Copy = new ArrayAssignmentSemantics("Copy", "C") {

		@Override
		public VariableVector assign(VariableVector lhs, VariableVector rhs, Configuration config) {
			int start = Integer.max(lhs.getOffset(), rhs.getOffset());
			int end = Integer.min(lhs.getOffset() + lhs.size() , rhs.getOffset() + rhs.size());
			for (int i = start; i < end; i++) {
				lhs.get(i).setValue(rhs.get(i).getValue(), config);
			}
			return lhs;
		}
	};
	
	public static ArrayAssignmentSemantics Reference = new ArrayAssignmentSemantics("Reference Sharing", "R") {

		@Override
		public VariableVector assign(VariableVector lhs, VariableVector rhs, Configuration config) {
			return rhs;
		}
	};


}
