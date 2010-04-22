package com.esferalia.aon.payroll.core.commons;

public class CommonsPayrollDAOFactory {

	private static CommonsPayrollDAOFactory instance;
	private ICommonsPayrollDAO commonsPayrollDAO;
	private boolean configured;

	private CommonsPayrollDAOFactory() {

	}

	public static CommonsPayrollDAOFactory getInstance() {
		if (instance == null) {
			instance = new CommonsPayrollDAOFactory();
		}
		return instance;
	}

	public ICommonsPayrollDAO getCommonsPayrollDAO() {
		if (commonsPayrollDAO == null) {
			throw new IllegalStateException("No hay un ICommonsPayrollDAO registrado.");
		}
		if (! configured ) {
			commonsPayrollDAO.configure();
			configured = true;
		}	
		return commonsPayrollDAO;
	}

	public static void register(ICommonsPayrollDAO commonsPayrollDAO) {
		CommonsPayrollDAOFactory.getInstance().commonsPayrollDAO = commonsPayrollDAO;
	}

}
