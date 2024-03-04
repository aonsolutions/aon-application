package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.watson.util.AonStringUtils.romanIntValue;

import java.util.Comparator;
import java.util.Objects;

import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;





public class  ItemComparator<E extends Enum<?>> implements Comparator<Item<E>> {
	
	private static RegExp ORDER = RegExp.compile(
			"^\\[(\\d+)\\].*$");
	private static RegExp ROMAN = RegExp.compile(
			"^((C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3}))([\\W_]+.*)?$",
			"i");


	@Override
	public int compare(Item<E> p0, Item<E> p1) {
		
		String name0 = p0.getName();
		String name1 = p1.getName();
		
		String description0 = p0.getDescription();
		String description1 = p1.getDescription();

		for ( String log: new String [] {"ADVERTENCIA", "NOTA", "INFO" }  ) {
		    boolean log0 = Objects.equals(log, name0);
		    boolean log1 = Objects.equals(log, name1);
		    if (log0) {
			
			return log1 ? compareByOrder(description0, description1) : 1; // p1 < p0
		    } else if (log1) {
			return -1; // p0 < p1
		    }
		}

		for (String bonus : new String[] { "BONIF" }) {
			boolean bonus0 = Objects.equals(bonus, name0);
			boolean bonus1 = Objects.equals(bonus, name1);
			if (bonus0) {
				return bonus1 ? compareByOrder(description0, description1) : 1; // p1 < p0
			} else if (bonus1) {
				return -1; // p0 < p1
			}
		}

		for ( String special: new String [] {"PPE", "FIX_BASE_CGC_MIN", "PPE_E" }  ) {
		    boolean special0 = Objects.equals(special, name0);
		    boolean special1 = Objects.equals(special, name1);
		    if (special0 && !special1 ) {
		    	return 1; // p1 < p0
		    } else if (!special0 && special1 ) {
		    	return -1; // p0 < p1
		    }
		}

		int compareTo = compareByOrder(description0, description1); 
		if (compareTo != 0) 
			return compareTo;
			
		// By scope ( reverse )
		compareTo = compareByScope(p0, p1);
		if (compareTo != 0) 
			return compareTo;

		// By type
		compareTo = compareByType(p0, p1);
		if (compareTo != 0) 
			return compareTo;


		compareTo = compareByDescription(description0, description1);
		if (compareTo != 0) 
			return compareTo;

		compareTo = compareByName(name0, name1);
		if (compareTo != 0) 
			return compareTo;

		// finally by id
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

	/**
	 * @param description0
	 * @param description1
	 * @return
	 */
	private int compareByOrder(String description0, String description1) {
	    // By order
	    MatchResult order0 = ORDER.exec(description0);
	    MatchResult order1 = ORDER.exec(description1);
	    if ( order0 != null && order1 == null)
	    	return -1; 			//p0 < p1
	    if ( order1 != null && order0 == null )
	    	return 1;			//p0 > p1
	    if ( order1 == order0 ) {
		return 0;
	    }
	    return Integer.parseInt(order0.getGroup(1)) - Integer.parseInt(order1.getGroup(1));
	}

	private int compareByScope(Item<E> p0, Item<E> p1) {
	    Scope scope0 = p0.getScope();
	    Scope scope1 = p1.getScope();
	    if ( scope0 != null && scope1 == null )
	    	return -1;
	    if ( scope0 == null && scope1 != null )
	    	return 1;
	    return scope0 == scope1 ? 0 : scope1.ordinal()-scope0.ordinal();
	}

	private int compareByType(Item<E> p0, Item<E> p1) {
	    int compareTo;
	    E type0 = p0.getType();
	    E type1 = p1.getType();
	    if ( type0 != null && type1 == null )
	    	return 1;
	    if ( type0 == null && type1 != null )
	    	return -1;
	    compareTo = type0 == type1 ? 0 : type0.ordinal()-type1.ordinal();
	    return compareTo;
	}

	private int compareByName(String name0, String name1) {
	    int compareTo;
	    // By name
	    if (name0 != null && name1 == null )
	    	return 1;
	    if (name0 == null && name1 != null )
	    	return -1;
	    compareTo = name0 == name1 ? 0 : name0.compareTo(name1);
	    return compareTo;
	}


	private int compareByDescription(String description0, String description1) {
	    // By description
	    if ( description0 != null && description1 == null ) {
		return 1;
	    }
	    if ( description0 == null && description1 != null ) {
		return -1;
	    }
	    if ( description0 == null /* && description1 == null */ ) {
		return 0;
	    }

	    try {
		MatchResult matcher0 = ROMAN.exec(description0.trim());
		MatchResult matcher1 = ROMAN.exec(description1.trim());
		if (matcher0 != null && matcher1 != null) {
		    int roman0 = romanIntValue(matcher0.getGroup(1));
		    int roman1 = romanIntValue(matcher1.getGroup(1));
		    if (roman0 != roman1) {
			return roman0 - roman1;
		    }
		}
	    } catch (IllegalArgumentException e) {
		// Not roman numeral. Due a bug at 'ROMAN' regular expression.
		// 'ROMAN' matches empty strings and strings like '[1] SALARIO'
	    }

	    return  description0.compareTo(description1);
	}
}