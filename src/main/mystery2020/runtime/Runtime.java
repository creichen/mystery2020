package mystery2020.runtime;

import java.util.ArrayList;
import java.util.List;

import AST.ASTNode;
import AST.Decl;
import AST.Expr;
import AST.ID;
import AST.NamedType;
import AST.VarDecl;
import mystery2020.Configuration;
import mystery2020.MType;
import mystery2020.MysteryLimitException;

/**
 * Aggregated runtime information
 *
 * @author creichen
 *
 */
public class Runtime {

	// This stack represents the STATIC LINK structure, not the dynamic links.
	// In other words, it reflects the static nesting structure of the program,
	// if you recurse, you'll simply keep updating the same-depth entries in it.
	public VariableStack stack = VariableStack.createEmpty();

	public ArrayList<String> output = new ArrayList<>();
	private int steps_taken = 0;
	private int calls_taken = 0;
	private int max_steps_taken = 10000;
	private int max_calls_taken = 100;
	private Configuration config;

	public Runtime(Configuration config) {
		this.config = config;
		this.setCallLimit(config.getCallLimit());
		this.setStepsLimit(config.getStepLimit());
	}

	/**
	 * Sets the maximum number of calls
	 *
	 * @param limit
	 */
	public void
	setCallLimit(int limit) {
		this.max_calls_taken = limit;
	}

	/**
	 * Sets the maximum number of stmts that will be executed
	 *
	 * @param limit
	 */
	public void
	setStepsLimit(int limit) {
		this.max_steps_taken = limit;
	}

	public VariableStack
	getStack() {
		return this.stack;
	}

	public void
	setStack(VariableStack new_stack) {
		this.stack = new_stack;
	}

	public Configuration
	getConfiguration() {
		return this.config;
	}

	/**
	 * Executed before each statement (to bound steps)
	 */
	public void
	runStep(ASTNode<?> n) {
		if (++this.steps_taken > this.max_steps_taken) {
			throw new MysteryLimitException(n.line(), "Ran for too long");
		}
	}

	/**
	 * Execute before each call (to bound recursion depth)
	 */
	public void
	runCall(ASTNode<?> n) {
		if (++this.calls_taken > this.max_calls_taken) {
			throw new MysteryLimitException(n.line(), "Ran too many calls");
		}
	}

	/**
	 * Adds a piece of output
	 * @param out The string to be output
	 */
	public void
	addOutput(String out) {
		this.output.add(out);
	}

	public List<String>
	getOutput() {
		return this.output;
	}

	public Decl
	getDeclaration(ID id) {
		return this.config.scoping.get().getDeclaration(this, id);
	}

	public Decl
	getDeclaration(NamedType id) {
		return this.config.scoping.get().getDeclaration(this, id);
	}

	public Variable
	getVariable(VarDecl var) {
		return this.config.scoping.get().getVariable(this, var);
	}

	private Variable
	prepareCallArgument(Expr expr, MType type) {
		return this.config.parameter_passing.get().prepareParameter(this, expr, type, config);
	}

	public void
	postprocessCallArguments(VariableVector actuals_vector, AST.List<Expr> original_args) {
		// currently always left-to-right
		int offset = 0;
		for (Expr expr : original_args) {
			Variable var = actuals_vector.get(offset);
			this.config.parameter_passing.get().postprocessParameter(this, var, expr);
		}
	}

	/**
	 * Computes a variable vector that represents the call arguments irrespective of parameter passing mode.
	 *
	 * @param args
	 * @return
	 */
	public VariableVector
	prepareCallArguments(AST.List<Expr> args, MType[] formal_types) {
		Variable[] vars = new Variable[args.getNumChild()];
		args.config().parameter_evaluation_order.get().prepareCallArguments(vars, formal_types, args, (e, t) -> this.prepareCallArgument(e, t));
		return new VariableVector(vars);
	}

	public void
	pushLookupDepthLimit() {
	}

	public void
	popLookupDepthLimit() {
	}

	@Override
	public String
	toString() {
		return "RT output=" + this.output + "\n" + this.stack;
	}
}
