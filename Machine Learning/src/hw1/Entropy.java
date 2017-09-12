package hw1;

import java.util.ArrayList;

public class Entropy {
	public static double calculateEntropy(ArrayList<Data> dataset, int numb_label) {
		double entropy = 0;
		if (dataset.size() == 0) {
			return 0;
		}
		for (int i = 0; i < numb_label; i++) {
			int count = 0;
			for (int j = 0; j < dataset.size(); j++) {
				Data data = dataset.get(j);
				if (data.getAttributes().get(0).getValue() == i) {
					count++;
				}
			}
			if (count > 0) {
				double p = count / (double) dataset.size();
				entropy += -p * (Math.log(p) / Math.log(2));
			}
		}
		return entropy;
	}

	public static double calculateGain(double rootEntropy, ArrayList<Double> subEntropies, ArrayList<Integer> setSizes,
			int data) {
		double gain = rootEntropy;

		for (int i = 0; i < subEntropies.size(); i++) {
			gain += -((setSizes.get(i) / (double) data) * subEntropies.get(i));
		}

		return gain;
	}
	public static double calculateGain(double rootEntropy,double[] subEntropies, int[] setSizes,int data) {
		double gain = rootEntropy;

		for (int i = 0; i < subEntropies.length; i++) {
			
			gain += (double) (setSizes[i] * subEntropies[i])/ data ;
		}

		return gain;
	}
}
