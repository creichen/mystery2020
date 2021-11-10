package mystery2020.runtime;

import mystery2020.AbstractConfigOption;
import mystery2020.MType;
import mystery2020.MinMax;
import AST.Number;

public abstract class LiteralType extends AbstractConfigOption<LiteralType> {

	public LiteralType(String name, String code) {
		super(name, code);
	}

	public abstract
	MType type(Number n);

	public Value
	value(Number n) {
		return new Value(this.type(n), n.literalInt());
	}

	public static LiteralType Integer = new LiteralType("Integer", "I") {
		@Override
		public MType type(Number n) {
			return MType.UR_INTEGER;
		}
	};

	public static LiteralType Subrange = new LiteralType("Subrange", "S") {
		@Override
		public MType type(Number n) {
			int v = n.literalInt();
			return MType.SUBRANGE(new MinMax(n.line(), n.column(), v, v));
		}
	};
}
