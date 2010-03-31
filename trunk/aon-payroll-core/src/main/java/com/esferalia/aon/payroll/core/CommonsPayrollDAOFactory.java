package com.esferalia.aon.payroll.core;

public class CommonsPayrollDAOFactory {

	private static CommonsPayrollDAOFactory instance;
	private ICommonsPayrollDAO commonsPayrollDAO;

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
		return commonsPayrollDAO;
	}

	public static void register(ICommonsPayrollDAO commonsPayrollDAO) {
		CommonsPayrollDAOFactory.getInstance().setCommonsPayrollDAO(commonsPayrollDAO);
	}

	public void setCommonsPayrollDAO(ICommonsPayrollDAO commonsPayrollDAO) {
		this.commonsPayrollDAO = commonsPayrollDAO;
	}
}
