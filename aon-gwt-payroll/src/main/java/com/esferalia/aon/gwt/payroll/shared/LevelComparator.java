package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.watson.util.AonStringUtils.romanIntValue;

import java.util.Comparator;

import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class LevelComparator implements Comparator<Level> {

	private static RegExp ROMAN = RegExp.compile(
			"^((CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3}))([\\W_]+.*)?$",
			"i");

	public int compare(Level l0, Level l1) {

		String description0 = l0.getDescription();
		String description1 = l1.getDescription();
		if (description0 == description1)
			return 0;
		if (description0 == null)
			return -1;
		if (description1 == null)
			return 1;
		try {
			MatchResult matcher0 = ROMAN.exec(description0.trim());
			MatchResult matcher1 = ROMAN.exec(description1.trim());
	
			if (matcher0 != null && matcher1 != null ) {
				int roman0 = romanIntValue(matcher0.getGroup(1));
				int roman1 = romanIntValue(matcher1.getGroup(1));
				if (roman0 != roman1)
					return roman0 - roman1;
			}
		} catch (IllegalArgumentException e){
			// Not roman numeral. Due a bug at 'ROMAN' regular expression.
			// 'ROMAN' matches empty strings and strings like '. 1'.
		}

		return description0.compareTo(description1);

	};
}
