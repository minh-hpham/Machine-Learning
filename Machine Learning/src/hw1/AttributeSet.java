package hw1;

import java.util.ArrayList;

public class AttributeSet {
	private String name;
	private ArrayList<HwAttribute> attributes;
	private double entropy;
	private boolean isUsed;
	
	public AttributeSet(String name) {
		this.name = name;
		attributes = new ArrayList<HwAttribute>();
		entropy = -1;
		isUsed = false;
	}
	
	public void setAttributes(ArrayList<HwAttribute> attributes) {
		this.attributes = attributes;
	}
	public void addAttribute(HwAttribute h) {
		this.attributes.add(h);
	}
	public ArrayList<HwAttribute> getAttributes() {
		return attributes;
	}

	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	public double getEntropy() {
		return entropy;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
