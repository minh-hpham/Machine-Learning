package homework5;

import static java.util.Comparator.comparingDouble;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class NaiveBayes extends Utility {

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
		double[] lamdas = new double[] { 2, 1.5, 1.0, 0.5 };
		double lamda = find_best_lamda(cross_validation_data, lamdas);
		System.out.println("Best learning rate: " + lamda);
		// cross validation accuracy
		System.out.println("Avg. Accuracy cross-validation " + cross_validation(cross_validation_data, lamda));
		// Train the classifier for 20 epochs
		HashMap<Integer, double[]> weight = naive_bayes(train_data, lamda);
		System.out.println("Accuracy Train " + accuracy(train_data, weight));
		System.out.println("Accuracy Test " + accuracy(test, weight));
	}

	private static HashMap<Integer, double[]> naive_bayes(ArrayList<ArrayList<Integer>> train_data, double lamda) {
		HashMap<Integer, double[]> weight = new HashMap<>();
		weight = train_weight(train_data, weight, lamda);
		return weight;
	}

	private static double cross_validation(ArrayList<ArrayList<ArrayList<Integer>>> cross_data,
			double lamda) {
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
			HashMap<Integer, double[]> weight = naive_bayes(train_data, lamda);
			accs += accuracy(cross_data.get(i), weight);
		}
		return accs / 5;
	}

	private static double find_best_lamda(ArrayList<ArrayList<ArrayList<Integer>>> cross_validation_data,
			double[] lamdas) {
		ArrayList<Double> accs = new ArrayList<Double>();

		ArrayList<ArrayList<Integer>> train_data = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < cross_validation_data.size(); i++) {
			train_data.addAll(cross_validation_data.get(i));
		}

		for (double lamda : lamdas) {
			
			// cross-validation
			
			double accuracy = 0.0;
			ArrayList<Integer> indices = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4));
			for (int i = 0; i < cross_validation_data.size(); i++) {
				// train_data
				indices.remove(i);
				ArrayList<ArrayList<Integer>> dataset = new ArrayList<ArrayList<Integer>>();
				for (int j : indices) {
					dataset.addAll(cross_validation_data.get(j));
				}
				indices.add(i, i);;
				HashMap<Integer, double[]> weight = new HashMap<Integer, double[]>();
				weight = train_weight(dataset, weight, lamda);
				accuracy += accuracy(cross_validation_data.get(i), weight);
			}
			accs.add(accuracy);
		}
		int maxIndex = IntStream.range(0, accs.size()).boxed().max(comparingDouble(accs::get)).get();
		return lamdas[maxIndex];
	}

	private static double accuracy(ArrayList<ArrayList<Integer>> test, HashMap<Integer, double[]> weight) {
		double[] prior = weight.get(-1);
		Set<Integer> keys = weight.keySet();
		keys.remove(-1);
		weight.put(-1, prior);
		double tot = 0.0;
		for (ArrayList<Integer> data : test) {
			tot += weight_predict_y(weight,data,prior,keys);
		}
		return tot/test.size();
	}

	private static int weight_predict_y(HashMap<Integer, double[]> weight, ArrayList<Integer> data, double[] prior,
			Set<Integer> keys) {
		double neg = 0.0;// bias
		double pos = 0.0;
		int y = data.get(0);
		data.remove(0);
		Set<Integer> features = new HashSet<Integer>(data);
		data.add(0, y);
		for (int key : keys) {
			if (features.contains(key)) {
				neg += Math.log(weight.get(key)[0] * prior[0]);
				pos += Math.log(weight.get(key)[1] * prior[1]);
			} else {
				neg += Math.log((1 - weight.get(key)[0]) * prior[1]);
				pos += Math.log((1 - weight.get(key)[1]) * prior[1]);
			}
		}
		int predict = neg > pos ? -1 : 1;

		return predict == y ? 1 : 0;
	}

	private static HashMap<Integer, double[]> train_weight(ArrayList<ArrayList<Integer>> train_data,
			HashMap<Integer, double[]> weight, double lamda) {
		int neg_y = 0;

		for (ArrayList<Integer> data : train_data) {
			int y = data.get(0);
			if (y < 0) {
				neg_y++;
			}
			for (int i = 1; i < data.size(); i++) {
				int key = data.get(i);
				if (!weight.containsKey(key)) {
					weight.put(key, new double[2]);
				}
				double[] val = weight.get(key);
				val[0] = y < 0 ? val[0] + 1 : val[0];
				val[1] = y > 0 ? val[1] + 1 : val[1];
				weight.put(key, val);
			}
		}
		int pos_y = train_data.size() - neg_y;
		for (int key : weight.keySet()) {
			double[] val = weight.get(key);
			val[0] = (double)(val[0] + lamda) / (double)(neg_y + 2 * lamda);
			val[1] = (double)(val[1] + lamda) / (double)(pos_y + 2 * lamda);
		}
		int size = train_data.size();
		double[] prior = new double[2];
		prior[0] = (double) neg_y / size;
		prior[1] = (double) pos_y / size;
		weight.put(-1, prior);
		return weight;
	}

}
