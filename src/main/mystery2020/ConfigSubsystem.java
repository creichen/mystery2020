package mystery2020;

import java.util.HashSet;
import java.util.Set;

public class ConfigSubsystem<T extends ConfigOption<T>> {
	private T[] options;
	private String name, code;
	public ConfigSubsystem(String subsystem_name, String code, @SuppressWarnings("unchecked") T ... options) {
		this.name = subsystem_name;
		this.code = code;
		this.options = options;
		Set<String> s = new HashSet<>();
		for (T t : options) {
			if (s.contains(t.getCode())) {
				throw new RuntimeException("Subsystem `" + subsystem_name + "': multiple options of code `" + t.getCode() + "'");
			}
			s.add(t.getCode());
		}
	}

	public T
	getAt(int i) {
		return this.options[i];
	}

	public int
	size() {
		return this.options.length;
	}

	public String
	getName() {
		return this.name;
	}

	public String
	getCode() {
		return this.code;
	}

	public Config
	getDefault() {
		return new Config();
	}

	public class Config {
		T choice = ConfigSubsystem.this.options[0];

		public T
		get() {
			return this.choice;
		}

		public ConfigSubsystem<T>
		getSubsystem() {
			return ConfigSubsystem.this;
		}

		public void
		set(String optcode) {
			for (T opt : ConfigSubsystem.this.options) {
				if (opt.getCode().equals(optcode)) {
					this.choice = opt;
					return;
				}
			}
			throw new IllegalArgumentException("Configuration option not supported: " + ConfigSubsystem.this.name + ":" + optcode);
		}
	}

	public ConfigSubsystem<T>.Config
	getConfig(Configuration configuration) {
		int config_index = Configuration.getConfigSubsystemIndex(this);
		ConfigSubsystem<T>.Config conf = this.getDefault();
		configuration.setSubsystem(config_index, conf);
		return conf;
	}
}
