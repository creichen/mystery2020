package mystery2020.runtime;

import AST.ProcDecl;
import mystery2020.ParameterCountMismatch;

public class Closure {
	ProcDecl proc;
	VariableStack env;

	public Closure(ProcDecl proc, VariableStack env) {
		this.proc = proc;
		this.env = env;
	}

	/**
	 * Get a new Closure with an updated environment
	 *
	 * Needed by shallow and ad-hoc binding.
	 *
	 * @param env
	 * @return
	 */
	public Closure
	withEnv(VariableStack env) {
		return new Closure(this.proc, env);
	}

	public VariableVector
	getFormals(Runtime rt) {
		return this.proc.variableVector(rt);
	}

	/**
	 * Checks arguments and types for parameters, sets their names
	 *
	 * @param line_nr
	 * @param args
	 */
	public void
	checkAndAdaptActuals(Runtime rt, int line_nr, ActivationRecord actuals) {
		VariableVector formals = this.getFormals(rt);
		if (formals.size() != actuals.size()) {
			throw new ParameterCountMismatch(line_nr, "Expected " + formals.size() + ", got " + actuals.size());
		}
		for (int i = 0; i < formals.size(); i++) {
			// FIXME: dynamic type check as needed
			actuals.get(i).setName(formals.get(i).getName());
		}
	}

	public Value
	call(Runtime rt, int line_nr, VariableVector args) {
		if (this.env == null) {
			throw new RuntimeException("Must set environment before calling closure");
		}
		Value result = rt.getConfiguration().scoping.get().callClosure(this, rt, line_nr, args);
		if (result == null) {
			return Value.NOTHING;
		}
		return result;
	}
}
