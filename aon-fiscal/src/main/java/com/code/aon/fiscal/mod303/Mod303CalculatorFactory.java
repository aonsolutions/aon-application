package com.code.aon.fiscal.mod303;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.config.enumeration.Administration;

public class Mod303CalculatorFactory {
	
	List<IMod303Calculator> calculators;
	
	public Mod303CalculatorFactory() {
		calculators = new LinkedList<IMod303Calculator>();
		calculators.add(new Aeat2017Mod303Calculator());
		calculators.add(new Aeat2014Mod303Calculator());
	}
	
	public IMod303Calculator getCalculator(int year,Administration administration) {
		for (IMod303Calculator calculator :  calculators) {
			if (calculator.accept(year, administration)) {
				return calculator;
			}
		}
		return null;
	}
	
}
