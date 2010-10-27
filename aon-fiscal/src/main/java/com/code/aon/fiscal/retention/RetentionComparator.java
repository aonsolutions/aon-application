package com.code.aon.fiscal.retention;

import java.util.Comparator;

public class RetentionComparator implements Comparator<Retention>{

	public int compare(Retention o1, Retention o2) {
		int a = o1.getWithholdingType().ordinal();
		int b = o2.getWithholdingType().ordinal();
		if (a<b) return -1;
		if (a>b) return 1;
		double c = o1.getPercent();
		double d = o2.getPercent();
		if (c<d) return -1;
		if (c>d) return 1;
		return 0;
	}

}
