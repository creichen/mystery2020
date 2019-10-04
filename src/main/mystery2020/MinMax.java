package mystery2020;

public class MinMax {
	private int min, max;

	public MinMax(int line_nr, int column_nr, int min, int max) {
		this.min = min;
		this.max = max;
		if (min > max) {
			throw new InvalidSubrangeException(line_nr, "Subrange " + this.toString() + " is not valid"); 
		}
	}

	public int getMin() {
		return this.min;
	}
	
	public int getMax() {
		return this.max;
	}
	
	@Override
	public String
	toString() {
		return "[" + this.min +  " TO " + this.max + "]";
	}
}
