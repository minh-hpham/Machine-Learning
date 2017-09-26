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
		int bestL_rate = findBestLearningRate(trainings, initial_weights, learning_rate,margins, 10);
		double bestMargin = 0.1;
		// Train the classifier for 20 epochs
		double[] bestWeights = trainWeightsWithBestHyperparameter(initial_weights,dataset,developmentSet,learning_rate[bestL_rate],20);
		// Evaluate the test set
		int testError = errors(test, bestWeights,bestMargin);
		System.out.println(String.format(" Test set accuracy: %f", 100 * (1 - (double) (testError / test.size()))));

	}

	private static int findBestLearningRate(ArrayList<ArrayList<double[]>> trainings, double[] initial_weights,
			double[] learningRate, double[] margins2, int epoch) {
		double margin = margins2[0];
		
		int bestIndex = 0;
		int minError = Integer.MAX_VALUE;
		int size = trainings.size();
		ArrayList<double[]> dataset = null;
		for (int l_rate = 1; l_rate < learning_rate.length; l_rate++) {
			double rate = learning_rate[l_rate];
			double[] thisWeights = initial_weights;
			// run n-1 epoch times. don't count error
			for (int e = 1; e < epoch; e++) {
				rate = rate/(1+e);
				for (int index = 0; index < size; index++) {
					 dataset = new ArrayList<>();
					for (int i = 0; i < size; i++) {
						if (i != index) {
							dataset.addAll(trainings.get(i));
						}
					}
					thisWeights = trainWeights(dataset, thisWeights, rate,margin);
				}
			}
			
			// run last epoch. count error
			rate = rate/(1+epoch);
			thisWeights = initial_weights;
			int totalError = 0;
			for (int index = 0; index < size; index++) {
				dataset = new ArrayList<>();
				for (int i = 0; i < size; i++) {
					if (i != index) {
						dataset.addAll(trainings.get(i));
					}
				}
				thisWeights = trainWeights(dataset, thisWeights, rate,margin);
				totalError += errors(trainings.get(index), thisWeights,margin);
			}
			
			if (totalError < minError) {
				minError = totalError;
				bestIndex = l_rate;
			}
		}
		int trainingsSize = dataset.size() + trainings.get(size-1).size();
		double accuracy = 100 * (1 - (minError/trainingsSize));
		System.out.println(String.format("The best hyper-parameters: %f", learning_rate[bestIndex]));
		System.out.println(String.format("The cross-validation accuracy for the best hyperparameter: %f", accuracy));
		return bestIndex;
	}

	private static double[] trainWeightsWithBestHyperparameter(double[] initial_weights, ArrayList<double[]> dataset,
			ArrayList<double[]> developmentSet, double hyperparameter, int epoch) {
		double margin =0.1;
		double[] accuracy = new double[epoch];
		int update = 0;
		int minDevError = Integer.MAX_VALUE;
		double[] bestWeights = null;
		double[] devWeights = null;		
		
		int bestEpoch = -1;
		for (int e = 0; e < 20; e++) {	
			hyperparameter = hyperparameter/(1+1+e);
			Collections.shuffle(dataset);
			devWeights = initial_weights;
			for (double[] data : dataset) {
				if (predict(data, devWeights,margin)) {
					devWeights = update(data, devWeights, hyperparameter);
					update++;
				}
			}
			int error = errors(developmentSet, devWeights,margin);
			accuracy[e] = 100 - 100*((double)error / (double)developmentSet.size());
			if (error < minDevError) {
				minDevError = error;
				bestWeights = devWeights;
				bestEpoch = e;
			}
		}
		System.out.println(String.format("Best epoch: %d",bestEpoch));
		System.out.println(String.format("The total number of updates the learning algorithm performs on the training set: %d", update));
		System.out.println("3f: Data for graph");
		for (int i = 0; i < epoch; i++) {
			System.out.println(String.format("%d\t%f", i+1,accuracy[i]));
		}
		System.out.println(String.format("Development set accuracy: %f", accuracy[bestEpoch]));
		
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
