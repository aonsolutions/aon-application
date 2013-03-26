package com.code.aon.fiscal.mod310;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.config.enumeration.Administration;

public class Mod310CalculatorFactory {
	
	List<IMod310Calculator> calculators;
	
	public Mod310CalculatorFactory() {
		calculators = new LinkedList<IMod310Calculator>();
		calculators.add(new Aeat2013Mod310Calculator());
	}
	
	public IMod310Calculator getCalculator(int year,Administration administration) {
		for (IMod310Calculator calculator :  calculators) {
			if (calculator.accept(year, administration)) {
				return calculator;
			}
		}
		return null;
	}
	
}
