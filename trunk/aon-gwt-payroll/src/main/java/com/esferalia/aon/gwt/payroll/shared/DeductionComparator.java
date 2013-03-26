package com.esferalia.aon.gwt.payroll.shared;

import java.util.Comparator;

import com.esferalia.aon.gwt.payroll.shared.Deduction.Type;



public class DeductionComparator implements Comparator<Deduction> {
	public int compare(Deduction arg0, Deduction arg1) {
		Type type0 = arg0.getType();
		Type type1 = arg1.getType();

		if ( type0 != null && type1 == null )
			return 1;
		if ( type0 == null && type1 != null )
			return -1;

		int compareTo = type0 == type1 ? 0 : arg0.getType().compareTo(arg1.getType());
		if (compareTo != 0) {
			return compareTo;
		}
		String description0 = arg0.getDescription();
		String description1 = arg1.getDescription();
		if (description0 == null) {
			return description1 == null ? 0 : -1;
		}
		return description0.compareTo(description1);

	};
}