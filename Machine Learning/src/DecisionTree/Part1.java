package DecisionTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Part1 {

	public static void main(String[] args) throws FileNotFoundException {
		
		HashMap<Integer, AttributeSet> attributeSet = getAllFeatures();
		InputStream in = new FileInputStream(new File(args[0]));
		ArrayList<Data> dataset = Dataset.buildDatasetWithAttributes(in);
		InputStream in1 = new FileInputStream(new File(args[1]));
		ArrayList<Data> testset = Dataset.buildDatasetWithAttributes(in1);
		Node root = new Node();
		root.setAttribute(new HwAttribute("outcome", -1));
		int commonLabel = Tree.majorityLabel(dataset, HwAttribute.outcome.values().length);
		Tree t = new Tree();
		root = t.buildTree(dataset, attributeSet, commonLabel, root,15);
		System.out.println("Max Depth = " + root.maxDepth());
		double trainError = error(dataset, root);
		System.out.println("Error for training set: "+ trainError);
		double testError = error(testset, root);
		System.out.println("Error for test set: "+ testError);
	}

	public static double error(ArrayList<Data> dataset, Node root) {
		int trainError = 0;
		for (Data data : dataset) {
			if(data.getAttributes().get(0).getValue() != traverseTree(data, root)) {
				trainError++;
			}
		}
		return 100 * trainError/dataset.size();
	}

	

	private static void printTree(Node root) {
		if(root.getEntropy() < 0) {
			System.out.println("Negative Entropy");
		}
		if (!root.children.isEmpty()) {
			if (root.getParent() == null) {
				System.out.println(String.format("Parent: null, Name: %s Path %d", root.getAttribute().getName(),
						root.getPathFromParent()));
			} else {
				System.out.println(
						String.format("Parent: %s, Name: %s Path %d", root.getParent().getAttribute().getName(),
								root.getAttribute().getName(), root.getPathFromParent()));
			}
			for (int key : root.children.keySet()) {
				printTree(root.children.get(key));
			}
		}
		if (root.children.isEmpty()) {
			System.out.println(String.format("Parent: %s, Name: %s VALUE %d", root.getParent().getAttribute().getName(),
					root.getAttribute().getName(), root.getAttribute().getValue()));
		}
	}

	public static HashMap<Integer, AttributeSet> getAllFeatures() {
		HashMap<Integer, AttributeSet> attributeSet = new HashMap<Integer, AttributeSet>();

		AttributeSet firstname_longer_than_lastname = new AttributeSet("firstname_longer_than_lastname");
		firstname_longer_than_lastname.addAttribute(new HwAttribute("firstname_longer_than_lastname", HwAttribute.yes));
		firstname_longer_than_lastname.addAttribute(new HwAttribute("firstname_longer_than_lastname", HwAttribute.no));

		AttributeSet have_middlename = new AttributeSet("have_middlename");
		have_middlename.addAttribute(new HwAttribute("have_middlename", HwAttribute.yes));
		have_middlename.addAttribute(new HwAttribute("have_middlename", HwAttribute.no));

		AttributeSet firstname_startend_sameletter = new AttributeSet("firstname_startend_sameletter");
		firstname_startend_sameletter.addAttribute(new HwAttribute("firstname_startend_sameletter", HwAttribute.yes));
		firstname_startend_sameletter.addAttribute(new HwAttribute("firstname_startend_sameletter", HwAttribute.no));

		AttributeSet firstname_alphabetical_before_lastname = new AttributeSet(
				"firstname_alphabetical_before_lastname");
		firstname_alphabetical_before_lastname
				.addAttribute(new HwAttribute("firstname_alphabetical_before_lastname", HwAttribute.yes));
		firstname_alphabetical_before_lastname
				.addAttribute(new HwAttribute("firstname_alphabetical_before_lastname", HwAttribute.no));

		AttributeSet firstname_2ndletter_vowel = new AttributeSet("firstname_2ndletter_vowel");
		firstname_2ndletter_vowel.addAttribute(new HwAttribute("firstname_2ndletter_vowel", HwAttribute.yes));
		firstname_2ndletter_vowel.addAttribute(new HwAttribute("firstname_2ndletter_vowel", HwAttribute.no));

		AttributeSet lastname_numLetters_even = new AttributeSet("lastname_numLetters_even");
		lastname_numLetters_even.addAttribute(new HwAttribute("lastname_numLetters_even", HwAttribute.yes));
		lastname_numLetters_even.addAttribute(new HwAttribute("lastname_numLetters_even", HwAttribute.no));
		
		AttributeSet totalVowels_lessequal_5 = new AttributeSet("totalVowels_lessequal_5");
		totalVowels_lessequal_5.addAttribute(new HwAttribute("totalVowels_lessequal_5", HwAttribute.yes));
		totalVowels_lessequal_5.addAttribute(new HwAttribute("totalVowels_lessequal_5", HwAttribute.no));
		
		AttributeSet firstLetterBTLKNWG = new AttributeSet("firstLetterBTLKNWG");
		firstLetterBTLKNWG.addAttribute(new HwAttribute("firstLetterBTLKNWG", HwAttribute.yes));
		firstLetterBTLKNWG.addAttribute(new HwAttribute("firstLetterBTLKNWG", HwAttribute.no));
		
		AttributeSet firstLetterARFH = new AttributeSet("firstLetterARFH");
		firstLetterARFH.addAttribute(new HwAttribute("firstLetterARFH", HwAttribute.yes));
		firstLetterARFH.addAttribute(new HwAttribute("firstLetterARFH", HwAttribute.no));
		
		AttributeSet ratio_less_1 = new AttributeSet("ratio_less_1");
		ratio_less_1.addAttribute(new HwAttribute("ratio_less_1", HwAttribute.yes));
		ratio_less_1.addAttribute(new HwAttribute("ratio_less_1", HwAttribute.no));
		
		
		// add those to attributeSet
		attributeSet.put(1, firstname_longer_than_lastname);
		attributeSet.put(2, have_middlename);
		attributeSet.put(3, firstname_startend_sameletter);
		attributeSet.put(4, firstname_alphabetical_before_lastname);
		attributeSet.put(5, firstname_2ndletter_vowel);
		attributeSet.put(6, lastname_numLetters_even);
		attributeSet.put(7, totalVowels_lessequal_5);
		attributeSet.put(8, firstLetterBTLKNWG);
		attributeSet.put(9, firstLetterARFH);
		attributeSet.put(10, ratio_less_1);
		
		return attributeSet;
	}
	
	public static int traverseTree(Data data, Node root) {
		int value = 0;
		if(!root.getChildren().isEmpty()) {
			
			String attribute = root.getAttribute().getName();
			int path = data.getAttribute(attribute).getValue();
			Node child = root.getChildren().get(path);
			return traverseTree(data, child);
			/*for (int i = 0; i < root.getChildren().size(); i++) {
				if (root.getChildren().get(i).getPathFromParent() == path) {
					value = traverseTree(data,root.getChildren().get(i));
					break;
				}
			}*/
		}
		else {
			return root.getAttribute().getValue();
		}
		
	}
	public static double sd(double[] errors) {
		double total = 0;
		for (int i = 0; i < errors.length; i++) {
			total += errors[i]; // this is the calculation for summing up all
								// the values
		}
		double mean = total / errors.length;

		for (int i = 0; i < errors.length; i++) {
			errors[i] = Math.pow((errors[i] - mean), 2);
		}
		total = 0;
		for (int i = 0; i < errors.length; i++) {
			total += errors[i]; 
		}
		double meanSquare = total / errors.length;
		return Math.sqrt(meanSquare);
	}

}

