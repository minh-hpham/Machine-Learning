package hw1;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.html.HTML.Attribute;

public class Tree {
	private final int numb_label = HwAttribute.outcome.values().length;

	public Node buildTree(ArrayList<Data> dataset, HashMap<Integer, AttributeSet> attributeSet, int commonLabel,
			Node root) {
		double maxGain = 0.0;
		double gain = 0.0;
		int bestAttribute = 0;
		// same label for all data
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
			//System.out.println(String.format("%s %d", root.getAttribute().getName(), root.getPathFromParent()));
			return root;
		}
		// leaf node
		else if (attributeSet.size() == 0) {
			/*int positive = 0;
			int all = 0;
			for (Data d : dataset) {
				all++;
				if (d.getAttributes().get(0).getValue() == 1) {
					positive++;
				}
			}
			if ((positive / all) > 0.5) {
				root.setAttribute(new HwAttribute("outcome", 1));
			} else {
				root.setAttribute(new HwAttribute("outcome", 0));
			}*/
			//System.out.println(String.format("%s %d", root.getAttribute().getName(), root.getPathFromParent()));
			root.setAttribute(new HwAttribute("outcome", majorityLabel(dataset, numb_label)));
			return root;
		} else {
			// remained feature
			root.setEntropy(Entropy.calculateEntropy(dataset, numb_label));

			if (attributeSet.size() == 1) {
				for (int key : attributeSet.keySet()) {
					bestAttribute = key;
					break;
				}
			}
			// Otherwise find the best attribute for this node
			else {

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
				if (!(maxGain > 0)) {
					int majorityLabel = majorityLabel(dataset, numb_label);
					root.setAttribute(new HwAttribute("outcome", majorityLabel));
					return root;
				}
			}
			// Modify root as best attribute and Creates branch for each
			// possible
			// value of root
			AttributeSet set = attributeSet.get(bestAttribute);
			// int rootKey = root.getAttribute().getValue();
			root.setAttribute(new HwAttribute(set.getName(), -1));
			// System.out.println(String.format("%s, %d",
			// root.getAttribute().getName(), root.getPathFromParent()));
			int types = set.getAttributes().size();
			HashMap<Integer, AttributeSet> replaceSet = new HashMap<Integer, AttributeSet>();
			for (int key : attributeSet.keySet()) {
				if (key != bestAttribute) {
					replaceSet.put(key, attributeSet.get(key));
				}
			}
			// recurse to children node
			for (int k = 0; k < types; k++) {
				Node node = new Node();
				node.setParent(root);
				node.setUsed(true);
				node.setPathFromParent(k);
				ArrayList<Data> nodeset = subset(dataset, bestAttribute, k);
				if (nodeset.size() == 0) {
					node.setAttribute(new HwAttribute("outcome", commonLabel));
				} else {
					node.setAttribute(new HwAttribute(set.getName(), k));
					//System.out.println(String.format("replace set size: %d", replaceSet.size()));
					node = buildTree(nodeset, replaceSet, commonLabel, node);
				}

				root.addChild(k, node);
			}
			//System.out.println(String.format("%s %d", root.getAttribute().getName(), root.getPathFromParent()));
			return root;
		}
	}

	private int majorityLabel(ArrayList<Data> dataset, int numb_label2) {
		int countMax = 0;
		int m = 0;
		for (int i = 0; i < numb_label2; i++) {
			int count = 0;
			for (Data d : dataset) {
				if(d.getAttributes().get(0).getValue() == i) {
					count++;
				}
			}
			if(countMax < count) {
				countMax = count;
				m = i;
			}
		}
		return m;
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
