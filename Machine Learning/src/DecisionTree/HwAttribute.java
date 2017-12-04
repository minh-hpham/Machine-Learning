package DecisionTree;
// Specifies features for the badges dataset
public class HwAttribute extends Attribute {
	
	public static final int yes = 1;
	public static final int no = 0;
	public static final int positive = 1;
	public static final int negative = 0;
	
	enum outcome {
		positive,negative
	}
	
	public HwAttribute(String name, int value) {
		super(name, value);
	}
	
}
