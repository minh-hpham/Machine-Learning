package project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class Utility {
	protected final static int numberOfFeatures = 16;
	protected final static double[] learning_rate = { 1, 0.1, 0.01 };

	protected static void estimateFeature(ArrayList<double[]> input) {
		Map<Double, Integer> map = new TreeMap<Double,Integer>();
		int l = input.size();
		double key;
		int val;
		double one = 0;
		for (int i = 0; i < l; i++) {
			if (input.get(i)[0] == one) {
				key = input.get(i)[16];
				if (map.containsKey(key)) {
					val = map.get(key);
					map.put(key, val + 1);
				} else {
					map.put(key, 1);
				}
			}
		}

		for (Double k : map.keySet()) {
			System.out.println(String.format("%f %d", k, map.get(k)));
		}

	}

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
		weights[0] = y > 0 ? weights[0] + l_rate * y : weights[0] - l_rate; // bias
		for (int i = 1; i < data.length; i++) {
			weights[i] = y > 0 ? weights[i] + l_rate * y * data[i] : weights[i] + l_rate * (-1) * data[i];
		}
		return weights;
	}

	protected static int predictLabel(double[] data, double[] weights) {
		double sum = weights[0]; // bias
		for (int i = 1; i < data.length; i++) {
			sum += (double) weights[i] * data[i];
		}
		int val = sum > 0 ? 1 : 0;
		return val;
	}

	protected static boolean predict(double[] data, double[] weights) {
		double sum = weights[0]; // bias
		for (int i = 1; i < data.length; i++) {
			sum += (double) weights[i] * data[i];
		}
		double val = sum > 0 ? 1 : 0;
		return val != data[0];
	}

	protected static double[] initialWeight(int size) {
		double min = -0.01;
		double max = 0.01;
		double[] weight = new double[size];
		for (int i = 0; i < size; i++) {
			Random random = new Random();
			weight[i] = min + random.nextDouble() * (max - min);
		}
		return weight;
	}

	protected static ArrayList<Integer> parseID(InputStream in) {
		ArrayList<Integer> dataset = new ArrayList<Integer>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				dataset.add(Integer.parseInt(line));
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

	protected static void writeToCSV(double[] trainWeight, ArrayList<Integer> inputID, ArrayList<double[]> input,
			String filename) throws FileNotFoundException {
		// BufferedWriter writer = null;
		File file = new File(filename);
		FileWriter writer = null;
		try {
			// writer = new BufferedWriter(new FileWriter(filename));
			writer = new FileWriter(filename, false);
			writer.append("Id,Prediction");
			writer.append("\n");
			// writer.write("Id");
			// writer.write(",");
			// writer.write("Prediction");
			// writer.newLine();
			int inputSize = input.size();
			for (int i = 0; i < inputSize; i++) {
				int label = predictLabel(input.get(i), trainWeight);
				writer.append(inputID.get(i).toString());
				writer.append(",");
				writer.append(String.valueOf(label));
				writer.append("\n");
				// writer.write(inputID.get(i));
				// writer.write(",");
				// writer.write(label);
				// writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.flush();
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
