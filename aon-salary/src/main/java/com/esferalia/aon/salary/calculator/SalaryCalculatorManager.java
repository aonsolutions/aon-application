package com.esferalia.aon.salary.calculator;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.salary.SalaryException;


public class SalaryCalculatorManager {

	private LinkedList<ISalaryCalculator> calculators = null;

	private static SalaryCalculatorManager instance;
	
	private SalaryCalculatorManager() {
		
	}
	
	public static SalaryCalculatorManager getInstance() {
		if (instance == null) {
			instance = new SalaryCalculatorManager();
		}
		return instance;
	}

	private List<ISalaryCalculator> getCalculators() {
		if (calculators == null) {
			calculators = new LinkedList<ISalaryCalculator>();
		}
		return calculators;
	}

	public void addCalculator( ISalaryCalculator calculator ) {
		getCalculators().add(calculator);
	}
	
	public ISalaryCalculator getCalculator( SalaryCalculatorContext ctx) throws SalaryException{
		try {
			for (ISalaryCalculator calculator: getCalculators()) {
				if (calculator.accept(ctx)) {
					ISalaryCalculator sc = calculator.getClass().newInstance();
					sc.initialize(ctx);
					return sc;  
				}
			}
			throw new SalaryException("No existe un Calculador adecuado a este contexto");
		} catch (InstantiationException e) {
			throw new SalaryException("No existe un Calculador adecuado a este contexto");
		} catch (IllegalAccessException e) {
			throw new SalaryException("No existe un Calculador adecuado a este contexto");
		}
	}

}
