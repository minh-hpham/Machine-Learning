package hw1;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {
	private Node parent;
	public HashMap<Integer,Node> children;
	private double entropy;
	private boolean isUsed;
	private HwAttribute label;
	private int pathFromParent;

	public Node() {
		parent = null;
		children = new HashMap<Integer,Node>();
		entropy = -1.0;
		isUsed = false;
		label = null;
		pathFromParent = -1;
	}
	
	public void setPathFromParent(int k) {
		this.pathFromParent = k;
	}

	public int getPathFromParent() {
		return pathFromParent;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getParent() {
		return parent;
	}
	
	/*public void setData(ArrayList<Record> data) {
		this.data = data;
	}

	public ArrayList<Record> getData() {
		return data;
	}*/

	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	public double getEntropy() {
		return entropy;
	}

	public void setChildren(HashMap<Integer,Node> chidren) {
		this.children = children;
	}
	
	public HashMap<Integer,Node> getChildren() {
		return children;
	}
	public void addChild(int key, Node value) {
		this.children.put(key, value);
	}
	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}

	public boolean isUsed() {
		return isUsed;
	}
	
	public void setAttribute(HwAttribute attribute) {
		this.label = attribute;
	}

	public HwAttribute getAttribute() {
		return label;
	}
	public int maxDepth() {
		if (this.getChildren().isEmpty()) {
			return 1;
		}
		int max = 0;
		for (int i = 0; i < children.size(); i++) {
			int childDepth = children.get(i).maxDepth();
			max = childDepth > max ? childDepth : max ; 
		}
		return 1 + max;
		
	}
}
