package com.code.aon.fiscal.vat.tax;

import java.util.Comparator;

import com.code.aon.fiscal.VatTaxDetail;

public class VatTaxDetailComparator implements Comparator<VatTaxDetail>{

	public int compare(VatTaxDetail o1, VatTaxDetail o2) {
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
