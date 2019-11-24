package mystery2020.runtime;

import AST.Decl;
import AST.ID;
import AST.NamedType;
import mystery2020.AbstractConfigOption;

public abstract class ClosureEnvironmentBinding extends AbstractConfigOption<ClosureEnvironmentBinding> {
	public ClosureEnvironmentBinding(String name, String code) {
		super(name, code);
	}
	
	public abstract Value callClosure(Closure closure, Runtime rt, int line_nr, VariableVector args);
	
	public abstract Decl
	getDeclaration(Runtime rt, ID id);

	public abstract Decl
	getDeclaration(Runtime rt, NamedType id);


	public static ClosureEnvironmentBinding Deep = new ClosureEnvironmentBinding("Deep Binding", "D") {
		@Override
		public Decl
		getDeclaration(Runtime rt, ID id) {
			return id.staticDeclaration();
		}

		@Override
		public Decl
		getDeclaration(Runtime rt, NamedType id) {
			return id.staticDeclaration();
		}

		@Override
		public Value callClosure(Closure closure, Runtime rt, int line_nr, VariableVector args) {
			VariableStack old_env = rt.getStack();
		
			// prepare env for call
			VariableStack call_env = closure.env.copy();
			ActivationRecord arecord = new ActivationRecord(args, closure.proc.getDecls());
			closure.checkAndAdaptActuals(rt, line_nr, arecord);
			call_env.push(arecord);
			rt.setStack(call_env);
		
			// call
			Value result = closure.proc.getBody().run(rt);
			// return to callee
			rt.setStack(old_env);
			return result;
		}
	};

	public static ClosureEnvironmentBinding Shallow = new ClosureEnvironmentBinding("Shallow Binding", "S") {
		@Override
		public Decl
		getDeclaration(Runtime rt, ID id) {
			return id.staticDeclaration();
		}

		@Override
		public Decl
		getDeclaration(Runtime rt, NamedType id) {
			return id.staticDeclaration();
		}

		@Override
		public Value callClosure(Closure closure, Runtime rt, int line_nr, VariableVector args) {
			// FIXME: this is completely broken!
			ActivationRecord arecord = new ActivationRecord(args, closure.proc.getDecls());
			rt.pushLookupDepthLimit();
			rt.getStack().push(arecord);
			Value v = closure.proc.getBody().run(rt);
			rt.getStack().pop();
			rt.popLookupDepthLimit();
			return v;
		}
	};

}
