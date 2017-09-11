package hw1;

import java.util.ArrayList;
import java.util.HashMap;

public class Tree {
	private final int numb_label = HwAttribute.outcome.values().length;

	public Node buildTree(ArrayList<Data> dataset, HashMap<Integer, AttributeSet> attributeSet, int commonLabel,
			Node root) {
		double maxGain = 0.0;
		double gain = 0.0;
		int bestAttribute = 0;

		boolean sameLabelAllData = true;
		int firstLabel = (int) dataset.get(0).getAttributes().get(0).getValue();
		for (Data d : dataset) {
			if (d.getAttributes().get(0).getValue() != firstLabel) {
				sameLabelAllData = false;
				break;
			}
		}
		if (sameLabelAllData) {
			root.setAttribute(dataset.get(0).getAttributes().get(0));
			return root;
		}
		if (attributeSet.size() == 1) {
			root = MajorityLeaf(dataset, attributeSet, commonLabel, root, bestAttribute);
			return root;
		}

		// find the best attribute for this node
		root.setEntropy(Entropy.calculateEntropy(dataset, numb_label));
		ArrayList<Double> subEntropies;

		for (int i : attributeSet.keySet()) {

			subEntropies = new ArrayList<Double>();
			int numbOftypes = attributeSet.get(i).getAttributes().size();

			ArrayList<Data> subData;
			ArrayList<Integer> subDataSize = new ArrayList<Integer>();

			for (int j = 0; j < numbOftypes; j++) {
				subData = subset(dataset, i, j);
				subDataSize.add(subData.size());
				subEntropies.add(Entropy.calculateEntropy(subData, numb_label));
			}
			gain = Entropy.calculateGain(root.getEntropy(), subEntropies, subDataSize, dataset.size());
			if (gain > maxGain) {
				bestAttribute = i;
				maxGain = gain;
			}
		}
		// Modify root as best attribute and Creates branch for each possible
		// value of root
		AttributeSet set = attributeSet.get(bestAttribute);
		int rootKey = root.getAttribute().getValue();
		root.setAttribute(new HwAttribute(set.getName(), rootKey));
		
		int types = set.getAttributes().size();
		HashMap<Integer, AttributeSet> replaceSet = new HashMap<Integer, AttributeSet>();
		for (int key : attributeSet.keySet()) {
			if (key != bestAttribute) {
				replaceSet.put(key, attributeSet.get(key));
			}
		}

		for (int k = 0; k < types; k++) {
			Node node = new Node();
			node.setParent(root);
			node.setUsed(true);

			ArrayList<Data> nodeset = subset(dataset, bestAttribute, k);
			if (nodeset.size() == 0) {
				node.setAttribute(new HwAttribute("outcome", commonLabel));
			} else {
				node.setAttribute(new HwAttribute(set.getName(), k));
				node = buildTree(nodeset, replaceSet, commonLabel, node);
			}
			System.out.println(String.format("Attribute %s, Value %s", node.getAttribute().getName(),
					node.getAttribute().getValue()));
			root.addChild(k, node);
		}

		return root;
	}

	private Node MajorityLeaf(ArrayList<Data> dataset, HashMap<Integer, AttributeSet> attributeSet, int commonLabel,
			Node root, int bestAttribute) {
		for (int i : attributeSet.keySet()) {
			bestAttribute = i;
		}
		AttributeSet set = attributeSet.get(bestAttribute);
		int rootKey = root.getAttribute().getValue();
		root.setAttribute(new HwAttribute(set.getName(), rootKey));
		int types = set.getAttributes().size();
		for (int k = 0; k < types; k++) {
			Node node = new Node();
			node.setParent(root);
			node.setUsed(true);

			ArrayList<Data> nodeset = subset(dataset, bestAttribute, k);
			int positive = 0;
			int all = 0;
			if (nodeset.size() == 0) {
				node.setAttribute(new HwAttribute("outcome", commonLabel));
				continue;
			}
			for (Data d : nodeset) {
				all++;
				if (d.getAttributes().get(0).getValue() == 1) {
					positive++;
				}
			}
			if ((positive / all) > 0.5) {
				node.setAttribute(new HwAttribute("outcome", 1));
			} else {
				node.setAttribute(new HwAttribute("outcome", 0));
			}
			System.out.println(String.format("Attribute %s, Value %s", node.getAttribute().getName(),
					node.getAttribute().getValue()));
			root.addChild(k, node);
		}
		return root;
	}

	private ArrayList<Data> subset(ArrayList<Data> dataset, int attribute, int subattribute) {
		ArrayList<Data> subset = new ArrayList<>();
		for (int i = 0; i < dataset.size(); i++) {
			Data d = dataset.get(i);
			if (d.getAttributes().get(attribute).getValue() == subattribute) {
				subset.add(d);
			}
		}
		return subset;
	}

}
