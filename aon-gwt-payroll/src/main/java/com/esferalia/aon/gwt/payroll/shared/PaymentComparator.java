package com.esferalia.aon.gwt.payroll.shared;

import java.util.Comparator;

import com.esferalia.aon.gwt.payroll.shared.Payment.Type;



public class PaymentComparator implements Comparator<Payment> {
	@Override
	public int compare(Payment arg0, Payment arg1) {
		Type type0 = arg0.getType();
		Type type1 = arg1.getType();

		if ( type0 != null && type1 == null )
			return 1;
		if ( type0 == null && type1 != null )
			return -1;
		
		int compareTo = type0 == type1 ? 0 : type0.compareTo(type1);
		if (compareTo != 0) {
			return compareTo;
		}
		String description0 = arg0.getDescription();
		String description1 = arg1.getDescription();
		if (description0 == null) {
			return description1 == null ? 0 : -1;
		}
		return description0.compareTo(description1);
	}
}