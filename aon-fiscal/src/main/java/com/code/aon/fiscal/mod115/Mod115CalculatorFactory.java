package com.code.aon.fiscal.mod115;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.config.enumeration.Administration;

public class Mod115CalculatorFactory {
	
	List<IMod115Calculator> calculators;
	
	public Mod115CalculatorFactory() {
		calculators = new LinkedList<IMod115Calculator>();
		calculators.add(new Aeat2011Mod115Calculator());
		calculators.add(new Alava2011Mod115Calculator());
		calculators.add(new Bizkaia2011Mod115Calculator());
		calculators.add(new Gipuzkoa2011Mod115Calculator());
		calculators.add(new Navarra2011Mod115Calculator());
	}
	
	public IMod115Calculator getCalculator(int year,Administration administration) {
		for (IMod115Calculator calculator :  calculators) {
			if (calculator.accept(year, administration)) {
				return calculator;
			}
		}
		return null;
	}
	
}
