package mystery2020.runtime;

import mystery2020.AbstractConfigOption;
import mystery2020.MType;
import mystery2020.StaticTypeError;
import AST.ASTNode;

public abstract class UnknownType extends AbstractConfigOption<UnknownType> {

	public UnknownType(String name, String code) {
		super(name, code);
	}

	public abstract
	MType get(ASTNode node);

	public static UnknownType Any = new UnknownType("Any", "A") {
		@Override
		public MType get(ASTNode n) {
			return MType.ANY;
		}
	};

	public static UnknownType Integer = new UnknownType("Integer", "I") {
		@Override
		public MType get(ASTNode n) {
			return MType.INTEGER;
		}
	};

	public static UnknownType Error = new UnknownType("Error", "E") {
		@Override
		public MType get(ASTNode n) {
			throw new StaticTypeError(n.line(), "Unspecified type");
		}
	};
}
