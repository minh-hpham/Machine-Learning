package perceptron;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class SimplePerceptron {
	private final static int numberOfFeatures = 69;
	private final static double[] learning_rate = { 1, 0.1, 0.01 };

	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<double[]> training00 = parseString(new FileInputStream(new File(args[0])));
		ArrayList<double[]> training01 = parseString(new FileInputStream(new File(args[1])));
		ArrayList<double[]> training02 = parseString(new FileInputStream(new File(args[2])));
		ArrayList<double[]> training03 = parseString(new FileInputStream(new File(args[3])));
		ArrayList<double[]> training04 = parseString(new FileInputStream(new File(args[4])));
		ArrayList<double[]> dataset = parseString(new FileInputStream(new File(args[5])));
		ArrayList<double[]> developmentSet = parseString(new FileInputStream(new File(args[6])));
		ArrayList<double[]> test = parseString(new FileInputStream(new File(args[7])));
		
		ArrayList<ArrayList<double[]>> trainings = new ArrayList<>();
		trainings.add(training00);
		trainings.add(training01);
		trainings.add(training02);
		trainings.add(training03);
		trainings.add(training04);
		
		double[] initial_weights = initialWeight(numberOfFeatures + 1); // include bias
		// Run cross validation for ten epochs for each hyper-parameter combination to get the best hyper-parameter setting		
		int[] L_rate = findBestLearningRate(trainings, initial_weights, learning_rate, 10);
		int  bestL_rate = L_rate[0];
		double crossValidation_accuracy = 100* (double)(L_rate[1]/dataset.size());
		System.out.println(String.format("The best hyper-parameters: %f", learning_rate[bestL_rate]));
		System.out.println(String.format("The cross-validation accuracy for the best hyperparameter: %f", crossValidation_accuracy));
		
		// Train the classifier for 20 epochs
		
		double[] accuracy = new double[20];
		int minDevError = Integer.MAX_VALUE;
		double [] devWeights = null;
		int error;
		for (int i = 0; i < 20; i++) {
			Collections.shuffle(dataset);
			double[] thisWeights = trainWeights(dataset, initial_weights, learning_rate[bestL_rate], 1);
			error = errors(developmentSet,thisWeights);
			accuracy[i] = 1 - (error/developmentSet.size());
			if (error < minDevError) {
				minDevError = error;
				devWeights = thisWeights;
			}
		}
		System.out.println(String.format("Development set accuracy: %f", 100*(1 - (double) (minDevError/developmentSet.size()))));
		
		// Evaluate the test set
		int testError = errors(test,devWeights);
		System.out.println(String.format(" Test set accuracy: %f", 100*(1 - (double) (testError/test.size()))));
		
	}
	private static int[] findBestLearningRate(ArrayList<ArrayList<double[]>> trainings, double[] weights, double[] learning_rate, int epoch) {
		int bestIndex = 0;
		int minError = Integer.MAX_VALUE;
		int size = trainings.size();		
		
		for (int l_rate = 1 ; l_rate < learning_rate.length; l_rate++) {
			int totalError = 0;
			for (int index = 0; index < size; index++) {
				ArrayList<double[]> dataset = new ArrayList<>();
				for (int i = 0; i < size; i++ ) {
					if (i != index) {
						dataset.addAll(trainings.get(i));
					}
				}
				double[] thisWeights = trainWeights(dataset, weights, learning_rate[l_rate], epoch);
				totalError += errors(trainings.get(index),thisWeights);
			}
			
			if (totalError < minError) {
				minError = totalError;
				bestIndex = l_rate;
			}
		}
		return new int[]{bestIndex,minError};
	}
	private static int errors (ArrayList<double[]> test , double[] weights) {
		int error = 0;
		for (double[] data : test) {
			if (predict(data, weights)) {
				error++;
			}
		}
		return error;
	}
	private static double[] trainWeights(ArrayList<double[]> dataset, double[] weights, double learning_rate, int epoch) {
		for (int i = 0; i < epoch; i++) {
			for (double[] data : dataset) {
				if (predict(data,weights)) {
					weights = update(data, weights,learning_rate);
				} 
			}
		}
		return weights;
	}

	private static double[] update(double[] data, double[] weights, double l_rate) {
		double y = data[0];
		weights[0] = weights[0] + l_rate * data[0]; // bias
		for (int i = 1; i < data.length; i++) {
			weights[i] = weights[i] + l_rate * y * data[i];
		}
		return weights;
	}

	private static boolean predict(double[] data, double[] weights) {
		double sum = weights[0]; // bias
		for (int i = 1; i < data.length; i++) {
			sum += (double) weights[i] * data[i];
		}
		return sum * data[0] > 0;
	}

	private static double[] initialWeight(int size) {
		double min = -0.01;
		double max = 0.01;
		double[] weight = new double[size];
		for (int i = 0; i < size; i++) {
			weight[i] = min + Math.random() * (max - min);
			;
		}
		return weight;
	}

	private static ArrayList<double[]> parseString(InputStream in) {
		ArrayList<double[]> dataset = new ArrayList<double[]>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] st = line.split("\\s+");
				double[] data = new double[numberOfFeatures + 1]; // 1 for
																	// outcome
				data[0] = Double.parseDouble(st[0]);
				String[] numb;
				int index;
				double value;
				for (int i = 1; i < st.length; i++) {
					numb = st[i].split(":");
					index = Integer.parseInt(numb[0]);
					value = Double.parseDouble(numb[1]);
					data[index] = value;
				}
				dataset.add(data);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dataset;
	}

}
