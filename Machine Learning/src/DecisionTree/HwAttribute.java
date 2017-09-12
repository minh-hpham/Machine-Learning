package DecisionTree;
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
	enum totalVowels_lessequal_5 {
		yes,no
	}
	enum firstLetterBTLKNWG {
		yes,no
	}
	enum firstLetterARFH {
		yes,no
	}
	enum ratio_less_1 {
		yes,no
	}
	
	public HwAttribute(String name, int value) {
		super(name, value);
	}
	
}
