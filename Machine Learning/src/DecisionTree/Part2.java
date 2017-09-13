package DecisionTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Part2 {

	public static void main(String[] args) throws FileNotFoundException {
		HashMap<Integer, AttributeSet> attributeSet = Part1.getAllFeatures();
		// set up data
		ArrayList<Data> train = Dataset.buildDatasetWithAttributes(new FileInputStream(new File(args[0])));
		InputStream in0 = new FileInputStream(new File(args[2]));
		InputStream in1 = new FileInputStream(new File(args[3]));
		InputStream in2 = new FileInputStream(new File(args[4]));
		InputStream in3 = new FileInputStream(new File(args[5]));
		ArrayList<Data> test = Dataset.buildDatasetWithAttributes(new FileInputStream(new File(args[1])));
		ArrayList<Data> dataset0 = Dataset.buildDatasetWithAttributes(in0);
		ArrayList<Data> dataset1 = Dataset.buildDatasetWithAttributes(in1);
		ArrayList<Data> dataset2 = Dataset.buildDatasetWithAttributes(in2);
		ArrayList<Data> dataset3 = Dataset.buildDatasetWithAttributes(in3);
		ArrayList<Data> combine = new ArrayList<>();

		Node root = null;
		Tree t = new Tree();
		root = new Node();
		root.setAttribute(new HwAttribute("outcome", -1));
		int[] depth = new int[args.length - 6];
		for (int i = 6; i < args.length; i++) {
			depth[i-6] = Integer.parseInt(args[i]);
		}
		
		double maxAccuracy = 0;
		int maxDepth = 0;
		for (int i = 0; i < depth.length; i++) {
			double[] accuracies = new double[4];
			// test is dataset0
			System.out.println("At depth :" + depth[i]);
			combine.clear();
			//System.out.println("All training data");
			//combine.addAll(dataset0);
			combine.addAll(dataset1);
			combine.addAll(dataset2);
			combine.addAll(dataset3);
			int commonLabel = Tree.majorityLabel(combine, HwAttribute.outcome.values().length);
			root = t.buildTree(combine, attributeSet, commonLabel, root, depth[i]);
			accuracies[0] = 100-Part1.error(dataset0, root);

			// test is dataset1
			combine.clear();
			combine.addAll(dataset0);
			combine.addAll(dataset2);
			combine.addAll(dataset3);

			commonLabel = Tree.majorityLabel(combine, HwAttribute.outcome.values().length);
			root = t.buildTree(combine, attributeSet, commonLabel, root, depth[i]);
			accuracies[1] = 100-Part1.error(dataset1, root);

			// test is dataset2
			combine.clear();
			combine.addAll(dataset0);
			combine.addAll(dataset1);
			combine.addAll(dataset3);

			commonLabel = Tree.majorityLabel(combine, HwAttribute.outcome.values().length);
			root = t.buildTree(combine, attributeSet, commonLabel, root, depth[i]);
			accuracies[2] = 100-Part1.error(dataset2, root);

			// test is dataset3
			combine.clear();
			combine.addAll(dataset0);
			combine.addAll(dataset1);
			combine.addAll(dataset2);

			commonLabel = Tree.majorityLabel(combine, HwAttribute.outcome.values().length);
			root = t.buildTree(combine, attributeSet, commonLabel, root, depth[i]);
			accuracies[3] = 100-Part1.error(dataset3, root);
			
			double avg = 0;
			for (int j = 0; j < accuracies.length; j++) {
				avg += accuracies[j];
			}
			avg = avg/accuracies.length;
			System.out.println("Average Accuracy: " + avg);
			double sd = Part1.sd(accuracies);

			System.out.println("Standard deviation: " + sd);
			if (avg > maxAccuracy) {
				maxAccuracy = avg;
				maxDepth = depth[i];
			}
		}
		System.out.println("Best Average Accuracy: " + maxAccuracy);
		System.out.println("Optimum depth: " + maxDepth);
		
		root.setAttribute(new HwAttribute("outcome", -1));
		int commonLabel = Tree.majorityLabel(train, HwAttribute.outcome.values().length);
		root = t.buildTree(train, attributeSet, commonLabel, root,maxDepth);
		double testError = Part1.error(test, root);
		System.out.println("Accuracy for test set: "+ (100 - testError));
		System.out.println("Error for test set: "+ testError);
		System.out.println("Depth = " + root.maxDepth());
		

	}

}
