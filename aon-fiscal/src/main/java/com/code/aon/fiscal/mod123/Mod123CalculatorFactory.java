package com.code.aon.fiscal.mod123;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.config.enumeration.Administration;

public class Mod123CalculatorFactory {
	
	List<IMod123Calculator> calculators;
	
	public Mod123CalculatorFactory() {
		calculators = new LinkedList<IMod123Calculator>();
		calculators.add(new Aeat2011Mod123Calculator());
		calculators.add(new Alava2011Mod123Calculator());
		calculators.add(new Bizkaia2011Mod123Calculator());
		calculators.add(new Gipuzkoa2011Mod123Calculator());
		calculators.add(new Navarra2011Mod123Calculator());
	}
	
	public IMod123Calculator getCalculator(int year,Administration administration) {
		for (IMod123Calculator calculator :  calculators) {
			if (calculator.accept(year, administration)) {
				return calculator;
			}
		}
		return null;
	}
	
}
