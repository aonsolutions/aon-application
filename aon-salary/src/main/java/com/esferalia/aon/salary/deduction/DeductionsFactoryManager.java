package com.esferalia.aon.salary.deduction;


import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.salary.SalaryException;

public class DeductionsFactoryManager {

	private LinkedList<IDeductionsFactory> factories = null;

	private static DeductionsFactoryManager instance;
	
	private DeductionsFactoryManager() {
	}

	public static DeductionsFactoryManager getInstance() {
		if (instance == null) {
			instance = new DeductionsFactoryManager();
		}
		return instance;
	}

	private List<IDeductionsFactory> getFactories() {
		if (factories == null) {
			factories = new LinkedList<IDeductionsFactory>();
		}
		return factories;
	}

	public void addFactory( IDeductionsFactory factory ) {
		getFactories().add(factory);
	}
	
	public IDeductionsFactory getFactory( IDeductionsFactoryContext ctx) throws SalaryException{
		try {
			for (IDeductionsFactory factory: getFactories()) {
				if (factory.accept(ctx)) {
					return factory.getClass().newInstance(); 
				}
			}
			throw new SalaryException("No existe una factoria adecuada a este contexto");
		} catch (InstantiationException e) {
			throw new SalaryException("No existe una factoria adecuada a este contexto");
		} catch (IllegalAccessException e) {
			throw new SalaryException("No existe una factoria adecuada a este contexto");
		}
	}

}
