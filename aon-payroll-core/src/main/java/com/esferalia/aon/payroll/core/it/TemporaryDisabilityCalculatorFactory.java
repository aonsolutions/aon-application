package com.esferalia.aon.payroll.core.it;

import java.util.LinkedList;
import java.util.List;

public class TemporaryDisabilityCalculatorFactory {
	
	private static TemporaryDisabilityCalculatorFactory instance;
	private static List<ITemporaryDisabilityCalculator> calculators;
	
	private TemporaryDisabilityCalculatorFactory() {
		
	}
	
	public static TemporaryDisabilityCalculatorFactory getInstance() {
		if (instance == null) {
			instance = new TemporaryDisabilityCalculatorFactory();
		}
		return instance;
	}

	public static List<ITemporaryDisabilityCalculator> getCalculators() {
		if (calculators == null) {
			calculators = new LinkedList<ITemporaryDisabilityCalculator>();
		}
		return calculators;
	}

	public ITemporaryDisabilityCalculator getTemporaryDisabilityCalculator(
			ITemporalDisability td) {
		for (ITemporaryDisabilityCalculator calculator: getCalculators()) {
			if (calculator.accept(td)) {
				return calculator;
			}
		}
		throw new IllegalArgumentException("No existe un método de cálculo para este tipo de IT" );
	}


}
