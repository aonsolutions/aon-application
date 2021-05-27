package com.esferalia.aon.salary.payment;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.salary.SalaryException;

public class PaymentsFactoryManager {

	private List<IPaymentsFactory> factories = null;

	private static PaymentsFactoryManager instance;
	
	private PaymentsFactoryManager() {
	}

	public static PaymentsFactoryManager getInstance() {
		if (instance == null) {
			instance = new PaymentsFactoryManager();
		}
		return instance;
	}

	private List<IPaymentsFactory> getFactories() {
		if (factories == null) {
			factories = new LinkedList<IPaymentsFactory>();
		}
		return factories;
	}

	public void addFactory(IPaymentsFactory factory ) {
		getFactories().add(factory);
	}
	
	public IPaymentsFactory getFactory( IPaymentsFactoryContext ctx) throws SalaryException{
		try {
			for (IPaymentsFactory factory: getFactories()) {
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
