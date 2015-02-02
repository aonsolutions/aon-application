package com.esferalia.aon.salary.cost;


import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.salary.SalaryException;

public class CostsFactoryManager {

	private LinkedList<ICostsFactory> factories = null;

	private static CostsFactoryManager instance;
	
	private CostsFactoryManager() {
	}

	public static CostsFactoryManager getInstance() {
		if (instance == null) {
			instance = new CostsFactoryManager();
		}
		return instance;
	}

	private List<ICostsFactory> getFactories() {
		if (factories == null) {
			factories = new LinkedList<ICostsFactory>();
		}
		return factories;
	}

	public void addFactory( ICostsFactory factory ) {
		getFactories().add(factory);
	}
	
	public ICostsFactory getFactory( ICostsFactoryContext ctx) throws SalaryException{
		try {
			for (ICostsFactory factory: getFactories()) {
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
