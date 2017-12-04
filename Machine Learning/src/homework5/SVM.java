package homework5;

import static java.util.Comparator.comparingDouble;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

public class SVM extends Utility {

	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<ArrayList<Integer>> training00 = featurize(new FileInputStream(new File(args[0])));
		ArrayList<ArrayList<Integer>> training01 = featurize(new FileInputStream(new File(args[1])));
		ArrayList<ArrayList<Integer>> training02 = featurize(new FileInputStream(new File(args[2])));
		ArrayList<ArrayList<Integer>> training03 = featurize(new FileInputStream(new File(args[3])));
		ArrayList<ArrayList<Integer>> training04 = featurize(new FileInputStream(new File(args[4])));
		ArrayList<ArrayList<Integer>> train_data = featurize(new FileInputStream(new File(args[5])));
		ArrayList<ArrayList<Integer>> test = featurize(new FileInputStream(new File(args[6])));

		ArrayList<ArrayList<ArrayList<Integer>>> cross_validation_data = new ArrayList<>();
		cross_validation_data.add(training00);
		cross_validation_data.add(training01);
		cross_validation_data.add(training02);
		cross_validation_data.add(training03);
		cross_validation_data.add(training04);
		// include bias
		double[] learning_rates = new double[] { 10, 1, 0.1, 0.01, 0.001, 0.0001 };
		double learning_rate = find_best_learning_rate(cross_validation_data, learning_rates);
		System.out.println("Best learning rate: " + learning_rate);
		double[] regularizations = new double[] { 10, 1, 0.1, 0.01, 0.001, 0.0001 };
		double C = find_best_regularization(cross_validation_data, regularizations);
		System.out.println("Best C: " + C);
		// cross validation accuracy
		System.out.println("Avg. Accuracy cross-validation " + cross_validataion(cross_validation_data, learning_rate, C));
		// Train the classifier for 20 epochs
		HashMap<Integer, Double> weight = SupportVectorMachine(train_data, learning_rate, C);
		System.out.println("Accuracy Train " + test(train_data, weight));
		System.out.println("Accuracy Test "+ test(test,weight));
	}
	private static HashMap<Integer, Double> train_weight(ArrayList<ArrayList<Integer>> train_data,
			HashMap<Integer, Double> weight, double learning_rate, double regularization) {
		Set<Integer> keys = weight.keySet();
		int count = 0;
		for (ArrayList<Integer> x_i : train_data) {
			double sum = 0.0;
			for (int i = 1; i < x_i.size(); i++) {
				int key = x_i.get(i);
				if (keys.contains(key)) {
					sum += weight.get(key);
				} else {
					weight.put(key, 0.0);
				}
			}
			// update weight*(1-gamma)
			for (Integer k : weight.keySet()) {
				weight.put(k, weight.get(k) * (1 - learning_rate));
			}
			int y = x_i.get(0);
			if (y * sum <= 1) {
				for (int i = 1; i < x_i.size(); i++) {
					int k = x_i.get(i);
					double val = weight.get(k) + learning_rate * regularization * y * 1;
					weight.put(k, val);
				}
			}
		}
		return weight;
	}

	private static double cross_validataion(
			ArrayList<ArrayList<ArrayList<Integer>>> cross_data,
			double learning_rate, double regularization) {
		ArrayList<ArrayList<Integer>> train_data;
		double accs = 0.0;
		ArrayList<Integer> indices = new ArrayList<Integer>(
				Arrays.asList(0,1,2,3,4));
		for (int i = 0; i < cross_data.size(); i++) {
			// train_data
			indices.remove(i);
			train_data = new ArrayList<>();
			for (int j : indices) {
				train_data.addAll(cross_data.get(j));
			}
			indices.add(i,i);
			HashMap<Integer,Double>weight = SupportVectorMachine(train_data, 
					learning_rate, regularization);
			accs += test(cross_data.get(i),weight);
		}

		return accs/5;
	}
	private static HashMap<Integer, Double> SupportVectorMachine(ArrayList<ArrayList<Integer>> train_data,
			double learning_rate, double c) {
		HashMap<Integer, Double> weight = new HashMap<>();
		weight.put(0, 0.0);
		for (int epoch = 0; epoch < 20; epoch++) {
			Collections.shuffle(train_data);
			weight = train_weight(train_data, weight, learning_rate, c);
		}
		return weight;
	}

	private static double find_best_regularization(ArrayList<ArrayList<ArrayList<Integer>>> cross_validation_data,
			double[] regularizations) {
		double learning_rate = 0.0001;
		ArrayList<Double> accs = new ArrayList<Double>();
		HashMap<Integer, Double> weight = new HashMap<>();

		ArrayList<ArrayList<Integer>> train_data = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < cross_validation_data.size(); i++) {
			train_data.addAll(cross_validation_data.get(i));
		}
		for (double C : regularizations) {
			// bias
			weight.put(0, 0.0);
			for (int epoch = 0; epoch < 5; epoch++) {
				weight = train_weight(train_data, weight, learning_rate, C);
			}
			// cross-validation
			ArrayList<ArrayList<Integer>> dataset;
			double accuracy = 0.0;
			ArrayList<Integer> indices = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4));
			for (int i = 0; i < cross_validation_data.size(); i++) {
				// train_data
				indices.remove(i);
				dataset = new ArrayList<>();
				for (int j : indices) {
					dataset.addAll(cross_validation_data.get(j));
				}
				indices.add(i,i);
				weight = train_weight(dataset, weight, learning_rate, C);
				accuracy += test(cross_validation_data.get(i), weight);
			}
			accs.add(accuracy);
			weight = new HashMap<>();
		}
		int maxIndex = IntStream.range(0, accs.size()).boxed().max(comparingDouble(accs::get)).get();
		return regularizations[maxIndex];
	}

	private static double find_best_learning_rate(
			ArrayList<ArrayList<ArrayList<Integer>>> cross_validation_data,
			double[] learning_rates) {
		double regularization = 0.1;
		ArrayList<Double> accs = new ArrayList<Double>();
		HashMap<Integer, Double> weight = new HashMap<>();

		ArrayList<ArrayList<Integer>> train_data = 
				new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < cross_validation_data.size(); i++) {
			train_data.addAll(cross_validation_data.get(i));
		}
		for (double learning_rate : learning_rates) {
			weight.put(0, 0.0);
			for (int epoch = 0; epoch < 5; epoch++) {
				weight = train_weight(train_data, weight, 
						learning_rate, regularization);
			}
			// cross-validation
			ArrayList<ArrayList<Integer>> dataset;
			double accuracy = 0.0;
			ArrayList<Integer> indices = new ArrayList<Integer>(
					Arrays.asList(0, 1, 2, 3, 4));
			for (int i = 0; i < cross_validation_data.size(); i++) {
				// train_data
				indices.remove(i);
				dataset = new ArrayList<>();
				for (int j : indices) {
					dataset.addAll(cross_validation_data.get(j));
				}
				indices.add(i,i);
				weight = train_weight(dataset, weight, 
						learning_rate, regularization);
				accuracy += test(cross_validation_data.get(i), weight);
			}
			accs.add(accuracy);
			weight = new HashMap<>();
		}
		int maxIndex = IntStream.range(0, accs.size()).boxed().max(comparingDouble(accs::get)).get();
		return learning_rates[maxIndex];
	}
}
