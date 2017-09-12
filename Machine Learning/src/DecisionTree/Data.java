package DecisionTree;

import java.util.ArrayList;

public class Data {
	private ArrayList<HwAttribute> attributes;

	public ArrayList<HwAttribute> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(ArrayList<HwAttribute> data_attributes) {
		this.attributes = data_attributes;
	}
	public HwAttribute getAttribute (String name) {
		for (int i = 0; i < attributes.size(); i++ ) {
			if (attributes.get(i).getName().equals(name)) {
				return attributes.get(i);
			}
		}
		return null;
	}
}
