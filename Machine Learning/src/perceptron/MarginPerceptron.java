package perceptron;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

public class MarginPerceptron extends Perceptron{
	private final static double[] margins = {1, 0.1, 0.01};
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
		
		double[] initial_weights = initialWeight(numberOfFeatures + 1); // include // bias
		
		// Run cross validation for ten epochs for each hyper-parameter
		// combination to get the best hyper-parameter setting
		double[] bestRates = findBestLearningRate(trainings, initial_weights, learning_rate,margins, 10);
		double bestLearningRate = bestRates[0];
		double bestMargin = bestRates[1];
		// Train the classifier for 20 epochs
		double[] bestWeights = trainWeightsWithBestHyperparameter(initial_weights,dataset,developmentSet,bestLearningRate,bestMargin,20);
		// Evaluate the test set
		int testError = errors(test, bestWeights,bestMargin);
		System.out.println(String.format("Test set accuracy: %f", 100 * (1 -((double)testError / (double)test.size()))));

	}

	private static double[] findBestLearningRate(ArrayList<ArrayList<double[]>> trainings, double[] devWeights,
			double[] learningRate, double[] margins, int epoch) {
		double bestMargin = -1;
		double bestLearningRate = -1;
		int minError = Integer.MAX_VALUE;
		int size = trainings.size();
		double [] initial_weights = null;
		ArrayList<double[]> dataset = null;
		for (int l_rate = 1; l_rate < learning_rate.length; l_rate++) {
			initial_weights = devWeights;
			double rate = learning_rate[l_rate];
			double margin;
			for (int m = 0; m < margins.length; m++) {
				margin = margins[m];
				// run n-1 epoch times. don't count error
				for (int e = 1; e < epoch; e++) {
					rate = rate / (1 + e);
					for (int index = 0; index < size; index++) {
						dataset = new ArrayList<>();
						for (int i = 0; i < size; i++) {
							if (i != index) {
								dataset.addAll(trainings.get(i));
							}
						}
						initial_weights = trainWeights(dataset, initial_weights, rate, margin);
					}
				}
				// run last epoch. count error
				rate = rate / (1 + epoch);
				int totalError = 0;
				for (int index = 0; index < size; index++) {
					dataset = new ArrayList<>();
					for (int i = 0; i < size; i++) {
						if (i != index) {
							dataset.addAll(trainings.get(i));
						}
					}
					initial_weights = trainWeights(dataset, initial_weights, rate, margin);
					totalError += errors(trainings.get(index), initial_weights, margin);
				}
				if (totalError < minError) {
					minError = totalError;
					bestMargin = margin;
					bestLearningRate = learning_rate[l_rate];
				} 
			}
		}
		int trainingsSize = dataset.size() + trainings.get(size-1).size();
		double accuracy = 100 * (1 - ((double)minError/(double)trainingsSize));
		System.out.println(String.format("The best hyper-parameter: %f", bestLearningRate));
		System.out.println(String.format("The best margin: %f", bestMargin));
		System.out.println(String.format("The cross-validation accuracy for the best hyperparameter: %f", accuracy));
		return new double[]{bestLearningRate,bestMargin};
	}

	private static double[] trainWeightsWithBestHyperparameter(double[] devWeights, ArrayList<double[]> dataset,
			ArrayList<double[]> developmentSet, double hyperparameter, double bestMargin, int epoch) {
		double[] accuracy = new double[epoch];
		int update = 0;
		int minDevError = Integer.MAX_VALUE;
		double[] bestWeights = null;
		
		for (int e = 0; e < 20; e++) {	
			hyperparameter = hyperparameter/(1+1+e);
			Collections.shuffle(dataset);
			for (double[] data : dataset) {
				if (predict(data, devWeights,bestMargin)) {
					devWeights = update(data, devWeights, hyperparameter);
					update++;
				}
			}
			int error = errors(developmentSet, devWeights,bestMargin);
			accuracy[e] = 100 - 100*((double)error / (double)developmentSet.size());
			if (error < minDevError) {
				minDevError = error;
				bestWeights = devWeights;
			}
		}
		System.out.println(String.format("The total number of updates the learning algorithm performs on the training set: %d", update));
		System.out.println("Development set accuracy:");
		System.out.println("Epoch:\tAccuracy:");
		for (int i = 0; i < epoch; i++) {
			System.out.println(String.format("%d\t%f", i+1,accuracy[i]));
		}
		
		return bestWeights;
	}

	private static boolean predict(double[] data, double[] weights,double margin) {
		double sum = weights[0]; // bias
		for (int i = 1; i < data.length; i++) {
			sum += (double) weights[i] * data[i];
		}
		return sum * data[0] < margin;
	}
	protected static double[] trainWeights(ArrayList<double[]> dataset, double[] weights, double rate, double margin) {
		for (double[] data : dataset) {
			if (predict(data, weights,margin)) {
				weights = update(data, weights, rate);
			}
		}
		return weights;
	}

	protected static int errors(ArrayList<double[]> test, double[] weights, double margin) {
		int error = 0;
		for (double[] data : test) {
			if (predict(data, weights,margin)) {
				error++;
			}
		}
		return error;
	}

}
