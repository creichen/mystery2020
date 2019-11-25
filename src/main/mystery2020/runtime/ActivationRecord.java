package mystery2020.runtime;

public final class ActivationRecord {
	private VariableVector variables;
	private AST.List<? extends AST.Decl> decl_block;
	
	public ActivationRecord(VariableVector vector, AST.List<? extends AST.Decl> decls) {
		this.decl_block = decls;
		this.variables = vector;
	}
	
	public VariableVector
	getVariables() {
		return this.variables;
	}
	
	public AST.List<? extends AST.Decl>
	getDecls() {
		return this.decl_block;
	}
	
	public Variable
	get(int i) {
		return this.getVariables().get(i);
	}
	
	public int
	size() {
		return this.variables.size();
	}
	
	public String
	toString() {
		StringBuffer sb = new StringBuffer();
		for (AST.Decl decl : this.decl_block) {
			if (sb.length() != 0) {
				sb.append(" ");
			}
			sb.append(((AST.Named)decl).name());
		}
		return this.variables.toString() + " @" + System.identityHashCode(this) + " [" + sb + "]";
	}
}
