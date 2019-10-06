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
			sb.append(i + ": " + this.stack.get(i).toString() + "\n");
		}
		return sb.toString();
	}

	/**
	 * Creates a shallow copy of this variable stack
	 *
	 * @return
	 */
	public VariableStack
	copyTruncated(int depth) {
		int size = this.stack.size();
		if (depth > size) {
			throw new RuntimeException("Can't truncate stack of size " + size + " to " + depth);
		}
		Stack<VariableVector> va = new Stack<>();
		for (int i = 0; i < depth; i++) {
			va.push(this.stack.get(i));
		}
		VariableStack result = new VariableStack(va);
		return result;
	}
	
	/**
	 * Creates a shallow copy of this variable stack
	 *
	 * @return
	 */
	public VariableStack
	copy() {
		Stack<VariableVector> va = new Stack<>();
		va.addAll(this.stack);
		return new VariableStack(va);
	}
}
