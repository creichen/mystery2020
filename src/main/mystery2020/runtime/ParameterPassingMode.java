package mystery2020.runtime;

import AST.Expr;
import mystery2020.AbstractConfigOption;
import mystery2020.MType;

public abstract class ParameterPassingMode extends AbstractConfigOption<ParameterPassingMode> {
	public ParameterPassingMode(String name, String code) {
		super(name, code);
	}
	
	// before call
	public abstract Variable prepareParameter(Runtime runtime, Expr expr);
	// after call
	public abstract void postprocessParameter(Runtime runtime, Variable var, Expr expr);
	
	protected Variable
	createValueParameter(Runtime rt, Expr expr) {
		Value v = expr.eval(rt);
		Variable var = new Variable(v.getType(), null);
		var.setValue(v.getType().getDefaultValue(), null); // may be needed for array copying
		var.setValue(v, rt.getConfiguration());
		return var;
	}
	
	protected MType
	getType(Runtime rt, Expr expr) {
		return MType.ANY;
	}

	protected void
	copyVariableToTarget(Runtime rt, Value val, Expr target) {
		Variable var = target.variable(rt);
		if (var != null) {
			var.checkAndSetValue(target, var.getValue(), rt.getConfiguration());
		}
	}
	
	public boolean
	assignabilityDynamicallyCheckableAtCallTime() {
		return this.typeDynamicallyCheckableAtCallTime();
	}

	/**
	 * Otherwise the PrameterPassingMode has to do dynamic type checking by hand (if appropriate) 
	 *
	 * @return
	 */
	public boolean
	typeDynamicallyCheckableAtCallTime() {
		return true;
	}

	// ========== 
	public static ParameterPassingMode ByValue = new ParameterPassingMode("Pass by Value", "V") {
		@Override
		public Variable prepareParameter(Runtime rt, Expr expr) {
			return this.createValueParameter(rt, expr);
		}

		@Override
		public void
		postprocessParameter(Runtime rt, Variable var, Expr expr) {
		}
	};

	// ========== 
	public static ParameterPassingMode ByResult = new ParameterPassingMode("Pass by Result", "R") {

		@Override
		public Variable prepareParameter(Runtime rt, Expr expr) {
			Variable var = expr.variable(rt);
			if (var == null) {
				expr.eval(rt);
				var = new Variable(MType.ANY, "<temp>");
			}
			return new Variable.WriteOnlyProxy(var, rt.getConfiguration(), expr); 
		}

		@Override
		public void
		postprocessParameter(Runtime rt, Variable var, Expr expr) {
			((Variable.Proxy)var).writeToRemote();
		}

		@Override
		public boolean
		typeDynamicallyCheckableAtCallTime() {
			return false;
		}
	};
	
	// ========== 
	public static ParameterPassingMode ByValueResult = new ParameterPassingMode("Pass by Value-Result", "C") {

		@Override
		public Variable prepareParameter(Runtime rt, Expr expr) {
			Variable var = expr.variable(rt);
			if (var == null) {
				Value val = expr.eval(rt);
				var = new Variable(val.getType(), "<temp>");
				var.checkAndSetValue(expr, val, rt.getConfiguration());
			}
			Variable.Proxy proxy_var = new Variable.Proxy(var, rt.getConfiguration());
			proxy_var.loadFromRemote();
			return proxy_var;
		}

		@Override
		public void
		postprocessParameter(Runtime rt, Variable var, Expr expr) {
			((Variable.Proxy)var).writeToRemote();
		}
	};
	
	// ========== 
	public static ParameterPassingMode ByReference = new ParameterPassingMode("Pass by Reference", "P") {

		@Override
		public Variable prepareParameter(Runtime rt, Expr expr) {
			Variable var = expr.variable(rt);
			if (var != null) {
				return var;
			}
			return this.createValueParameter(rt, expr);
		}

		@Override
		public void
		postprocessParameter(Runtime rt, Variable var, Expr expr) {
		}
	};
	
	// ========== 
	public static ParameterPassingMode ByName = new ParameterPassingMode("Pass by Name", "N") {

		@Override
		public Variable prepareParameter(Runtime rt, Expr expr) {
			return new Variable.Lazy(this.getType(rt, expr), "<outmode-arg>", expr, rt);
		}

		@Override
		public void
		postprocessParameter(Runtime rt, Variable var, Expr expr) {
		}

		@Override
		public boolean
		typeDynamicallyCheckableAtCallTime() {
			return false;
		}
	};
	
	public static ParameterPassingMode ByNeed = new ParameterPassingMode("Pass by Need", "E") {

		@Override
		public Variable prepareParameter(Runtime rt, Expr expr) {
			return new Variable.LazyCached(this.getType(rt, expr), "<outmode-arg>", expr, rt); 
		}

		@Override
		public void
		postprocessParameter(Runtime rt, Variable var, Expr expr) {
		}

		@Override
		public boolean
		typeDynamicallyCheckableAtCallTime() {
			return false;
		}
	};
}
