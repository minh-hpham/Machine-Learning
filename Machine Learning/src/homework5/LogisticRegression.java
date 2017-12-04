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

public class LogisticRegression extends Utility {

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
		double[] learning_rates = new double[] {  1, 0.1, 0.01, 0.001, 0.0001,0.00001 };
		double learning_rate = find_best_learning_rate(cross_validation_data, learning_rates);
		System.out.println("Best learning rate: " + learning_rate);
		double[] tradeoff = new double[] { 0.1, 1, 10, 100, 1000, 10000 };
		double C = find_best_tradeoff(cross_validation_data, tradeoff);
		System.out.println("Best C: " + C);
		// cross validation accuracy
		System.out.println(
				"Avg. Accuracy cross-validation " + cross_validataion(cross_validation_data, learning_rate, C));
		// Train the classifier for 20 epochs
		HashMap<Integer, Double> weight = logistic_regression(train_data, learning_rate, C);
		System.out.println("Accuracy Train " + test(train_data, weight));
		System.out.println("Accuracy Test " + test(test, weight));
	}

	private static HashMap<Integer, Double> logistic_regression(ArrayList<ArrayList<Integer>> train_data,
			double learning_rate, double tradeoff) {
		Random r = new Random();
		int index;
		HashMap<Integer,Double> weight = new HashMap<>();
		weight.put(0, 0.0);
		for (int epoch = 1 ; epoch < 1000; epoch ++) {
			Collections.shuffle(train_data);
			index = r.nextInt(train_data.size());
			ArrayList<Integer> x_i = train_data.get(index);
			weight = train_weight(x_i, weight, learning_rate, tradeoff);
		}
		return weight;
	}

	private static double cross_validataion(ArrayList<ArrayList<ArrayList<Integer>>> cross_data, double learning_rate,
			double tradeoff) {
		ArrayList<ArrayList<Integer>> train_data;
		double accs = 0.0;
		ArrayList<Integer> indices = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4));
		for (int i = 0; i < cross_data.size(); i++) {
			// train_data
			indices.remove(i);
			train_data = new ArrayList<>();
			for (int j : indices) {
				train_data.addAll(cross_data.get(j));
			}
			indices.add(i,i);
			HashMap<Integer, Double> weight = logistic_regression(train_data, learning_rate, tradeoff);
			accs += test(cross_data.get(i), weight);
		}
		return accs / 5;
	}

	private static double find_best_tradeoff(ArrayList<ArrayList<ArrayList<Integer>>> cross_validation_data,
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
			for (int epoch = 0; epoch < 2; epoch++) {
				for (ArrayList<Integer> data : train_data) {
					weight = train_weight(data, weight, learning_rate, C);
				}
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
				for (ArrayList<Integer> data : dataset) {
					weight = train_weight(data, weight, learning_rate, C);
				}
				accuracy += test(cross_validation_data.get(i), weight);
			}
			accs.add(accuracy);
			weight = new HashMap<>();
		}
		int maxIndex = IntStream.range(0, accs.size()).boxed().max(comparingDouble(accs::get)).get();
		return regularizations[maxIndex];
	}

	private static HashMap<Integer, Double> train_weight(ArrayList<Integer> x_i, HashMap<Integer, Double> weight,
			double learning_rate, double tradeoff) {
		Set<Integer> keys = weight.keySet();
		int y = x_i.get(0);
		HashMap<Integer, Double> gradient = new HashMap<>();
		for (int key : weight.keySet()) {
			gradient.put(key, 0.0);
		}
		for (int i = 1; i < x_i.size(); i++) {
			int key = x_i.get(i);
			if (!keys.contains(key)) {
				weight.put(key, 0.0);
			}
			double val = weight.get(key);
			double exp = Math.exp(-y * val);
			gradient.put(key, -(y * exp) / (1 + exp) + (2 / tradeoff) * val);
		}

		for (int key : weight.keySet()) {
			double val = weight.get(key) - learning_rate * gradient.get(key);
			weight.put(key, val);
		}
		return weight;
	}

	private static double find_best_learning_rate(ArrayList<ArrayList<ArrayList<Integer>>> cross_validation_data,
			double[] learning_rates) {
		double tradeoff = 0.1;
		ArrayList<Double> accs = new ArrayList<Double>();
		HashMap<Integer, Double> weight = new HashMap<>();

		ArrayList<ArrayList<Integer>> train_data = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < cross_validation_data.size(); i++) {
			train_data.addAll(cross_validation_data.get(i));
		}
		for (double learning_rate : learning_rates) {
			weight.put(0, 0.0);
			for (int epoch = 0; epoch < 5; epoch++) {
				for (ArrayList<Integer> data : train_data) {
					weight = train_weight(data, weight, learning_rate, tradeoff);
				}
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
				for (ArrayList<Integer> data : dataset) {
					weight = train_weight(data, weight, learning_rate, tradeoff);
				}
				accuracy += test(cross_validation_data.get(i), weight);
			}

			accs.add(accuracy);
			weight = new HashMap<>();
		}
		int maxIndex = IntStream.range(0, accs.size()).boxed().max(comparingDouble(accs::get)).get();
		return learning_rates[maxIndex];
	}
}
