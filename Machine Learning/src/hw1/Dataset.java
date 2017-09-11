package hw1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Dataset {
	private static int positive = 0;
	private static int datasize = 0;
	public static ArrayList<Data> buildDatasetWithAttributes(InputStream in) {
		BufferedReader reader = null;
		ArrayList<Data> dataset = new ArrayList<Data>();

		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line;
			Data data = null;
			int line_len = 0;
			ArrayList<HwAttribute> data_attributes;
			
			while ((line = reader.readLine()) != null) {
				datasize++;
				String[] st = line.toLowerCase().split("\\s+");
				line_len = st.length;
				data_attributes = new ArrayList<HwAttribute>();
				data = new Data();
				// data's label
				if (st[0].equals("+")) {
					positive++;
					data_attributes.add(new HwAttribute("outcome", HwAttribute.positive));
				} else {
					data_attributes.add(new HwAttribute("outcome", HwAttribute.negative));
				}
				// 1. Is their first name longer than their last name?
				if (st[1].length() > st[line_len - 1].length()) {
					data_attributes.add(new HwAttribute("firstname_longer_than_lastname", HwAttribute.yes));
				}else {
					data_attributes.add(new HwAttribute("firstname_longer_than_lastname", HwAttribute.no));
				}
				// 2. Do they have a middle name?
				if (st.length > 3) {
					data_attributes.add(new HwAttribute("have_middlename", HwAttribute.yes));
				} else {
					data_attributes.add(new HwAttribute("have_middlename", HwAttribute.no));
				}
				// 3. Does their first name start and end with the same letter?
				// (ie "Ada")
				if (st[1].charAt(0) == st[1].charAt(st[1].length()-1)) {
					data_attributes.add(new HwAttribute("firstname_startend_sameletter", HwAttribute.yes));
				} else {
					data_attributes.add(new HwAttribute("firstname_startend_sameletter", HwAttribute.no));
				}
				// 4. Does their first name come alphabetically before their
				// last name? (ie "Dan Klein" because "d" comes before "k")
				if (st[1].charAt(0) < st[line_len-1].charAt(0)) {
					data_attributes.add(new HwAttribute("firstname_alphabetical_before_lastname", HwAttribute.yes));
				} else {
					data_attributes.add(new HwAttribute("firstname_alphabetical_before_lastname", HwAttribute.no));
				}
				// 5. Is the second letter of their first name a vowel
				// (a,e,i,o,u)?
				if ("aeiou".indexOf(st[1].charAt(1)) < 0) {
					data_attributes.add(new HwAttribute("firstname_2ndletter_vowel", HwAttribute.no));
				} else {
					data_attributes.add(new HwAttribute("firstname_2ndletter_vowel", HwAttribute.yes));
				}
				// 6. Is the number of letters in their last name even?
				if (st[line_len-1].length() % 2 == 0) {
					data_attributes.add(new HwAttribute("lastname_numLetters_even", HwAttribute.yes));
				} else {
					data_attributes.add(new HwAttribute("lastname_numLetters_even", HwAttribute.no));
				}
				
				data.setAttributes(data_attributes);
				dataset.add(data);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dataset;
	}
	public static int commonLabel() {
		if ((positive/datasize) > 0.5) {
			return HwAttribute.positive;
		}
		return HwAttribute.negative;
	}
}
