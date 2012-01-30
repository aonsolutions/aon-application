package com.code.aon.fiscal.vat.tax;

import java.util.Comparator;

public class VatTaxKeyExComparator implements Comparator<VatTaxKeyEx>{

	public int compare(VatTaxKeyEx o1, VatTaxKeyEx o2) {
		int a = o1.getKey().ordinal();
		int b = o2.getKey().ordinal();
		if (a<b) return -1;
		if (a>b) return 1;
		double c = o1.getPercent();
		double d = o2.getPercent();
		if (c<d) return -1;
		if (c>d) return 1;
		return 0;
	}

}
