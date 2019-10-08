package mystery2020;

public abstract class AbstractConfigOption<T> implements ConfigOption<T> {
	private String name;
	private String code;

	public AbstractConfigOption(String name, String code) {
		this.name = name;
		this.code = code;
	}

	@Override
	public String
	getName() {
		return this.name;
	}

	@Override
	public String
	getCode() {
		return this.code;
	}
}
