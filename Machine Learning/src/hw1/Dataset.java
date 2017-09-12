
package hw1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Dataset {
	/*private static int positive = 0;
	private static int datasize = 0;
*/	public static ArrayList<Data> buildDatasetWithAttributes(InputStream in) {
		BufferedReader reader = null;
		ArrayList<Data> dataset = new ArrayList<Data>();

		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line;
			Data data = null;
			int line_len = 0;
			ArrayList<HwAttribute> data_attributes;
			
			while ((line = reader.readLine()) != null) {
				//datasize++;
				String[] st = line.toLowerCase().split("\\s+");
				line_len = st.length;
				data_attributes = new ArrayList<HwAttribute>();
				data = new Data();
				// data's label
				if (st[0].equals("+")) {
					//positive++;
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
				if (st[1].length() > 1) {
					if ("aeiou".indexOf(st[1].charAt(1)) < 0) {
						data_attributes.add(new HwAttribute("firstname_2ndletter_vowel", HwAttribute.no));
					} else {
						data_attributes.add(new HwAttribute("firstname_2ndletter_vowel", HwAttribute.yes));
					}
				} else {
					data_attributes.add(new HwAttribute("firstname_2ndletter_vowel", HwAttribute.no));
				}
				// 6. Is the number of letters in their last name even?
				if (st[line_len-1].length() % 2 == 0) {
					data_attributes.add(new HwAttribute("lastname_numLetters_even", HwAttribute.yes));
				} else {
					data_attributes.add(new HwAttribute("lastname_numLetters_even", HwAttribute.no));
				}
				// additional features
				// total number of vowels in first and last name <= 5 
				int countVowels = 0;
				for (int i = 0 ; i < st[1].length(); i++) {
					if ("aeiou".indexOf(st[1].charAt(i)) >= 0) {
						countVowels++;
					}
				}
				for (int i = 0 ; i < st[line_len -1].length(); i++) {
					if ("aeiou".indexOf(st[line_len-1].charAt(i)) >= 0) {
						countVowels++;
					}
				}
				if (countVowels <= 5) {
					data_attributes.add(new HwAttribute("totalVowels_lessequal_5", HwAttribute.yes));
				}else {
					data_attributes.add(new HwAttribute("totalVowels_lessequal_5", HwAttribute.no));
				}
				
				// first letter of first name is in {B,T,L,K,N,W,G}
				if ("btlknwg".indexOf(st[1].charAt(0)) >= 0) {
					data_attributes.add(new HwAttribute("firstLetterBTLKNWG", HwAttribute.yes));
				}else {
					data_attributes.add(new HwAttribute("firstLetterBTLKNWG", HwAttribute.no));
				}
				// first letter of first name is not in {A,R,F,H}
				if ("arfh".indexOf(st[1].charAt(0)) < 0) {
					data_attributes.add(new HwAttribute("firstLetterARFH", HwAttribute.yes));
				}else {
					data_attributes.add(new HwAttribute("firstLetterARFH", HwAttribute.no));
				}
				// ratio of firstname by last name is less than 1
				if((st[1].length()/st[line_len-1].length()) < 1) {
					data_attributes.add(new HwAttribute("ratio_less_1", HwAttribute.yes));
				} else {
					data_attributes.add(new HwAttribute("ratio_less_1", HwAttribute.no));
				}
				// last letter of first name has aschii value less than 
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
	/*public static int commonLabel() {
		if ((positive/datasize) > 0.5) {
			return HwAttribute.positive;
		}
		return HwAttribute.negative;
	}*/
}
