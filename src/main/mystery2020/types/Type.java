package mystery2020.types;

import mystery2020.Configuration;

public interface Type {
	/**
	 * Check whether the other type can be widened to this type
	 *
	 * @param other
	 * @param config
	 * @return
	 */
	public boolean canBeWidenedFrom(Type other, Configuration config);
}
