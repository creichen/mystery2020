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

	public static ScopingMode Static = new ScopingMode("Static Scoping", "S") {
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
		public Variable
		getVariable(Runtime rt, VarDecl var) {
			return rt.getStack().getVariable(var.accessDepth(), var.accessIndex());
		}
	};

	public static ScopingMode Dynamic = new ScopingMode("Dynamic Scoping", "D") {

		@Override
		public Decl getDeclaration(Runtime rt, ID id) {
			Decl decl = rt.getStack().findDeclaration(id.getName());
			if (decl == null) {
				throw new NameException(id.line(), "Name not found in dynamic scop: '"+id.getName()+"'");
			}
			return decl;
		}

		@Override
		public Decl getDeclaration(Runtime rt, NamedType id) {
			Decl decl = rt.getStack().findDeclaration(id.getName());
			if (decl == null) {
				throw new NameException(id.line(), "Name not found in dynamic scop: '"+id.getName()+"'");
			}
			return decl;
		}

		@Override
		public Variable getVariable(Runtime rt, VarDecl vardecl) {
			return rt.getStack().getVariable(vardecl);
		}
	};
}
