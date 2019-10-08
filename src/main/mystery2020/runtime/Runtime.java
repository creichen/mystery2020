package mystery2020.runtime;

import java.util.ArrayList;
import java.util.List;

import AST.Decl;
import AST.Expr;
import AST.ID;
import AST.VarDecl;

/**
 * Aggregated runtime information
 *  
 * @author creichen
 *
 */
public class Runtime {
	public ArrayList<String> output = new ArrayList<>();
	public VariableStack stack = VariableStack.createEmpty();
	
	public Runtime() {
	}
	
	public VariableStack
	getStack() {
		return this.stack;
	}
	
	public void
	setStack(VariableStack new_stack) {
		this.stack = new_stack;
	}
	
	/**
	 * Executed before each statement (to bound steps) 
	 */
	public void
	runStep() {
	}
	
	/**
	 * Execute before each call (to bound recursion depth)
	 */
	public void
	runCall() {
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
		// FIXME: static scoping only for now
		return id.staticDeclaration();
	}
	
	public Variable
	getVariable(VarDecl var) {
		// FIXME: static scoping only for now
		return this.getStack().getVariable(var.accessDepth(), var.accessIndex());
	}

	private Variable
	prepareCallArgument(Expr expr) {
		// FIXME: pass-by-value only for now
		Value v = expr.eval(this);
		Variable var = new Variable(v.getType(), null);
		var.setValue(v);
		return var; 
	}
	
	/**
	 * Computes a variable vector that represents the call arguments irrespective of parameter passing mode. 
	 *
	 * @param args
	 * @return
	 */
	public VariableVector
	prepareCallArguments(AST.List<Expr> args) {
		Variable[] vars = new Variable[args.getNumChild()];
		args.config().parameter_evaluation_order.get().prepareCallArguments(vars, args, e -> this.prepareCallArgument(e));
		return new VariableVector(vars);
	}
	
	@Override
	public String
	toString() {
		return "RT output=" + this.output + "\n" + this.stack;
	}
}