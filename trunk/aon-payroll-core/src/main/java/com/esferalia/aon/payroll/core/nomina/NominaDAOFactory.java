package com.esferalia.aon.payroll.core.nomina;


public class NominaDAOFactory {

	private static NominaDAOFactory instance;
	private INominaDAO nominaDAO;

	public static void register(INominaDAO nominaDAO) {
		NominaDAOFactory.getInstance().nominaDAO = nominaDAO;
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
