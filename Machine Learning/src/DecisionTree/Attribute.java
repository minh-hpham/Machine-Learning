package DecisionTree;

public class Attribute {
	private String name;
	private int value;
	
	public Attribute(String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
