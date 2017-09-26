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
import java.util.Random;

public class SimplePerceptron extends Perceptron {
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
		int bestL_rate = findBestLearningRate(trainings, initial_weights, learning_rate, 10);

		// Train the classifier for 20 epochs
		double[] bestWeights = trainWeightsWithBestHyperparameter(initial_weights,dataset,developmentSet,learning_rate[bestL_rate],20);
		// Evaluate the test set
		int testError = errors(test, bestWeights);
		System.out.println(String.format(" Test set accuracy: %f", 100 * (1 - (double) (testError / test.size()))));

	}
	protected static double[] trainWeightsWithBestHyperparameter(double[] weights, ArrayList<double[]> dataset,
			ArrayList<double[]> developmentSet, double hyperparameter, int epoch) {
		double[] accuracy = new double[epoch];
		int update = 0;
		int minDevError = Integer.MAX_VALUE;
		double[] bestWeights = null;
		double[] devWeights = null;		
		
		int bestEpoch = -1;
		for (int e = 0; e < 20; e++) {			
			Collections.shuffle(dataset);
			devWeights = weights;
			for (double[] data : dataset) {
				if (predict(data, devWeights)) {
					devWeights = update(data, devWeights, hyperparameter);
					update++;
				}
			}
			int error = errors(developmentSet, devWeights);
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
	protected static int findBestLearningRate(ArrayList<ArrayList<double[]>> trainings, double[] weights,
			double[] learning_rate, int epoch) {
		int bestIndex = 0;
		int minError = Integer.MAX_VALUE;
		int size = trainings.size();
		ArrayList<double[]> dataset = null;
		for (int l_rate = 1; l_rate < learning_rate.length; l_rate++) {
			
			double[] thisWeights = weights;
			// run n-1 epoch times. don't count error
			for (int e = 0; e < epoch-1; e++) {
				for (int index = 0; index < size; index++) {
					 dataset = new ArrayList<>();
					for (int i = 0; i < size; i++) {
						if (i != index) {
							dataset.addAll(trainings.get(i));
						}
					}
					thisWeights = trainWeights(dataset, thisWeights, learning_rate[l_rate]);
				}
			}
			
			// run last epoch. count error
			thisWeights = weights;
			int totalError = 0;
			for (int index = 0; index < size; index++) {
				dataset = new ArrayList<>();
				for (int i = 0; i < size; i++) {
					if (i != index) {
						dataset.addAll(trainings.get(i));
					}
				}
				thisWeights = trainWeights(dataset, thisWeights, learning_rate[l_rate]);
				totalError += errors(trainings.get(index), thisWeights);
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
	

}
