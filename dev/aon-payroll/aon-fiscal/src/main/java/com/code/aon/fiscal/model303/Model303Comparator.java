package com.code.aon.fiscal.model303;

import java.util.Comparator;

public class Model303Comparator implements Comparator<Model303>{

	public int compare(Model303 o1, Model303 o2) {
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
