package com.code.aon.fiscal.mod311;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.config.enumeration.Administration;

public class Mod311CalculatorFactory {
	
	List<IMod311Calculator> calculators;
	
	public Mod311CalculatorFactory() {
		calculators = new LinkedList<IMod311Calculator>();
		calculators.add(new Aeat2013Mod311Calculator());
	}
	
	public IMod311Calculator getCalculator(int year,Administration administration) {
		for (IMod311Calculator calculator :  calculators) {
			if (calculator.accept(year, administration)) {
				return calculator;
			}
		}
		return null;
	}
	
}
