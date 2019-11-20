package mystery2020.runtime;

import mystery2020.AbstractConfigOption;
import mystery2020.MType;

public abstract class TypeNamesTYPE extends AbstractConfigOption<TypeNamesTYPE> {

	public TypeNamesTYPE(String name, String code) {
		super(name, code);
	}

	public abstract MType normaliseType(MType type);
	// What kind of MType should a  `TYPE name = type' resolve to?
	//public abstract MType nameType(String name, MType type);
	
	public static TypeNamesTYPE Structural = new TypeNamesTYPE("Structural", "S") {

		@Override
		public MType normaliseType(MType type) {
			while (type.namedType() != null) {
				type = type.namedType().getBody();
			}
			return type;
		}
		//@Override
		//public MType nameType(String name, MType type) {
//			return type;
//		}
	};
	
	public static TypeNamesTYPE Nominal = new TypeNamesTYPE("Nominal", "N") {

		@Override
		public MType normaliseType(MType type) {
			return type;
		}
		//@Override
		//public MType nameType(String name, MType type) {
//			return MType.NAMED(name, type);
		//}
	};
}
