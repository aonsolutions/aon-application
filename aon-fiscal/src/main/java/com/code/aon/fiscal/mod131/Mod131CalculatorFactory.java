package com.code.aon.fiscal.mod131;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.config.enumeration.Administration;

public class Mod131CalculatorFactory {
	
	List<IMod131Calculator> calculators;
	
	public Mod131CalculatorFactory() {
		calculators = new LinkedList<IMod131Calculator>();
		calculators.add(new Aeat2015Mod131Calculator());
		calculators.add(new Aeat2013Mod131Calculator());
	}
	
	public IMod131Calculator getCalculator(int year,Administration administration) {
		for (IMod131Calculator calculator :  calculators) {
			if (calculator.accept(year, administration)) {
				return calculator;
			}
		}
		return null;
	}
	
}
