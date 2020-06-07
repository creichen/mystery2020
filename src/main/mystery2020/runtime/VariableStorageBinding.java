package mystery2020.runtime;

import AST.ASTNode;
import mystery2020.AbstractConfigOption;
import mystery2020.NameException;
import java.util.IdentityHashMap;

public abstract class VariableStorageBinding extends AbstractConfigOption<VariableStorageBinding> {

	public VariableStorageBinding(String name, String code) {
		super(name, code);
	}

	public abstract VariableVector
	getVariableVectorInstance(VariableVector vec, ASTNode node);

	public static VariableStorageBinding StackDynamic = new VariableStorageBinding("Stack-Dynamic", "SD") {

		@Override
		public VariableVector
		getVariableVectorInstance(VariableVector vec, ASTNode node) {
			return vec.instantiate(node);
		}
	};

	public static VariableStorageBinding Static = new VariableStorageBinding("Static", "S") {

		private IdentityHashMap<ASTNode, VariableVector> static_storage = new IdentityHashMap<>();

		@Override
		public VariableVector
		getVariableVectorInstance(VariableVector vec, ASTNode node) {
			if (!this.static_storage.containsKey(node)) {
				this.static_storage.put(node, vec.instantiate(node));
			}
			return this.static_storage.get(node);
		}
	};

}
