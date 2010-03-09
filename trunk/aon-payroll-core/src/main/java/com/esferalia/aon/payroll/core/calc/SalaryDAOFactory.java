package com.esferalia.aon.payroll.core.calc;

public class SalaryDAOFactory {

	private static SalaryDAOFactory instance;
	
	private SalaryDAOFactory() {
		
	}
	
	public static SalaryDAOFactory getInstance() {
		if (instance == null) {
			instance = new SalaryDAOFactory();
		}
		return instance;
	}
	
	public ISalaryDAO getSalaryDAO() {
		return null;
	}
	
	
}
