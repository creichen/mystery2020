package mystery2020.runtime;

import mystery2020.AbstractConfigOption;
import mystery2020.Configuration;
import mystery2020.MType;

public abstract class ProcedureSubtyping extends AbstractConfigOption<ProcedureSubtyping> {

	public ProcedureSubtyping(String name, String code) {
		super(name, code);
	}

	public abstract boolean
	isSubtypeOf(MType tleft, MType tright, Configuration config);

	public static ProcedureSubtyping Invariant = new ProcedureSubtyping("Equal", "=") {
		@Override
		public boolean isSubtypeOf(MType tleft, MType tright, Configuration config) {
			return tleft.equalTo(tright, config);
		}
	};

	public static ProcedureSubtyping Covariant = new ProcedureSubtyping("Covariant", "+") {
		@Override
		public boolean isSubtypeOf(MType tleft, MType tright, Configuration config) {
			return tleft.isSubtypeOf(tright, config);
		}
	};

	public static ProcedureSubtyping Contravariant = new ProcedureSubtyping("Contravariant", "-") {
		@Override
		public boolean isSubtypeOf(MType tleft, MType tright, Configuration config) {
			return tright.isSubtypeOf(tleft, config);
		}
	};

	public static ProcedureSubtyping Bivariant = new ProcedureSubtyping("Bivariant", "_") {
		@Override
		public boolean isSubtypeOf(MType tleft, MType tright, Configuration config) {
			return true;
		}
	};
}
