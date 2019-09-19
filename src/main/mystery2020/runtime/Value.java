package mystery2020.runtime;

import mystery2020.Configuration;
import mystery2020.MType;

public class Value {
	private MType type;
	private Object value;
	
	public Value(MType type, Object obj) {
		this.type = type;
		this.value = obj;
		if (this.value == null) {
			throw new RuntimeException("Must not be null");
		}
	}
	
	public MType
	getType() {
		return this.type;
	}
	
	public Object
	getValue() {
		return this.value;
	}
	
	public boolean
	equalTo(Object other, Configuration config) {
		if (other instanceof Value) {
			Value v = (Value) other;
			return this.getType().equalTo(v.getType(), config) && this.getType().valueEquals(this.getValue(), v.getValue(), config);
		}
		return false;
	}
}
