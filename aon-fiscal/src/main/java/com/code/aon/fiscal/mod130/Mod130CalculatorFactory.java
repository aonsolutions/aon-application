package com.code.aon.fiscal.mod130;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.config.enumeration.Administration;

public class Mod130CalculatorFactory {
	
	List<IMod130Calculator> calculators;
	
	public Mod130CalculatorFactory() {
		calculators = new LinkedList<IMod130Calculator>();
		calculators.add(new Aeat2015Mod130Calculator());
		calculators.add(new Aeat2011Mod130Calculator());
	}
	
	public IMod130Calculator getCalculator(int year,Administration administration) {
		for (IMod130Calculator calculator :  calculators) {
			if (calculator.accept(year, administration)) {
				return calculator;
			}
		}
		return null;
	}
	
}
