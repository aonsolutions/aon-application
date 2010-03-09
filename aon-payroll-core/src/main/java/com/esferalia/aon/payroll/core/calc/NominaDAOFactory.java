package com.esferalia.aon.payroll.core.calc;

public class NominaDAOFactory {

	private static NominaDAOFactory instance;
	
	private NominaDAOFactory() {
		
	}
	
	public static NominaDAOFactory getInstance() {
		if (instance == null) {
			instance = new NominaDAOFactory();
		}
		return instance;
	}
	
	public INominaDAO getNominaDAO() {
		return null;
	}
	
	
}
