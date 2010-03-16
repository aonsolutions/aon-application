package com.esferalia.aon.payroll.core.calc;


public class NominaDAOFactory {

	private static NominaDAOFactory instance;
	private static INominaDAO nominaDAO;

	public static void setNominaDAO(INominaDAO nominaDAO) {
		NominaDAOFactory.nominaDAO = nominaDAO;
	}

	private NominaDAOFactory() {
	}
	
	public static NominaDAOFactory getInstance() {
		if (instance == null) {
			instance = new NominaDAOFactory();
		}
		return instance;
	}
	
	public INominaDAO getNominaDAO() {
		return nominaDAO;
	}
	
	
}
