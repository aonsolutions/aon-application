package com.esferalia.aon.payroll.core.it;


public class ParteITDAOFactory {

	private static ParteITDAOFactory instance;
	private IParteITDAO parteITDAO;

	public static void register(IParteITDAO parteITDAO) {
		ParteITDAOFactory.getInstance().parteITDAO = parteITDAO;
	}

	private ParteITDAOFactory() {
	}
	
	public static ParteITDAOFactory getInstance() {
		if (instance == null) {
			instance = new ParteITDAOFactory();
		}
		return instance;
	}
	
	public IParteITDAO getParteITDAO() {
		if (parteITDAO == null) {
			throw new IllegalStateException("No hay un IParteITDAO registrado.");
		}
		return parteITDAO;
	}
	
	
}
