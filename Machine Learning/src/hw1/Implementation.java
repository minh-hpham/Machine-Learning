package hw1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Implementation {

	public static void main(String[] args) throws FileNotFoundException {
		HashMap<Integer,AttributeSet> attributeSet = getAllFeatures();
		InputStream in = new FileInputStream(new File(args[0]));
		ArrayList<Data> dataset = Dataset.buildDatasetWithAttributes(in);
		
		Node root = new Node();
		root.setAttribute(new HwAttribute("outcome", -1));
		Tree t = new Tree();
		root = t.buildTree(dataset, attributeSet, Dataset.commonLabel(), root);
		
		printTree(root);
	}
	private static void printTree(Node root) {
		if (root.children.isEmpty()) {
			Node parent = root.getParent();
			String p = parent == null ? "None":root.getParent().getAttribute().getName();
			System.out.println(String.format("Parent: %s, Attribute %s, Value %s",p , root.getAttribute().getName(), root.getAttribute().getValue()));
		}
		for (int key : root.children.keySet()) {
			printTree(root.children.get(key));
		}		
	}
	public static HashMap<Integer,AttributeSet> getAllFeatures() {
		HashMap<Integer,AttributeSet> attributeSet = new HashMap<Integer, AttributeSet>();
		
		
		AttributeSet firstname_longer_than_lastname = new AttributeSet("firstname_longer_than_lastname");
		firstname_longer_than_lastname.addAttribute(new HwAttribute("firstname_longer_than_lastname", HwAttribute.yes));
		firstname_longer_than_lastname.addAttribute(new HwAttribute("firstname_longer_than_lastname", HwAttribute.no));
		
		AttributeSet have_middlename = new AttributeSet("have_middlename");
		have_middlename.addAttribute(new HwAttribute("have_middlename", HwAttribute.yes));
		have_middlename.addAttribute(new HwAttribute("have_middlename", HwAttribute.no));
		
		AttributeSet firstname_startend_sameletter = new AttributeSet("firstname_startend_sameletter");
		firstname_startend_sameletter.addAttribute(new HwAttribute("firstname_startend_sameletter", HwAttribute.yes));
		firstname_startend_sameletter.addAttribute(new HwAttribute("firstname_startend_sameletter", HwAttribute.no));
		
		AttributeSet firstname_alphabetical_before_lastname = new AttributeSet("firstname_alphabetical_before_lastname");
		firstname_alphabetical_before_lastname.addAttribute(new HwAttribute("firstname_alphabetical_before_lastname", HwAttribute.yes));
		firstname_alphabetical_before_lastname.addAttribute(new HwAttribute("firstname_alphabetical_before_lastname", HwAttribute.no));
		
		AttributeSet firstname_2ndletter_vowel = new AttributeSet("firstname_2ndletter_vowel");
		firstname_2ndletter_vowel.addAttribute(new HwAttribute("firstname_2ndletter_vowel", HwAttribute.yes));
		firstname_2ndletter_vowel.addAttribute(new HwAttribute("firstname_2ndletter_vowel", HwAttribute.no));
		
		AttributeSet lastname_numLetters_even = new AttributeSet("lastname_numLetters_even");
		lastname_numLetters_even.addAttribute(new HwAttribute("lastname_numLetters_even", HwAttribute.yes));
		lastname_numLetters_even.addAttribute(new HwAttribute("lastname_numLetters_even", HwAttribute.no));
		
		// add those to attributeSet
		attributeSet.put(1, firstname_longer_than_lastname);
		attributeSet.put(2, have_middlename);
		attributeSet.put(3, firstname_startend_sameletter);
		attributeSet.put(4, firstname_alphabetical_before_lastname);
		attributeSet.put(5, firstname_2ndletter_vowel);
		attributeSet.put(6, lastname_numLetters_even);
		return attributeSet;
	}
}
