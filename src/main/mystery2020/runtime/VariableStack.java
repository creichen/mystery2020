package mystery2020.runtime;

import java.util.Stack;

/**
 * A layered stack of locals.
 * 
 * Locals at level 0 are the globals.
 *
 * @author creichen
 *
 */
public class VariableStack {
	private Stack<VariableVector> stack;

	private VariableStack(Stack<VariableVector> new_stack) {
		this.stack = new_stack;
	}
	
	public Variable
	getVariable(int depth, int index) {
		if (depth >= this.stack.size()) {
			throw new RuntimeException("Invalid depth " + depth + ": max is " + this.stack.size());
		}
		return this.stack.get(depth).get(index);
	}
	
	public void
	pop() {
		this.stack.pop();
	}
	
	public void
	push(VariableVector vv) {
		this.stack.push(vv);
	}
	
	public static VariableStack
	createEmpty() {
		return new VariableStack(new Stack<>());
	}
	
	@Override
	public String
	toString() {
		StringBuffer sb = new StringBuffer("VariableStack:\n");
		for (int i = 0; i < this.stack.size(); i++) {
			sb.append(i + ": " + this.stack.get(i).toString());
		}
		return sb.toString();
	}
}
