package homework5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Utility {
	protected final static int numberOfFeatures = 69;
	protected final static double[] learning_rate = { 1, 0.1, 0.01 };

	protected static double[] trainWeights(ArrayList<double[]> dataset, double[] weights, double rate) {
		for (double[] data : dataset) {
			if (predict(data, weights)) {
				weights = update(data, weights, rate);
			}
		}
		return weights;
	}

	protected static int errors(ArrayList<double[]> test, double[] weights) {
		int error = 0;
		for (double[] data : test) {
			if (predict(data, weights)) {
				error++;
			}
		}
		return error;
	}


	protected static double[] update(double[] data, double[] weights, double l_rate) {
		double y = data[0];
		weights[0] = weights[0] + l_rate * y; // bias
		for (int i = 1; i < data.length; i++) {
			weights[i] = weights[i] + l_rate * y * data[i];
		}
		return weights;
	}

	protected static boolean predict(double[] data, double[] weights) {
		double sum = weights[0]; // bias
		for (int i = 1; i < data.length; i++) {
			sum += (double) weights[i] * data[i];
		}
		return sum * data[0] < 0;
	}

	protected static double[] initialWeight(int size) {
		double min = -0.01;
		double max = 0.01;
		double[] weight = new double[size];
		for (int i = 0; i < size; i++) {
			Random random = new Random();
			weight[i] = min +  random.nextDouble()*(max - min);
		}
		return weight;
	}

	protected static ArrayList<double[]> parseString(InputStream in) {
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
