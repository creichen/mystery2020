package mystery2020.runtime;

import mystery2020.AbstractConfigOption;

public class TypeCheck extends AbstractConfigOption<TypeCheck> {

	public TypeCheck(String name, String code) {
		super(name, code);
	}

	public boolean
	dynamic_checks() {
		return false;
	}

	public boolean
	static_checks() {
		return false;
	}

	public static TypeCheck Dynamic = new TypeCheck("Dynamic", "D") {
		@Override
		public boolean
		dynamic_checks() {
			return true;
		}
	};

	public static TypeCheck Static = new TypeCheck("Static", "S") {
		@Override
		public boolean
		static_checks() {
			return true;
		}
	};

	public static TypeCheck None = new TypeCheck("None", "N") {
	};
}
