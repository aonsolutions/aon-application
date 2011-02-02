package com.code.aon.accounting.summary;

import java.util.Comparator;

public class SummaryComparator implements Comparator<Summary> {

	public int compare(Summary sum1, Summary sum2) {
		String account1 = sum1.getId();
		String account2 = sum2.getId();

		if (account1.substring(0, 1).equals(account2.substring(0, 1))) {
			return account1.compareTo(account2);
		}
		return (account1.substring(0, 1).equals("7")) ? -1 : 1;
	}

}
