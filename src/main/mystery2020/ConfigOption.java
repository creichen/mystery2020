package mystery2020;

public interface ConfigOption<T> {
	public String
	getName(); // human-readable string description
	
	public String
	getCode(); // short code for the option, unique within subsystem
}
