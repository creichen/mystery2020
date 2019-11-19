package mystery2020.runtime;

import mystery2020.AbstractConfigOption;
import AST.BinArguments;

public abstract class OperandEvaluationOrder extends AbstractConfigOption<OperandEvaluationOrder> {

	public OperandEvaluationOrder(String name, String code) {
		super(name, code);
	}
	
	public void
	prepare(BinArguments args, Runtime rt) {
		this.getValue(args, rt, true);
		this.getValue(args, rt, false);
	}
	
	public abstract Value
	getValue(BinArguments args, Runtime rt, boolean first);
	
	public static OperandEvaluationOrder LeftToRight = new OperandEvaluationOrder("Left to Right", "LR") {
		@Override
		public Value
		getValue(BinArguments args, Runtime rt, boolean first) {
			if (first) {
				return args.getLHS(rt);
			} else {
				return args.getRHS(rt);
			}
		}
	};
	
	public static OperandEvaluationOrder RightToLeft = new OperandEvaluationOrder("Right to Left", "RL") {
		@Override
		public Value
		getValue(BinArguments args, Runtime rt, boolean first) {
			if (first) {
				return args.getRHS(rt);
			} else {
				return args.getLHS(rt);
			}
		}
	};

}
