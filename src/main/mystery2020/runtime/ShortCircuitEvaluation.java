package mystery2020.runtime;

import mystery2020.AbstractConfigOption;
import AST.BinArguments;

public abstract class ShortCircuitEvaluation extends AbstractConfigOption<ShortCircuitEvaluation> {

	public ShortCircuitEvaluation(String name, String code) {
		super(name, code);
	}
	
	public abstract void
	preprocessArgs(BinArguments args, Runtime rt);
	
	public abstract boolean
	isShortCircuit();

	public static ShortCircuitEvaluation ON = new ShortCircuitEvaluation("Enabled", "Y") {
		@Override
		public void
		preprocessArgs(BinArguments args, Runtime rt) {
		}
		
		public boolean
		isShortCircuit() {
			return true;
		}

	};
	
	public static ShortCircuitEvaluation OFF = new ShortCircuitEvaluation("Disabled", "N") {
		@Override
		public void
		preprocessArgs(BinArguments args, Runtime rt) {
			args.prepare(rt);
		}
		
		public boolean
		isShortCircuit() {
			return false;
		}
	};
}
