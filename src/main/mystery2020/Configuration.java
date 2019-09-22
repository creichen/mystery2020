package mystery2020;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Configuration {
	
	// ====================
	// operators
	
	public enum Op {
		ADD,
		AND,
		EQ,
		GT
	}
	
	private Map<Op, OpConfig> op_config = new HashMap<>();
	{
		this.op_config.put(Op.ADD, new OpConfig(3, OpConfig.Associativity.RIGHT));
		this.op_config.put(Op.GT,  new OpConfig(2, OpConfig.Associativity.NONE));
		this.op_config.put(Op.EQ,  new OpConfig(1, OpConfig.Associativity.NONE));
		this.op_config.put(Op.AND, new OpConfig(0, OpConfig.Associativity.RIGHT));
	}
	
	public Map<Op, OpConfig>
	getOpConfig() {
		return this.op_config;
	}
	
	// ====================
	// other configuration
	
	public interface ConfigOption {
		/**
		 * @return Unique key (within this configuration option) for identifying this option 
		 */
		public char getKey();
		
		/**
		 * From the Enum class
		 * 
		 * @return The declaring class of the enum object
		 */
		public Class<? extends Enum<?>>
		getDeclaringClass();
	}

	public enum Scoping implements ConfigOption {
		STATIC('s', "Static Scoping"),
		DYNAMIC('d', "Dynamic Scoping");

		private String name;
		private char key;
		private Scoping(char key, String name) {
			this.key = key;
			this.name = name;
		}
		
		@Override
		public char
		getKey() {
			return this.key;
		}
		
		@Override
		public String
		toString() {
			return this.name;
		}
	}

	private static class Subsystem<E extends ConfigOption> {
		private char key;
		private String name;
		private Class<? extends ConfigOption> option;
		private E deflt;
		private BiConsumer<Configuration, E> setter;

		public Subsystem(char key, String name, Class<E> option, E deflt, BiConsumer<Configuration, E> consumer) {
			this.key = key;
			this.name = name;
			this.option = option;
			this.deflt = deflt;
			this.setter = consumer;
			
			if (deflt.getDeclaringClass() != option) {
				throw new RuntimeException("Wrong default option " + deflt + " for " + option);
			}
		}

		public char
		getKey() {
			return this.key;
		}
		
		public void
		setDefault(Configuration config) {
			this.setter.accept(config, this.deflt);
		}
	}

	private static Subsystem<?>[] subsystems = new Subsystem[] {
			new Subsystem<Scoping>('S', "Scoping", Scoping.class, Scoping.STATIC, (c, e) -> c.setScoping(e))
	};
	
	private static Map<Character, Subsystem<?>> options_by_key = new HashMap<>();
	{
		for (Subsystem<?> sys : Configuration.subsystems) {
			Configuration.options_by_key.put(sys.getKey(), sys);
		}
	}

	// ==========
	
	private Scoping scoping;
	
	public Scoping
	getScoping() {
		return this.scoping;
	}

	public void
	setScoping(Scoping s) {
		this.scoping = s;
	}

	/**
	 * Constructs a default configuration
	 */
	public Configuration() {
		for (Subsystem<?> sys: Configuration.subsystems) {
			sys.setDefault(this);
		}
	}
}
