package homework5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Utility {
	protected static double test(ArrayList<ArrayList<Integer>> test_data, HashMap<Integer, Double> weight) {
		double tot = 0.0;
		for (ArrayList<Integer> d : test_data) {
			tot += verify_y_and_weight(weight,d);
		}
		return tot/test_data.size();
	}

	private static int verify_y_and_weight(HashMap<Integer, Double> weight, ArrayList<Integer> d) {
		double sum = weight.get(0);//bias
		double temp;
		for (int i = 1; i < d.size(); i++) {
			int key = d.get(i);
			temp = weight.containsKey(key) ? weight.get(key) : 0.0;
			sum += temp;
		}
		
		return sum *d.get(0) > 0 ? 1 : 0;
	}

	protected static ArrayList<ArrayList<Integer>> featurize(InputStream in) {
		ArrayList<ArrayList<Integer>> dataset = new ArrayList<ArrayList<Integer>>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] st = line.split("\\s+");
				ArrayList<Integer> data = new ArrayList<>();
				// outcome
				data.add(Integer.parseInt(st[0]));
				String[] features;
				for (int i = 1; i < st.length; i++) {
					features = st[i].split(":");
					data.add(Integer.parseInt(features[0]));
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
