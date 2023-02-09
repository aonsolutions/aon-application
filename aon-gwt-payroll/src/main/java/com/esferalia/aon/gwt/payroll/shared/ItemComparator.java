package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.watson.util.AonStringUtils.romanIntValue;

import java.util.Comparator;

import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;





public class  ItemComparator<E extends Enum<?>> implements Comparator<Item<E>> {
	
	private static RegExp ORDER = RegExp.compile(
			"^\\[(\\d+)\\].*$");
	private static RegExp ROMAN = RegExp.compile(
			"^((C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3}))([\\W_]+.*)?$",
			"i");

	public static <E extends Enum<?>> int comparator(Item<E> p0, Item<E> p1) {
		String description0 = p0.getDescription();
		String description1 = p1.getDescription();

		// By order
		MatchResult order0 = ORDER.exec(description0);
		MatchResult order1 = ORDER.exec(description1);
		if ( order0 != null && order1 == null)
			return -1; 			//p0 < p1
		if ( order1 != null && order0 == null )
			return 1;			//p0 > p1
		if ( order1 != null && order0 != null ) {
			int compareTo = Integer.parseInt(order0.getGroup(1)) - Integer.parseInt(order1.getGroup(1)); 
			if (compareTo != 0) 
				return compareTo;
		}
			
		// By scope ( reverse )
		Scope scope0 = p0.getScope();
		Scope scope1 = p1.getScope();
		if ( scope0 != null && scope1 == null )
			return -1;
		if ( scope0 == null && scope1 != null )
			return 1;
		int compareTo = scope0 == scope1 ? 0 : scope1.ordinal()-scope0.ordinal();
		if (compareTo != 0) 
			return compareTo;

		// By type
		E type0 = p0.getType();
		E type1 = p1.getType();
		if ( type0 != null && type1 == null )
			return 1;
		if ( type0 == null && type1 != null )
			return -1;
		compareTo = type0 == type1 ? 0 : type0.ordinal()-type1.ordinal();
		if (compareTo != 0) 
			return compareTo;


		// By description
		if (description0 != null && description1 == null )
			return 1;
		if (description0 == null && description1 != null )
			return -1;
		
		if (description0 != null &&  description1 != null ){
			try {
				MatchResult matcher0 = ROMAN.exec(description0.trim());
				MatchResult matcher1 = ROMAN.exec(description1.trim());
				if (matcher0 != null && matcher1 != null ) {
					int roman0 = romanIntValue(matcher0.getGroup(1));
					int roman1 = romanIntValue(matcher1.getGroup(1));
					if (roman0 != roman1)
						return roman0 - roman1;
				}
			}catch ( IllegalArgumentException e){
				// Not roman numeral. Due a bug at 'ROMAN' regular expression.
				// 'ROMAN' matches empty strings and strings like '[1] SALARIO'
			}
		} // Noy null

		compareTo = description0 == description1 ? 0 : description0.compareTo(description1);
		if (compareTo != 0) 
			return compareTo;

		// By name
		String name0 = p0.getName();
		String name1 = p1.getName();
		if (name0 != null && name1 == null )
			return 1;
		if (name0 == null && name1 != null )
			return -1;
		compareTo = name0 == name1 ? 0 : name0.compareTo(name1);
		if (compareTo != 0) 
			return compareTo;

		// By id
		Integer id0= p0.getId();
		Integer id1= p1.getId()	;
		if ( id0 == id1 )
			return 0;
		if ( id0 == null )
			return -1;
		if ( id1 == null )
			return 1;
		return Math.abs(id0) - Math.abs(id1);
	}

	@Override
	public int compare(Item<E> p0, Item<E> p1) {
	    return ItemComparator.comparator(p0,p1);
	}
}