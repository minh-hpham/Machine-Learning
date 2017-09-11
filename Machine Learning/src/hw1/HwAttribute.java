package hw1;
// Specifies features for the badges dataset
public class HwAttribute extends Attribute {
	
	public static final int yes = 1;
	public static final int no = 0;
	public static final int positive = 1;
	public static final int negative = 0;
	
	enum outcome {
		positive,negative
	}
	
	enum firstname_longer_than_lastname {
		yes,no
	}
	enum have_middlename {
		yes,no
	}
	enum firstname_startend_sameletter {
		yes,no
	}
	enum firstname_alphabetical_before_lastname {
		yes,no
	}
	enum firstname_2ndletter_vowel {
		yes,no
	}
	enum lastname_numLetters_even {
		yes,no
	}
	
	public HwAttribute(String name, int value) {
		super(name, value);
	}
	/*
	public static int getAttributeSize(String name) {
		if(name.equals("firstname_longer_than_lastname")) {
			return firstname_longer_than_lastname.values().length;
		} else if (name.equals("have_middlename")) {
			return have_middlename.values().length;
		} else if (name.equals("firstname_startend_sameletter")) {
			return firstname_startend_sameletter.values().length;
		} else if (name.equals("firstname_alphabetical_before_lastname")) {
			return firstname_alphabetical_before_lastname.values().length;
		} else if (name.equals("firstname_2ndletter_vowel")) {
			return firstname_2ndletter_vowel.values().length;
		} else if (name.equals("lastname_numLetters_even")) {
			return lastname_numLetters_even.values().length;
		}
		return outcome.values().length;
	}*/
}
