package mystery2020.runtime;

import AST.Decl;
import AST.ID;
import AST.NamedType;
import AST.VarDecl;
import mystery2020.AbstractConfigOption;
import mystery2020.NameException;

public abstract class ScopingMode extends AbstractConfigOption<ScopingMode> {

	public ScopingMode(String name, String code) {
		super(name, code);
	}

	public abstract Decl
	getDeclaration(Runtime rt, ID id);

	public abstract Decl
	getDeclaration(Runtime rt, NamedType id);

	public abstract Variable
	getVariable(Runtime rt, VarDecl vardecl);

	public abstract Value
	callClosure(Closure closure, Runtime rt, int line_nr, VariableVector args);

	public static ScopingMode Static = new ScopingMode("Static Scoping", "S") {

		@Override
		public Value
		callClosure(Closure closure, Runtime rt, int line_nr, VariableVector args) {
			return rt.getConfiguration().closure_env_binding.get().callClosure(closure, rt, line_nr, args);
		}

		@Override
		public Decl
		getDeclaration(Runtime rt, ID id) {
			return rt.getConfiguration().closure_env_binding.get().getDeclaration(rt, id);
		}

		@Override
		public Decl
		getDeclaration(Runtime rt, NamedType id) {
			return rt.getConfiguration().closure_env_binding.get().getDeclaration(rt, id);
		}

		@Override
		public Variable
		getVariable(Runtime rt, VarDecl var) {
			return rt.getStack().getVariable(var.accessDepth(), var.accessIndex());
		}
	};

	public static ScopingMode Dynamic = new ScopingMode("Dynamic Scoping", "D") {

		@Override
		public Value
		callClosure(Closure closure, Runtime rt, int line_nr, VariableVector args) {
			// prepare env for call
			VariableStack call_env = rt.getStack();
			ActivationRecord arecord = new ActivationRecord(args, closure.proc.getDecls());
			closure.checkAndAdaptActuals(rt, line_nr, arecord);
			call_env.push(arecord);
			//System.err.println("------ REC CALL: pushing to make\n" + call_env);

			// call
			Value result = closure.proc.getBody().run(rt);
			// return to callee
			call_env.pop();
			//System.err.println("------ pop");
			return result;
		}


		@Override
		public Decl getDeclaration(Runtime rt, ID id) {
			Decl decl = rt.getStack().findDeclaration(id.getName());
			if (decl == null) {
				throw new NameException(id.line(), "Name not found in dynamic scope: '"+id.getName()+"'");
			}
			return decl;
		}

		@Override
		public Decl getDeclaration(Runtime rt, NamedType id) {
			Decl decl = rt.getStack().findDeclaration(id.getName());
			if (decl == null) {
				throw new NameException(id.line(), "Name not found in dynamic scope: '"+id.getName()+"'");
			}
			return decl;
		}

		@Override
		public Variable getVariable(Runtime rt, VarDecl vardecl) {
			return rt.getStack().getVariable(vardecl);
		}
	};
}
