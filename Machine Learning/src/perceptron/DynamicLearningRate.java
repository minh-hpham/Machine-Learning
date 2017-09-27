package perceptron;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
/*
 * UPDATE LEARNING RATE AT EACH EPOCH
 */
public class DynamicLearningRate extends Perceptron{

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
		System.out.println(String.format("Test set accuracy: %f", 100 * (1 -  ((double)testError / (double)test.size()))));
	}

	private static double[] trainWeightsWithBestHyperparameter(double[] devWeights, ArrayList<double[]> dataset,
			ArrayList<double[]> developmentSet, double hyperparameter, int epoch) {
		double[] accuracy = new double[epoch];
		int totalUpdate = 0;
		int update = 0;
		int minDevError = Integer.MAX_VALUE;
		double[] bestWeights = null;
		
		for (int e = 0; e < 20; e++) {	
			hyperparameter = hyperparameter/(1+1+e);
			Collections.shuffle(dataset);
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
				totalUpdate = update;
			}
		}
		System.out.println(String.format("The total number of updates the learning algorithm performs on the training set: %d", totalUpdate));
		System.out.println("Development set accuracy:");
		System.out.println("Epoch:\tAccuracy:");
		for (int i = 0; i < epoch; i++) {
			System.out.println(String.format("%d\t%f", i+1,accuracy[i]));
		}
		return bestWeights;
	}

	private static int findBestLearningRate(ArrayList<ArrayList<double[]>> trainings, double[] devWeights,
			double[] learningRate, int epoch) {
		int bestIndex = 0;
		int minError = Integer.MAX_VALUE;
		int size = trainings.size();
		double[] initial_weights = null;
		ArrayList<double[]> dataset = null;
		for (int l_rate = 1; l_rate < learning_rate.length; l_rate++) {
			initial_weights = devWeights;
			double rate = learning_rate[l_rate];
			
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
					initial_weights = trainWeights(dataset, initial_weights, rate);
				}
			}
			
			// run last epoch. count error
			rate = rate/(1+epoch);
			int totalError = 0;
			for (int index = 0; index < size; index++) {
				dataset = new ArrayList<>();
				for (int i = 0; i < size; i++) {
					if (i != index) {
						dataset.addAll(trainings.get(i));
					}
				}
				initial_weights = trainWeights(dataset, initial_weights, rate);
				totalError += errors(trainings.get(index), initial_weights);
			}
			
			if (totalError < minError) {
				minError = totalError;
				bestIndex = l_rate;
			}
		}
		int trainingsSize = dataset.size() + trainings.get(size-1).size();
		double accuracy = 100 * (1 - ((double)minError/(double)trainingsSize));
		System.out.println(String.format("The best hyper-parameters: %f", learning_rate[bestIndex]));
		System.out.println(String.format("The cross-validation accuracy for the best hyperparameter: %f", accuracy));
		return bestIndex;
	}

}