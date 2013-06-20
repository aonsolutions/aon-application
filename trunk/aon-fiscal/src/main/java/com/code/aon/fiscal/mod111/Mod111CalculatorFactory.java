package com.code.aon.fiscal.mod111;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.config.enumeration.Administration;

public class Mod111CalculatorFactory {
	
	List<IMod111Calculator> calculators;
	
	public Mod111CalculatorFactory() {
		calculators = new LinkedList<IMod111Calculator>();
		calculators.add(new Aeat2011Mod111Calculator());
		calculators.add(new Alava2011Mod111Calculator());
		calculators.add(new Bizkaia2011Mod111Calculator());
		calculators.add(new Gipuzkoa2011Mod111Calculator());
		calculators.add(new Navarra2011Mod111Calculator());
	}
	
	public IMod111Calculator getCalculator(int year,Administration administration) {
		for (IMod111Calculator calculator :  calculators) {
			if (calculator.accept(year, administration)) {
				return calculator;
			}
		}
		return null;
	}
	
}
