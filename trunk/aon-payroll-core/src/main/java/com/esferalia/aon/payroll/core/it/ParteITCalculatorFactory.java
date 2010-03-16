package com.esferalia.aon.payroll.core.it;


import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.payroll.core.ITrabajo;

public class ParteITCalculatorFactory {
	
	private static ParteITCalculatorFactory instance;
	private static List<IParteITCalculator> calculators;
	
	private ParteITCalculatorFactory() {
		
	}
	
	public static ParteITCalculatorFactory getInstance() {
		if (instance == null) {
			instance = new ParteITCalculatorFactory();
		}
		return instance;
	}

	public static List<IParteITCalculator> getCalculators() {
		if (calculators == null) {
			calculators = new LinkedList<IParteITCalculator>();
		}
		return calculators;
	}

	public IParteITCalculator getParteITCalculator(
			IParteIT td,ITrabajo trabajo) {
		for (IParteITCalculator calculator: getCalculators()) {
			if (calculator.accept(td,trabajo)) {
				return calculator;
			}
		}
		throw new IllegalArgumentException("No existe un método de cálculo para este tipo de IT" );
	}
	
	public static void register( IParteITCalculator calculator) {
		if (calculators == null) {
			calculators = new LinkedList<IParteITCalculator>();
		}
		calculators.add(calculator);
	}

}
