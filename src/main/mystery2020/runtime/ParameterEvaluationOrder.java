package mystery2020.runtime;

import java.util.function.BiFunction;

import AST.Expr;
import AST.List;
import mystery2020.AbstractConfigOption;
import mystery2020.MType;

public abstract class ParameterEvaluationOrder extends AbstractConfigOption<ParameterEvaluationOrder> {

	public ParameterEvaluationOrder(String name, String code) {
		super(name, code);
	}
	
	protected abstract void
	prepareCallArguments(Variable[] vars, MType[] formal_types, List<Expr> args, BiFunction<Expr, MType, Variable> encoder);
	
	public static ParameterEvaluationOrder LeftToRight = new ParameterEvaluationOrder("Left to right", "LR") {
		@Override
		protected void
		prepareCallArguments(Variable[] vars, MType[] formal_types, List<Expr> args, BiFunction<Expr, MType, Variable> encoder) {
			for (int i = 0; i < vars.length; i++) {
				vars[i] = encoder.apply(args.getChild(i), formal_types.length > i ? formal_types[i] : MType.ANY);
			}
		}
	};
	
	public static ParameterEvaluationOrder RightToLeft = new ParameterEvaluationOrder("Right to left", "RL") {
		@Override
		protected void
		prepareCallArguments(Variable[] vars, MType[] formal_types, List<Expr> args, BiFunction<Expr, MType, Variable> encoder) {
			for (int i = vars.length - 1; i >= 0; i--) {
				vars[i] = encoder.apply(args.getChild(i), formal_types.length > i ? formal_types[i] : MType.ANY);
			}
		}
	};
}
