package com.esferalia.aon.salary.bonus;


import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.salary.SalaryException;

public class BonusesFactoryManager {

	private LinkedList<IBonusesFactory> factories = null;

	private static BonusesFactoryManager instance;
	
	private BonusesFactoryManager() {
	}

	public static BonusesFactoryManager getInstance() {
		if (instance == null) {
			instance = new BonusesFactoryManager();
		}
		return instance;
	}

	private List<IBonusesFactory> getFactories() {
		if (factories == null) {
			factories = new LinkedList<IBonusesFactory>();
		}
		return factories;
	}

	public void addFactory( IBonusesFactory factory ) {
		getFactories().add(factory);
	}
	
	public IBonusesFactory getFactory( IBonusesFactoryContext ctx) throws SalaryException{
		try {
			for (IBonusesFactory factory: getFactories()) {
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
