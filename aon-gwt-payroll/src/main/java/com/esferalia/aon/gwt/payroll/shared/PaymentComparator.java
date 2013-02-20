package com.esferalia.aon.gwt.payroll.shared;

import java.util.Comparator;



public class PaymentComparator implements Comparator<Payment> {
	@Override
	public int compare(Payment arg0, Payment arg1) {
		int compareTo = arg0.getType().compareTo(arg1.getType());
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