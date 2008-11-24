package com.code.aon.accounting.summary;

import java.util.Comparator;

public class ProfitAndLossComparator implements Comparator<Object[]> {

	public int compare(Object[] obj1, Object[] obj2) {
		String account1 = ((String) obj1[1]);
		String account2 = ((String) obj2[1]);

		if (account1.substring(0, 1).equals(account2.substring(0, 1))) {
			return account1.compareTo(account2);
		}
		return (account1.substring(0, 1).equals("7")) ? -1 : 1;
	}

}
