package mystery2020.runtime;

import AST.ProcDecl;
import mystery2020.ParameterCountMismatch;

public class Closure {
	private ProcDecl proc;
	private VariableStack env;
	
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
	getFormals() {
		return this.proc.variableVector();
	}

	/**
	 * Checks arguments and types for parameters, sets their names
	 * 
	 * @param line_nr
	 * @param args
	 */
	public void
	checkAndAdaptActuals(int line_nr, VariableVector actuals) {
		VariableVector formals = this.getFormals();
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
		VariableStack old_env = rt.getStack();
		
		// prepare env for call
		VariableStack call_env = this.env.copy();
		checkAndAdaptActuals(line_nr, args);
		call_env.push(args);
		rt.setStack(call_env);
		
		// call
		Value result = this.proc.getBody().run(rt);
		// return to callee
		rt.setStack(old_env);

		if (result == null) {
			return Value.NOTHING; 
		}
		return result;
	}
}
